package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.plugin.irc.IWrapper
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import engineer.carrot.warren.warren.event.ConnectionLifecycleEvent
import engineer.carrot.warren.warren.state.LifecycleState

class LifecycleHandler(private val wrapper: IWrapper, private val sink: IThumpMinecraftSink) {

    fun onConnectionLifecycleChanged(event: ConnectionLifecycleEvent) {
        val lifecycle = event.lifecycle
        when (lifecycle) {
            LifecycleState.CONNECTED -> {
                val output = TokenHelper().addMessageToken(wrapper.id).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.networkReady)

                sink.sendToAllPlayersWithoutCheckingSource(output)
            }

            LifecycleState.DISCONNECTED -> {
                val output = TokenHelper().addMessageToken(wrapper.id).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.networkDisconnected)

                sink.sendToAllPlayersWithoutCheckingSource(output)
            }

            else -> {
                LogHelper.info("New IRC lifecycle: $lifecycle")
            }
        }
    }

}
