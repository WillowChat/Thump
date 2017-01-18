package chat.willow.thump.plugin.irc.handler

import chat.willow.thump.api.IThumpMinecraftSink
import chat.willow.thump.helper.LogHelper
import chat.willow.thump.helper.TokenHelper
import chat.willow.thump.plugin.irc.IWrapper
import chat.willow.thump.plugin.irc.IrcServicePlugin
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
