package engineer.carrot.warren.thump.listener

import com.google.common.eventbus.Subscribe
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.command.irc.CommandPlayers
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.warren.event.ChannelActionEvent
import engineer.carrot.warren.warren.event.ChannelMessageEvent
import engineer.carrot.warren.warren.event.PrivateActionEvent
import engineer.carrot.warren.warren.event.PrivateMessageEvent

class MessageListener(private val manager: ConnectionManager) {

    @Subscribe
    fun handleChannelMessage(event: ChannelMessageEvent) {
        val user = event.fromUser.nameWithoutAccess

        if (manager.usernameMatchesAnyConnection(user)) {
            return
        }

        var output = TokenHelper().addUserToken(user).addChannelToken(event.channel.toString()).addMessageToken(event.contents).applyTokens(Thump.configuration.formats.irc.channelMessage)

        if (Thump.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        // TODO: Merge in to command system when available
        if (event.contents.equals("!players")) {
            if (Thump.configuration.commands.players) {
                CommandPlayers.handlePlayersCommand(manager)
            }
        }

        if (!Thump.configuration.events.irc.channelMessage) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        PlayerHelper.sendMessageToAllPlayers(output)
    }

    @Subscribe
    fun handleChannelAction(event: ChannelActionEvent) {
        val user = event.fromUser.nameWithoutAccess

        if (manager.usernameMatchesAnyConnection(user)) {
            return
        }

        var output = TokenHelper().addUserToken(user).addChannelToken(event.channel.toString()).addMessageToken(event.contents).applyTokens(Thump.configuration.formats.irc.channelAction)

        if (Thump.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        if (!Thump.configuration.events.irc.channelAction) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        PlayerHelper.sendMessageToAllPlayers(output)
    }

    @Subscribe
    fun handlePrivateMessage(event: PrivateMessageEvent) {
        var output = "PM from " + event.fromUser + ": " + event.contents

        if (!Thump.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }

    @Subscribe
    fun handlePrivateAction(event: PrivateActionEvent) {
        var output = "PM ACTION from " + event.fromUser + ": " + event.contents

        if (!Thump.configuration.general.logIrcToServerConsole) {
            return
        }

        output = StringHelper.stripBlacklistedIRCCharacters(output)

        LogHelper.info(output)
    }
}
