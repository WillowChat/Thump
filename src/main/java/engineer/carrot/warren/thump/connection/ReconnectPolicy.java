package engineer.carrot.warren.thump.connection;

import engineer.carrot.warren.thump.config.ServerConfiguration;

public class ReconnectPolicy {
    private boolean shouldReconnect;
    private int reconnectDelaySeconds;
    private int maxConsecutiveReconnects;

    private boolean isDisabled = false;

    private int currentConnectionAttempt = 1;

    private final Object policyLock;

    public ReconnectPolicy(ServerConfiguration configuration) {
        this.shouldReconnect = configuration.shouldReconnectAutomatically;
        this.reconnectDelaySeconds = configuration.automaticReconnectDelaySeconds;
        this.maxConsecutiveReconnects = configuration.maxConsecutiveReconnectAttempts;

        this.policyLock = new Object();
    }

    public boolean shouldReconnect() {
        synchronized (this.policyLock) {
            return this.shouldReconnect;
        }
    }

    public int getCurrentConnectionAttempt() {
        synchronized (this.policyLock) {
            return this.currentConnectionAttempt;
        }
    }

    public void incrementConnectionAttempt() {
        synchronized (this.policyLock) {
            this.currentConnectionAttempt++;
        }
    }

    public void resetCurrentConnectionAttempts() {
        synchronized (this.policyLock) {
            this.currentConnectionAttempt = 1;
            this.isDisabled = false;
        }
    }

    public int getReconnectDelaySeconds() {
        synchronized (this.policyLock) {
            return this.reconnectDelaySeconds;
        }
    }

    public int getMaxConsecutiveReconnects() {
        synchronized (this.policyLock) {
            return this.maxConsecutiveReconnects;
        }
    }

    public boolean getIsDisabled() {
        synchronized (this.policyLock) {
            return this.isDisabled;
        }
    }

    public void disableNextReconnect() {
        synchronized (this.policyLock) {
            this.isDisabled = true;
        }
    }
}
