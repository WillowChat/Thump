package engineer.carrot.warren.thump.connection

import com.google.common.eventbus.Subscribe
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.warren.event.MotdEvent

class ConnectionStateListener(private val wrapper: ConnectionWrapper) {

    @Subscribe
    fun onServerConnected(event: MotdEvent) {
        if (this.wrapper.getConnectionState() != ConnectionState.CONNECTING) {
            LogHelper.error("Connection got a 'connected' event, but doesn't know it's 'connecting'")
            return
        }

        this.wrapper.resetReconnectCounter()
        this.wrapper.setConnectionState(ConnectionState.CONNECTED)
        LogHelper.info("Connected '{}' successfully!", wrapper.id)
    }
}
