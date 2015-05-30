package engineer.carrot.warren.thump.connection;

import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.listener.ServerEventListener;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.ChannelManager;
import engineer.carrot.warren.warren.IRCServerConnection;
import engineer.carrot.warren.warren.irc.Channel;
import joptsimple.internal.Strings;

import java.util.List;
import java.util.Map;

public class ConnectionWrapper implements Runnable {
    private static final String LOGIN = "thumpBridge";

    private String id;
    private ConnectionState connectionState;
    private IRCServerConnection connection;

    private final Object connectionLock;

    public ConnectionWrapper(String id, ServerConfiguration configuration, List<Object> listeners) {
        this.id = id;
        this.connectionLock = new Object();

        this.initialiseFromConfiguration(configuration);
        this.registerInternalListeners();
        this.registerExternalListeners(listeners);

        this.connectionState = ConnectionState.DISCONNECTED;
    }

    public String getId() {
        return this.id;
    }

    private void initialiseFromConfiguration(ServerConfiguration configuration) {
        this.connection = new IRCServerConnection(configuration.server, configuration.port, configuration.nickname, LOGIN);
        this.connection.setSocketShouldUsePlaintext(!configuration.useTLS);

        if (configuration.identifyWithNickServ) {
            connection.setNickservPassword(configuration.nickServPassword);
        }

        if (!configuration.channels.isEmpty()) {
            connection.setAutoJoinChannels(Lists.newArrayList(configuration.channels));
        }

        if (configuration.forceAcceptCertificates) {
            if (!configuration.forciblyAcceptedCertificates.isEmpty()) {
                connection.setForciblyAcceptedCertificates(configuration.forciblyAcceptedCertificates);
            }
        }
    }

    private void registerInternalListeners() {
        this.connection.registerListener(new ConnectionStateListener(this));
        this.connection.registerListener(new ServerEventListener(id));
    }

    private void registerExternalListeners(List<Object> listeners) {
        for (Object listener : listeners) {
            this.connection.registerListener(listener);
        }
    }

    public ConnectionState getConnectionState() {
        synchronized (this.connectionLock) {
            return this.connectionState;
        }
    }

    public void setConnectionState(ConnectionState connectionState) {
        synchronized (this.connectionLock) {
            this.connectionState = connectionState;
        }
    }

    public void stop() {
        if (this.getConnectionState() == ConnectionState.DISCONNECTING) {
            LogHelper.error("Already disconnecting connection '{}'", id);
            return;
        }

        this.setConnectionState(ConnectionState.DISCONNECTING);

        synchronized (this.connectionLock) {
            this.connection.disconnect();
        }
    }

    public void sendMessageToAllChannels(String message) {
        if (this.getConnectionState() != ConnectionState.CONNECTED) {
            LogHelper.error("Connection '{}' is not ready to send messages yet: {}", id, message);
            return;
        }

        synchronized (this.connectionLock) {
            ChannelManager channelManager = this.connection.getJoinedChannels();

            Map<String, Channel> channels = channelManager.getAllChannels();

            if (channels.isEmpty()) {
                LogHelper.warn("Message had nowhere to go because the bridge doesn't think it's in any channels yet: {}", id, message);
                return;
            }

            for (String sChannel : channels.keySet()) {
                Channel channel = channels.get(sChannel);
                this.connection.sendMessageToChannel(channel, message);
            }

            LogHelper.info("Sent message to channels '{}': {}", Strings.join(Lists.newArrayList(channels.keySet()), ","), message);
        }
    }

    public String getUsername() {
        synchronized (this.connectionLock) {
            return this.connection.getBotNickname();
        }
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
