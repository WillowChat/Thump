package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.api.IServiceChatFormatter
import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.plugin.irc.IWrapper
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import engineer.carrot.warren.thump.plugin.irc.command.chat.CommandPlayers
import chat.willow.warren.event.ChannelActionEvent
import chat.willow.warren.event.ChannelMessageEvent
import chat.willow.warren.event.PrivateActionEvent
import chat.willow.warren.event.PrivateMessageEvent

class MessageHandler(private val sink: IThumpMinecraftSink, private val wrapper: IWrapper, private val formatter: IServiceChatFormatter) {

    fun onChannelMessage(event: ChannelMessageEvent) {
        val nick = event.user.nick

        if (shouldIgnore(nick)) {
            LogHelper.debug("ignoring channel message from $nick as they're ignored for connection ${wrapper.id}")
            return
        }

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.name).addMessageToken(event.message).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.channelMessage)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (event.message == "${Thump.configuration.commands.prefix}players") {
            if (Thump.configuration.commands.players) {
                CommandPlayers.handlePlayersCommand(Thump)
            }
        }

        if (!IrcServicePlugin.configuration.events.channelMessage) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)
        val formattedOutput = formatter.format(output)

        sink.sendToAllPlayers(nick, formattedOutput)
    }

    fun onChannelAction(event: ChannelActionEvent) {
        val nick = event.user.nick

        if (shouldIgnore(nick)) {
            LogHelper.debug("ignoring channel action from $nick as they're ignored for connection ${wrapper.id}")
            return
        }

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.name).addMessageToken(event.message).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.channelAction)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (!IrcServicePlugin.configuration.events.channelAction) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)
        val formattedOutput = formatter.format(output)

        sink.sendToAllPlayers(nick, formattedOutput)
    }

    fun onPrivateMessage(event: PrivateMessageEvent) {
        val nick = event.user.nick

        var output = "PM from " + nick + ": " + event.message

        if (shouldIgnore(nick)) {
            LogHelper.debug("ignoring private message from $nick as they're ignored for connection ${wrapper.id}")
            return
        }

        if (!IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }

    fun onPrivateAction(event: PrivateActionEvent) {
        val nick = event.user.nick

        if (shouldIgnore(nick)) {
            LogHelper.debug("ignoring private action from $nick as they're ignored for connection ${wrapper.id}")
            return
        }

        var output = "PM ACTION from " + event.user.nick + ": " + event.message

        if (!IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }

    private fun shouldIgnore(nick: String): Boolean {
        val caseMapping = wrapper.ircState?.parsing?.caseMapping?.mapping
        if (caseMapping != null) {
            val lowercaseNick = caseMapping.toLower(nick)

            val connectionConfiguration = IrcServicePlugin.configuration.connections.servers[wrapper.id]
            if (connectionConfiguration != null && connectionConfiguration.ignoredNicks.any { caseMapping.toLower(it) == lowercaseNick }) {
                return true
            }
        }

        return false
    }
}
