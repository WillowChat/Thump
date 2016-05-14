package engineer.carrot.warren.thump.handler

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.command.irc.CommandPlayers
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.runner.IWrappersManager
import engineer.carrot.warren.warren.ChannelMessageEvent

class MessageHandler(private val manager: IWrappersManager) {

    fun onChannelMessage(event: ChannelMessageEvent) {
        val nick = event.user.nick

        if (manager.anyWrappersMatch(nick)) {
            return
        }

        var output = TokenHelper().addUserToken(nick).addChannelToken(event.channel.toString()).addMessageToken(event.message).applyTokens(Thump.configuration.formats.irc.channelMessage)

        if (Thump.configuration.general.logIrcToServerConsole) {
            LogHelper.info(output)
        }

        // TODO: Merge in to command system when available
        if (event.message.equals("!players")) {
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

//    @Subscribe
//    fun handleChannelAction(event: ChannelActionEvent) {
//        val user = event.fromUser.nameWithoutAccess
//
//        if (manager.usernameMatchesAnyConnection(user)) {
//            return
//        }
//
//        var output = TokenHelper().addUserToken(user).addChannelToken(event.channel.toString()).addMessageToken(event.contents).applyTokens(Thump.configuration.formats.irc.channelAction)
//
//        if (Thump.configuration.general.logIrcToServerConsole) {
//            LogHelper.info(output)
//        }
//
//        if (!Thump.configuration.events.irc.channelAction) {
//            return
//        }
//
//        output = StringHelper.stripBlacklistedIRCCharacters(output)
//
//        PlayerHelper.sendMessageToAllPlayers(output)
//    }
//
//    @Subscribe
//    fun handlePrivateMessage(event: PrivateMessageEvent) {
//        var output = "PM from " + event.fromUser + ": " + event.contents
//
//        if (!Thump.configuration.general.logIrcToServerConsole) {
//            return
//        }
//
//        output = StringHelper.stripBlacklistedIRCCharacters(output)
//
//        LogHelper.info(output)
//    }
//
//    @Subscribe
//    fun handlePrivateAction(event: PrivateActionEvent) {
//        var output = "PM ACTION from " + event.fromUser + ": " + event.contents
//
//        if (!Thump.configuration.general.logIrcToServerConsole) {
//            return
//        }
//
//        output = StringHelper.stripBlacklistedIRCCharacters(output)
//
//        LogHelper.info(output)
//    }
}
