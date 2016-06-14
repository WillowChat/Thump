package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import engineer.carrot.warren.thump.plugin.irc.command.chat.CommandPlayers
import engineer.carrot.warren.warren.event.ChannelActionEvent
import engineer.carrot.warren.warren.event.ChannelMessageEvent
import engineer.carrot.warren.warren.event.PrivateActionEvent
import engineer.carrot.warren.warren.event.PrivateMessageEvent

class MessageHandler(private val sink: IThumpMinecraftSink) {

    fun onChannelMessage(event: ChannelMessageEvent) {
        val nick = event.user.nick

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.toString()).addMessageToken(event.message).applyTokens(IrcServicePlugin.configuration.formats.channelMessage)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (event.message.equals("!players")) {
            if (Thump.configuration.commands.players) {
                CommandPlayers.handlePlayersCommand(Thump)
            }
        }

        if (!IrcServicePlugin.configuration.events.channelMessage) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        sink.sendToAllPlayers(nick, output)
    }

    fun onChannelAction(event: ChannelActionEvent) {
        val nick = event.user.nick

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.toString()).addMessageToken(event.message).applyTokens(IrcServicePlugin.configuration.formats.channelAction)

        if (IrcServicePlugin.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (!IrcServicePlugin.configuration.events.channelAction) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        sink.sendToAllPlayers(nick, output)
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
