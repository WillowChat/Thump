package engineer.carrot.warren.thump;

import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.ChannelManager;
import engineer.carrot.warren.warren.IRCServerConnection;
import engineer.carrot.warren.warren.irc.Channel;
import joptsimple.internal.Strings;

import java.util.List;
import java.util.Map;

public class ConnectionWrapper implements Runnable {
    private String id;
    private ConnectionState connectionState;
    private IRCServerConnection connection;

    public ConnectionWrapper(String id, ServerConfiguration configuration, List<Object> listeners) {
        this.id = id;

        this.initialiseFromConfiguration(configuration);
        this.registerInternalListeners();
        this.registerExternalListeners(listeners);

        this.connectionState = ConnectionState.DISCONNECTED;
    }

    public String getId() {
        return this.id;
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

        this.connection.registerListener(new ConnectionStateListener(this));
    }

    private void registerExternalListeners(List<Object> listeners) {
        for (Object listener : listeners) {
            this.connection.registerListener(listener);
        }
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    public void setConnectionState(ConnectionState connectionState) {
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

    public void sendMessageToAllChannels(String message) {
        if (this.getConnectionState() != ConnectionState.CONNECTED) {
            LogHelper.error("Connection '{}' is not ready to send messages yet: {}", id, message);
            return;
        }

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

        LogHelper.info("Sent message to channels '{}': {}", Strings.join(Lists.newArrayList(channels.keySet()), ","));
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
