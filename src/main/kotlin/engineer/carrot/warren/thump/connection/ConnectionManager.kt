package engineer.carrot.warren.thump.connection

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import engineer.carrot.warren.thump.config.GeneralConfiguration
import engineer.carrot.warren.thump.config.ServerConfiguration
import engineer.carrot.warren.thump.helper.LogHelper

class ConnectionManager {
    private val connectionMap: MutableMap<String, ConnectionWrapper>
    private val connectionThreads: MutableMap<String, Thread>

    init {
        this.connectionThreads = Maps.newHashMap<String, Thread>()
        this.connectionMap = Maps.newHashMap<String, ConnectionWrapper>()
    }

    fun addNewConnection(serverConfiguration: ServerConfiguration, generalConfiguration: GeneralConfiguration, listeners: List<Any>): Boolean {
        val id = serverConfiguration.ID

        if (this.connectionMap.containsKey(id)) {
            return false
        }

        this.connectionMap.put(id, ConnectionWrapper(id, serverConfiguration, generalConfiguration, listeners))
        return true
    }

    fun removeConnection(id: String): Boolean {
        if (!this.connectionMap.containsKey(id)) {
            LogHelper.warn("Tried to remove connection '{}', which does not exist", id)

            return false
        }

        if (this.getConnectionState(id) != ConnectionState.DISCONNECTED) {
            LogHelper.warn("Tried to remove connection '{}' but it is not DISCONNECTED yet", id)

            return false
        }

        this.connectionMap.remove(id)
        LogHelper.info("Removed connection '{}' successfully", id)
        return true
    }

    fun getConnectionState(id: String): ConnectionState? {
        if (!this.connectionMap.containsKey(id)) {
            return null
        }

        return this.connectionMap[id]?.getConnectionState()
    }

    fun startConnection(id: String): Boolean {
        if (!this.connectionMap.containsKey(id)) {
            LogHelper.error("Tried to start connection '{}' which does not exist yet", id)
            return false
        }

        val wrapper = this.connectionMap[id]
        if (wrapper == null) {
            LogHelper.error("Failed to find connection '{}' in tracked connections - this is a bug!", id)

            return false
        }

        if (wrapper.getConnectionState() == ConnectionState.WAITING) {
            LogHelper.info("Tried to start connection '{}' which is already waiting to reconnect. Reconnecting now...", id)
            this.connectionThreads[id]?.interrupt()

            return true
        }

        if (this.connectionThreads.containsKey(id) && this.getConnectionState(id) != ConnectionState.DISCONNECTED) {
            LogHelper.error("Tried to start connection '{}' which already has a thread", id)
            return false
        }

        if (wrapper.getConnectionState() != ConnectionState.DISCONNECTED) {
            LogHelper.error("Tried to start connection '{}' which has already been started", id)
            return false
        }

        val connectionThread = Thread(wrapper)
        this.connectionThreads.put(id, connectionThread)
        connectionThread.setUncaughtExceptionHandler { thread, throwable ->
            LogHelper.info("Uncaught exception from Warren thread - disconnecting: ", throwable)

            stopConnection(id)
        }

        connectionThread.start()

        LogHelper.info("Created and started new thread for connection '{}'", id)
        return true
    }

    fun stopAllConnections() {
        LogHelper.info("Stopping all connections...")

        for (id in this.allConnections) {
            this.stopConnection(id)
        }
    }

    fun removeAllConnections(): Boolean {
        LogHelper.info("Removing all connections...")

        var successful = true
        for (id in this.allConnections) {
            successful = successful && this.removeConnection(id)
        }

        return successful
    }

    fun stopConnection(id: String): Boolean {
        if (!this.connectionMap.containsKey(id)) {
            LogHelper.error("Tried to stop connection '{}' which does not exist yet", id)
            return false
        }

        if (!this.connectionThreads.containsKey(id)) {
            LogHelper.error("Tried to stop connection '{}' which doesn't have a thread yet", id)
            return false
        }

        val wrapper = this.connectionMap[id]
        if (wrapper == null) {
            LogHelper.error("Failed to find connection '{}' in tracked connections - this is a bug!", id)

            return false
        }

        wrapper.disableNextReconnect()

        val thread = this.connectionThreads[id]
        if (thread == null) {
            LogHelper.error("Failed to find thread '{}' in tracked threads - this is a bug!", id)

            return false
        }
        if (thread.isAlive) {
            thread.interrupt()

            try {
                LogHelper.info("Waiting for connection thread '{}' to finish...", id)
                thread.join()
            } catch (e: InterruptedException) {

            }

        } else {
            LogHelper.info("Thread '{}' did not appear to be alive, not waiting for it to finish cleanly")
        }

        wrapper.setConnectionState(ConnectionState.DISCONNECTED)

        this.connectionThreads.remove(id)
        LogHelper.info("Stopped connection '{}'", id)

        return true
    }

    val allConnections: Set<String>
        get() = this.connectionMap.keys

    fun sendMessageToAllChannels(message: String) {
        for (id in this.allConnections) {
            val wrapper = this.connectionMap[id]
            if (wrapper == null) {
                LogHelper.info("Couldn't get wrapper for connection '{}' - not sending message. This is a bug!", id)

                continue
            }

            if (wrapper.getConnectionState() != ConnectionState.CONNECTED) {
                LogHelper.warn("Not sending message to '{}' as it is not connected yet: {}", id, message)
                continue
            }

            wrapper.sendMessageToAllChannels(message)
        }
    }

    fun getAllJoinedChannelsForConnection(id: String): Set<String> {
        val wrapper = this.connectionMap[id]
        if (wrapper == null) {
            LogHelper.error("Tried to get joined channels for '{}' but it doesn't exist", id)

            return Sets.newHashSet()
        }

        if (wrapper.getConnectionState() != ConnectionState.CONNECTED) {
            return Sets.newHashSet()
        }

        return wrapper.getAllJoinedChannels()
    }

    fun usernameMatchesAnyConnection(username: String): Boolean {
        for (id in this.allConnections) {
            val wrapper = this.connectionMap[id]
            if (wrapper == null) {
                LogHelper.info("Couldn't get wrapper for connection '{}' - not sending message. This is a bug!", id)

                continue
            }

            if (username.equals(wrapper.username, ignoreCase = true)) {
                return true
            }
        }

        return false
    }
}
