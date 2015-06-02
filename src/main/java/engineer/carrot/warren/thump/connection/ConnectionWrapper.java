package engineer.carrot.warren.thump.connection;

import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.listener.ServerEventListener;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.ChannelManager;
import engineer.carrot.warren.warren.IRCConnection;
import engineer.carrot.warren.warren.irc.Channel;
import joptsimple.internal.Strings;

import java.util.List;
import java.util.Map;

public class ConnectionWrapper implements Runnable {
    private static final String LOGIN = "thumpBridge";

    private String id;
    private ConnectionState connectionState;
    private IRCConnection connection;
    private ReconnectPolicy reconnectPolicy;

    private final Object connectionLock;

    public ConnectionWrapper(String id, ServerConfiguration configuration, List<Object> listeners) {
        this.id = id;
        this.connectionLock = new Object();

        this.initialiseFromConfiguration(configuration, listeners);

        this.connectionState = ConnectionState.DISCONNECTED;
    }

    public String getId() {
        return this.id;
    }

    private void initialiseFromConfiguration(ServerConfiguration configuration, List<Object> listeners) {
        IRCConnection.Builder builder = new IRCConnection.Builder()
                .server(configuration.server)
                .port(configuration.port)
                .nickname(configuration.nickname)
                .login(LOGIN)
                .plaintext(!configuration.useTLS);

        builder.channels(Lists.newArrayList(configuration.channels));

        if (configuration.identifyWithNickServ) {
            builder.nickservPassword(configuration.nickServPassword);
        }

        if (configuration.forceAcceptCertificates) {
            builder.fingerprints(configuration.forciblyAcceptedCertificates);
        }

        this.registerInternalListeners(builder);
        this.registerExternalListeners(builder, listeners);

        this.connection = builder.build();

        this.reconnectPolicy = new ReconnectPolicy(configuration);
    }

    private void registerInternalListeners(IRCConnection.Builder builder) {
        builder.listener(new ConnectionStateListener(this));
        builder.listener(new ServerEventListener(id));
    }

    private void registerExternalListeners(IRCConnection.Builder builder, List<Object> listeners) {
        for (Object listener : listeners) {
            builder.listener(listener);
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

    public void resetReconnectCounter() {
        this.reconnectPolicy.resetCurrentConnectionAttempts();
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

    public void disableNextReconnect() {
        this.reconnectPolicy.disableNextReconnect();
    }

    // Runnable

    @Override
    public void run() {
        this.reconnectPolicy.resetCurrentConnectionAttempts();

        while (true) {
            this.setConnectionState(ConnectionState.CONNECTING);
            this.connection.connect();
            this.setConnectionState(ConnectionState.DISCONNECTED);

            if (!this.reconnectPolicy.shouldReconnect() || this.reconnectPolicy.getIsDisabled()) {
                break;
            }

            this.reconnectPolicy.incrementConnectionAttempt();

            if (this.reconnectPolicy.getCurrentConnectionAttempt() > this.reconnectPolicy.getMaxConsecutiveReconnects()) {
                break;
            }

            this.setConnectionState(ConnectionState.WAITING);
            int delay = this.reconnectPolicy.getReconnectDelaySeconds();
            try {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }

                Thread.sleep(delay * 1000);
            } catch (InterruptedException e) {
                if (this.reconnectPolicy.getIsDisabled()) {
                    break;
                }
            }
        }

        this.setConnectionState(ConnectionState.DISCONNECTED);
    }
}
