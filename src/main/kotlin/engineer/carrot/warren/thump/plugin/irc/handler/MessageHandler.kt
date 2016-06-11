package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.IThumpServicePlugins
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.plugin.irc.command.CommandPlayers
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import engineer.carrot.warren.warren.event.ChannelActionEvent
import engineer.carrot.warren.warren.event.ChannelMessageEvent
import engineer.carrot.warren.warren.event.PrivateActionEvent
import engineer.carrot.warren.warren.event.PrivateMessageEvent

class MessageHandler(private val servicePlugins: IThumpServicePlugins) {

    fun onChannelMessage(event: ChannelMessageEvent) {
        val nick = event.user.nick

        if (servicePlugins.anyServicesMatch(nick)) {
            return
        }

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.toString()).addMessageToken(event.message).applyTokens(IrcServicePlugin.configuration.formats.channelMessage)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (event.message.equals("!players")) {
            if (Thump.configuration.commands.players) {
                CommandPlayers.handlePlayersCommand(servicePlugins)
            }
        }

        if (!IrcServicePlugin.configuration.events.channelMessage) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        PlayerHelper.sendMessageToAllPlayers(output)
    }

    fun onChannelAction(event: ChannelActionEvent) {
        val nick = event.user.nick

        if (servicePlugins.anyServicesMatch(nick)) {
            return
        }

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.toString()).addMessageToken(event.message).applyTokens(IrcServicePlugin.configuration.formats.channelAction)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (!IrcServicePlugin.configuration.events.channelAction) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        PlayerHelper.sendMessageToAllPlayers(output)
    }

    fun onPrivateMessage(event: PrivateMessageEvent) {
        var output = "PM from " + event.user.nick + ": " + event.message

        if (!IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }

    fun onPrivateAction(event: PrivateActionEvent) {
        var output = "PM ACTION from " + event.user.nick + ": " + event.message

        if (!IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }
}
