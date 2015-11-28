package engineer.carrot.warren.thump.connection;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.event.MotdEvent;

public class ConnectionStateListener {
    private ConnectionWrapper wrapper;

    public ConnectionStateListener(ConnectionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Subscribe
    public void onServerConnected(MotdEvent event) {
        if (this.wrapper.getConnectionState() != ConnectionState.CONNECTING) {
            LogHelper.error("Connection got a 'connected' event, but doesn't know it's 'connecting'");
            return;
        }

        this.wrapper.resetReconnectCounter();
        this.wrapper.setConnectionState(ConnectionState.CONNECTED);
        LogHelper.info("Connected '{}' successfully!", wrapper.getId());
    }
}
