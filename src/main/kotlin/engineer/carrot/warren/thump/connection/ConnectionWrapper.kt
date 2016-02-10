package engineer.carrot.warren.thump.connection

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import engineer.carrot.warren.thump.config.ServerConfiguration
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.listener.ServerEventListener
import engineer.carrot.warren.warren.IRCConnection

class ConnectionWrapper(val id: String, configuration: ServerConfiguration, listeners: List<Any>) : Runnable {
    private lateinit var connectionState: ConnectionState
    private lateinit var connection: IRCConnection
    private lateinit var reconnectPolicy: ReconnectPolicy

    private val connectionLock: Any

    init {
        this.connectionLock = Object()

        this.initialiseFromConfiguration(configuration, listeners)

        this.connectionState = ConnectionState.DISCONNECTED
    }

    private fun initialiseFromConfiguration(configuration: ServerConfiguration, listeners: List<Any>) {
        val builder = IRCConnection.Builder().server(configuration.server).port(configuration.port).nickname(configuration.nickname).login(LOGIN).plaintext(!configuration.useTLS)

        for ((channelName, channelKey) in configuration.channels) {
            if (channelKey == null) {
                builder.channel(channelName)
            } else {
                builder.channel(channelName, channelKey)
            }
        }

        if (configuration.identifyWithNickServ) {
            builder.nickservPassword(configuration.nickServPassword)
        }

        if (configuration.forceAcceptCertificates) {
            builder.fingerprints(configuration.forciblyAcceptedCertificates)
        }

        this.registerInternalListeners(builder)
        this.registerExternalListeners(builder, listeners)

        this.connection = builder.build()

        this.reconnectPolicy = ReconnectPolicy(configuration)
    }

    private fun registerInternalListeners(builder: IRCConnection.Builder) {
        builder.listener(ConnectionStateListener(this))
        builder.listener(ServerEventListener(id))
    }

    private fun registerExternalListeners(builder: IRCConnection.Builder, listeners: List<Any>) {
        for (listener in listeners) {
            builder.listener(listener)
        }
    }

    fun getConnectionState(): ConnectionState {
        synchronized (this.connectionLock) {
            return this.connectionState
        }
    }

    fun setConnectionState(connectionState: ConnectionState) {
        synchronized (this.connectionLock) {
            this.connectionState = connectionState
        }
    }

    fun resetReconnectCounter() {
        this.reconnectPolicy.resetCurrentConnectionAttempts()
    }

    fun stop() {
        if (this.getConnectionState() == ConnectionState.DISCONNECTING) {
            LogHelper.error("Already disconnecting connection '{}'", id)
            return
        }

        this.setConnectionState(ConnectionState.DISCONNECTING)

        synchronized (this.connectionLock) {
            this.connection.disconnect()
        }
    }

    fun sendMessageToAllChannels(message: String) {
        if (this.getConnectionState() != ConnectionState.CONNECTED) {
            LogHelper.error("Connection '{}' is not ready to send messages yet: {}", id, message)
            return
        }

        synchronized (this.connectionLock) {
            val channelManager = this.connection.joinedChannels

            val channels = channelManager.allChannels

            if (channels.isEmpty()) {
                LogHelper.warn("Message had nowhere to go because the bridge doesn't think it's in any channels yet: {}", id, message)
                return
            }

            for (sChannel in channels.keys) {
                val channel = channels[sChannel]
                this.connection.sendMessageToChannel(channel, message)
            }

            LogHelper.info("Sent message to channels '{}': {}", Joiner.on(",").join(channels.keys), message)
        }
    }

    fun getAllJoinedChannels(): Set<String> {
        if (this.getConnectionState() != ConnectionState.CONNECTED) {
            LogHelper.warn("Tried to get joined channels for '{}', but it isn't connected yet", id)
            return Sets.newHashSet()
        }

        synchronized(this.connectionLock) {
            return this.connection.joinedChannels.allChannels.keys
        }
    }

    val username: String
        get() = synchronized (this.connectionLock) {
            return this.connection.botNickname
        }

    fun disableNextReconnect() {
        this.reconnectPolicy.disableNextReconnect()
    }

    // Runnable

    override fun run() {
        this.reconnectPolicy.resetCurrentConnectionAttempts()

        while (true) {
            this.setConnectionState(ConnectionState.CONNECTING)
            this.connection.connect()
            this.setConnectionState(ConnectionState.DISCONNECTED)

            if (!this.reconnectPolicy.shouldReconnect() || this.reconnectPolicy.getIsDisabled()) {
                break
            }

            this.reconnectPolicy.incrementConnectionAttempt()

            if (this.reconnectPolicy.getCurrentConnectionAttempt() > this.reconnectPolicy.getMaxConsecutiveReconnects()) {
                break
            }

            this.setConnectionState(ConnectionState.WAITING)
            val delay = this.reconnectPolicy.getReconnectDelaySeconds()
            try {
                if (Thread.currentThread().isInterrupted) {
                    break
                }

                Thread.sleep((delay * 1000).toLong())
            } catch (e: InterruptedException) {
                if (this.reconnectPolicy.getIsDisabled()) {
                    break
                }
            }

        }

        this.setConnectionState(ConnectionState.DISCONNECTED)
    }

    companion object {
        private val LOGIN = "thumpBridge"
    }
}
