package engineer.carrot.warren.thump.connection;

import com.google.common.collect.Maps;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.util.helper.LogHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectionManager {
    private Map<String, ConnectionWrapper> connectionMap;
    private Map<String, Thread> connectionThreads;

    public ConnectionManager() {
        this.connectionThreads = Maps.newHashMap();
        this.connectionMap = Maps.newHashMap();
    }

    public boolean addNewConnection(ServerConfiguration configuration, List<Object> listeners) {
        String id = configuration.ID;

        if (this.connectionMap.containsKey(id)) {
            return false;
        }

        this.connectionMap.put(id, new ConnectionWrapper(id, configuration, listeners));
        return true;
    }

    @Nullable
    public ConnectionState getConnectionState(String id) {
        if (!this.connectionMap.containsKey(id)) {
            return null;
        }

        return this.connectionMap.get(id).getConnectionState();
    }

    public boolean startConnection(String id) {
        if (!this.connectionMap.containsKey(id)) {
            LogHelper.error("Tried to start connection '{}' which does not exist yet", id);
            return false;
        }

        if (this.connectionThreads.containsKey(id) && this.getConnectionState(id) != ConnectionState.DISCONNECTED) {
            LogHelper.error("Tried to start connection '{}' which already has a thread", id);
            return false;
        }

        ConnectionWrapper wrapper = this.connectionMap.get(id);
        if (wrapper.getConnectionState() != ConnectionState.DISCONNECTED) {
            LogHelper.error("Tried to start connection '{}' which has already been started", id);
            return false;
        }

        Thread connectionThread = new Thread(wrapper);
        this.connectionThreads.put(id, connectionThread);
        connectionThread.start();

        LogHelper.info("Created and started new thread for connection '{}'", id);
        return true;
    }

    public void stopAllConnections() {
        LogHelper.info("Stopping all connections...");

        for (String id : this.getAllConnections()) {
            this.stopConnection(id);
        }
    }

    public boolean stopConnection(String id) {
        if (!this.connectionMap.containsKey(id)) {
            LogHelper.error("Tried to stop connection '{}' which does not exist yet", id);
            return false;
        }

        if (!this.connectionThreads.containsKey(id)) {
            LogHelper.error("Tried to stop connection '{}' which doesn't have a thread yet", id);
            return false;
        }

        Thread thread = this.connectionThreads.get(id);
        thread.interrupt();

        try {
            LogHelper.info("Waiting for connection thread '{}' to finish...", id);
            thread.join();
        } catch (InterruptedException e) {

        }

        this.connectionThreads.remove(id);
        LogHelper.info("Stopped connection '{}'", id);

        return true;
    }

    public Set<String> getAllConnections() {
        return this.connectionMap.keySet();
    }

    public void sendMessageToAllChannels(String message) {
        for (String id : this.getAllConnections()) {
            ConnectionWrapper wrapper = this.connectionMap.get(id);
            if (wrapper.getConnectionState() != ConnectionState.CONNECTED) {
                LogHelper.warn("Not sending message to '{}' as it is not connected yet: {}", id, message);
                continue;
            }

            wrapper.sendMessageToAllChannels(message);
        }
    }

    public boolean usernameMatchesAnyConnection(String username) {
        for (String id : this.getAllConnections()) {
            ConnectionWrapper wrapper = this.connectionMap.get(id);
            if (username.equalsIgnoreCase(wrapper.getUsername())) {
                return true;
            }
        }

        return false;
    }
}
