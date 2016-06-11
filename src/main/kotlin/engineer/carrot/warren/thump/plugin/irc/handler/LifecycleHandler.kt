package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.warren.event.ConnectionLifecycleEvent
import engineer.carrot.warren.warren.state.LifecycleState

class LifecycleHandler(private val id: String) {

    fun onConnectionLifecycleChanged(event: ConnectionLifecycleEvent) {
        val lifecycle = event.lifecycle
        when (lifecycle) {
            LifecycleState.CONNECTED -> {
                val output = TokenHelper().addMessageToken(this.id).applyTokens(Thump.configuration.formats.irc.networkReady)
                // fixme: route through plugins
                PlayerHelper.sendMessageToAllPlayers(output)
            }

            LifecycleState.DISCONNECTED -> {
                val output = TokenHelper().addMessageToken(this.id).applyTokens(Thump.configuration.formats.irc.networkDisconnected)
                PlayerHelper.sendMessageToAllPlayers(output)
            }

            else -> {
                LogHelper.info("New IRC lifecycle: $lifecycle")
            }
        }
    }

}
