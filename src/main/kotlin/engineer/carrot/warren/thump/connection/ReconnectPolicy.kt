package engineer.carrot.warren.thump.connection

import engineer.carrot.warren.thump.config.ServerConfiguration

class ReconnectPolicy(configuration: ServerConfiguration) {
    private val shouldReconnect: Boolean
    private val reconnectDelaySeconds: Int
    private val maxConsecutiveReconnects: Int

    private var isDisabled = false

    private var currentConnectionAttempt = 1

    private val policyLock: Any

    init {
        this.shouldReconnect = configuration.shouldReconnectAutomatically
        this.reconnectDelaySeconds = configuration.automaticReconnectDelaySeconds
        this.maxConsecutiveReconnects = configuration.maxConsecutiveReconnectAttempts

        this.policyLock = Object()
    }

    fun shouldReconnect(): Boolean {
        synchronized (this.policyLock) {
            return this.shouldReconnect
        }
    }

    fun getCurrentConnectionAttempt(): Int {
        synchronized (this.policyLock) {
            return this.currentConnectionAttempt
        }
    }

    fun incrementConnectionAttempt() {
        synchronized (this.policyLock) {
            this.currentConnectionAttempt++
        }
    }

    fun resetCurrentConnectionAttempts() {
        synchronized (this.policyLock) {
            this.currentConnectionAttempt = 1
            this.isDisabled = false
        }
    }

    fun getReconnectDelaySeconds(): Int {
        synchronized (this.policyLock) {
            return this.reconnectDelaySeconds
        }
    }

    fun getMaxConsecutiveReconnects(): Int {
        synchronized (this.policyLock) {
            return this.maxConsecutiveReconnects
        }
    }

    fun getIsDisabled(): Boolean {
        synchronized (this.policyLock) {
            return this.isDisabled
        }
    }

    fun disableNextReconnect() {
        synchronized (this.policyLock) {
            this.isDisabled = true
        }
    }
}
