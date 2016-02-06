package engineer.carrot.warren.thump.listener

import com.google.common.eventbus.Subscribe
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.warren.event.MotdEvent
import engineer.carrot.warren.warren.event.ServerDisconnectedEvent

@Suppress("UNUSED", "UNUSED_PARAMETER")
class ServerEventListener(private val id: String) {

    @Subscribe
    fun onConnectedToServer(event: MotdEvent) {
        val output = TokenHelper().addMessageToken(this.id).applyTokens(Thump.configuration.formats.irc.networkReady)
        PlayerHelper.sendMessageToAllPlayers(output)
    }

    @Subscribe
    fun onDisconnectedFromServer(event: ServerDisconnectedEvent) {
        val output = TokenHelper().addMessageToken(this.id).applyTokens(Thump.configuration.formats.irc.networkDisconnected)
        PlayerHelper.sendMessageToAllPlayers(output)
    }
}
