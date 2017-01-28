package engineer.carrot.warren.thump.plugin.irc.handler

import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import engineer.carrot.warren.thump.plugin.irc.IWrapper
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import chat.willow.warren.event.ConnectionLifecycleEvent
import chat.willow.warren.state.LifecycleState
import net.minecraft.util.text.TextComponentString

class LifecycleHandler(private val wrapper: IWrapper, private val sink: IThumpMinecraftSink) {

    fun onConnectionLifecycleChanged(event: ConnectionLifecycleEvent) {
        val lifecycle = event.lifecycle
        when (lifecycle) {
            LifecycleState.CONNECTED -> {
                val output = TokenHelper().addMessageToken(wrapper.id).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.networkReady)
                val text = TextComponentString(output)

                sink.sendToAllPlayersWithoutCheckingSource(text)
            }

            LifecycleState.DISCONNECTED -> {
                val output = TokenHelper().addMessageToken(wrapper.id).addServerToken(wrapper.server).applyTokens(IrcServicePlugin.configuration.formats.networkDisconnected)
                val text = TextComponentString(output)

                sink.sendToAllPlayersWithoutCheckingSource(text)
            }

            else -> {
                LogHelper.info("New IRC lifecycle: $lifecycle")
            }
        }
    }

}
