package engineer.carrot.warren.thump;

import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.IRCServerConnection;

import java.util.List;

public class ConnectionWrapper implements Runnable {
    private String id;
    private ConnectionState connectionState;
    private IRCServerConnection connection;

    public ConnectionWrapper(String id, ServerConfiguration configuration, List<Object> listeners) {
        this.id = id;

        this.initialiseFromConfiguration(configuration);
        this.registerExternalListeners(listeners);

        this.connectionState = ConnectionState.DISCONNECTED;
    }

    private void initialiseFromConfiguration(ServerConfiguration configuration) {
        this.connection = new IRCServerConnection(configuration.server, configuration.port, configuration.nickname);
        if (configuration.identifyWithNickServ) {
            connection.setNickservPassword(configuration.nickServPassword);
        }

        if (!configuration.autoJoinChannels.isEmpty()) {
            connection.setAutoJoinChannels(configuration.autoJoinChannels);
        }

        if (configuration.forceAcceptCertificates) {
            if (!configuration.forciblyAcceptedCertificates.isEmpty()) {
                connection.setForciblyAcceptedCertificates(configuration.forciblyAcceptedCertificates);
            }
        }
    }

    private void registerInternalListeners() {
        // Must listen for:
        //  ServerConnectedEvent
        //  ServerDisconnectedEvent
    }

    private void registerExternalListeners(List<Object> listeners) {
        for (Object listener : listeners) {
            this.connection.registerListener(listener);
        }
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    private void setConnectionState(ConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public void stop() {
        if (this.getConnectionState() == ConnectionState.DISCONNECTING) {
            LogHelper.error("Already disconnecting connection '{}'", id);
            return;
        }

        this.setConnectionState(ConnectionState.DISCONNECTING);
        this.connection.disconnect();
    }

    // Runnable

    @Override
    public void run() {
        this.setConnectionState(ConnectionState.CONNECTING);

        LogHelper.info("Entering blocking section of thread '{}'", id);
        this.connection.connect();
        LogHelper.info("Exiting blocking section of thread '{}'", id);

        this.setConnectionState(ConnectionState.DISCONNECTED);
    }
}
