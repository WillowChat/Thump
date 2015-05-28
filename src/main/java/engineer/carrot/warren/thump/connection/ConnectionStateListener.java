package engineer.carrot.warren.thump.connection;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.event.EndOfMOTDEvent;
import engineer.carrot.warren.warren.event.ServerDisconnectedEvent;

public class ConnectionStateListener {
    private ConnectionWrapper wrapper;

    public ConnectionStateListener(ConnectionWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Subscribe
    public void onServerConnected(EndOfMOTDEvent event) {
        if (this.wrapper.getConnectionState() != ConnectionState.CONNECTING) {
            LogHelper.error("Connection got a 'connected' event, but doesn't know it's 'connecting'");
            return;
        }

        LogHelper.info("Connected '{}' successfully!", wrapper.getId());
        this.wrapper.setConnectionState(ConnectionState.CONNECTED);
    }
}
