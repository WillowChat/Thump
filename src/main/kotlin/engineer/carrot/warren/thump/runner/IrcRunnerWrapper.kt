package engineer.carrot.warren.thump.runner

import engineer.carrot.warren.kale.irc.message.rfc1459.PrivMsgMessage
import engineer.carrot.warren.kale.irc.message.utility.RawMessage
import engineer.carrot.warren.thump.config.GeneralConfiguration
import engineer.carrot.warren.thump.config.ServerConfiguration
import engineer.carrot.warren.thump.handler.LifecycleHandler
import engineer.carrot.warren.thump.handler.MessageHandler
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.warren.*
import engineer.carrot.warren.warren.event.*
import engineer.carrot.warren.warren.event.internal.SendSomethingEvent
import engineer.carrot.warren.warren.state.IrcState
import engineer.carrot.warren.warren.state.LifecycleState
import kotlin.concurrent.thread

enum class WrapperState { READY, RUNNING, RECONNECTING }

data class ReconnectionState(val shouldReconnect: Boolean, var forciblyDisabled: Boolean, val delaySeconds: Int, val maxConsecutive: Int, var currentReconnectCount: Int = 0)

data class ConfigurationState(val server: String, val port: Int, val useTLS: Boolean, val nickname: String, val channels: Map<String, String?>, val shouldLogIncomingLines: Boolean, val fingerprints: Set<String>?, val sasl: SaslConfiguration?, val nickserv: NickServConfiguration?)

interface IWrapper {
    fun start(): Boolean
    fun stop(shouldReconnect: Boolean = true): Boolean
    fun sendRaw(line: String): Boolean
    fun sendMessage(target: String, message: String)
    fun sendMessageToAll(message: String)

    val channels: Set<String>?
    val nickname: String?
    val state: WrapperState
    val ircState: IrcState?
}

class IrcRunnerWrapper(val id: String, serverConfiguration: ServerConfiguration, generalConfiguration: GeneralConfiguration, private val manager: IWrappersManager): IWrapper {
    val reconnectState: ReconnectionState

    val configuration: ConfigurationState
    @Volatile override var state: WrapperState = WrapperState.READY
    @Volatile private var currentRunner: IrcRunner? = null

    @Volatile private var currentThread: Thread? = null
    override val nickname: String?
        get() = currentRunner?.lastStateSnapshot?.connection?.nickname

    override val channels: Set<String>?
        get() = currentRunner?.lastStateSnapshot?.channels?.joined?.all?.keys

    override val ircState: IrcState?
        get() = currentRunner?.lastStateSnapshot

    init {
        reconnectState = generateReconnectState(serverConfiguration)
        configuration = generateConfiguration(serverConfiguration, generalConfiguration)
    }

    private fun generateConfiguration(serverConfiguration: ServerConfiguration, generalConfiguration: GeneralConfiguration): ConfigurationState {
        val filteredFingerprints: Set<String> = serverConfiguration.forciblyAcceptedCertificates.filterNot { it.isBlank() }.toSet()
        var fingerprints: Set<String>? = null
        if (serverConfiguration.forceAcceptCertificates) {
            fingerprints = filteredFingerprints
        }

        val saslConfiguration = if (serverConfiguration.identifyWithSasl) {
            SaslConfiguration(account = serverConfiguration.saslAccount, password = serverConfiguration.saslPassword)
        } else {
            null
        }

        val nickservConfiguration = if (serverConfiguration.identifyWithNickServ) {
            NickServConfiguration(account = serverConfiguration.nickServAccount, password = serverConfiguration.nickServPassword)
        } else {
            null
        }

        return ConfigurationState(serverConfiguration.server, serverConfiguration.port, serverConfiguration.useTLS, serverConfiguration.nickname, serverConfiguration.channels, generalConfiguration.logRawIRCLinesToServerConsole, fingerprints, saslConfiguration, nickservConfiguration)
    }

    private fun generateReconnectState(serverConfiguration: ServerConfiguration): ReconnectionState {
        return ReconnectionState(shouldReconnect = serverConfiguration.shouldReconnectAutomatically, forciblyDisabled = false, delaySeconds = serverConfiguration.automaticReconnectDelaySeconds, maxConsecutive = serverConfiguration.maxConsecutiveReconnectAttempts)
    }

    private fun createRunner(): IrcRunner {
        val events = WarrenEventDispatcher()
        events.on(ChannelMessageEvent::class) {
            MessageHandler(manager).onChannelMessage(it)
        }

        events.on(ChannelActionEvent::class) {
            MessageHandler(manager).onChannelAction(it)
        }

        events.on(PrivateMessageEvent::class) {
            MessageHandler(manager).onPrivateMessage(it)
        }

        events.on(PrivateActionEvent::class) {
            MessageHandler(manager).onPrivateAction(it)
        }

        if (configuration.shouldLogIncomingLines) {
            events.on(RawIncomingLineEvent::class) {
                LogHelper.info("$id >> ${it.line}")
            }
        }

        events.on(ConnectionLifecycleEvent::class) {
            when(it.lifecycle) {
                LifecycleState.CONNECTED -> reconnectState.currentReconnectCount = 0
                else -> Unit
            }

            LifecycleHandler(id).onConnectionLifecycleChanged(it)
        }

        val fingerprints = configuration.fingerprints
        if (fingerprints != null && fingerprints.isEmpty()) {
            LogHelper.warn("DANGER ZONE: making runner for $id with the \"accept all certificates\" option - it's not secure! Add the expected certificate authority to your Java trust store, or use certificate fingerprints instead!")
        }

        val factory = WarrenFactory(ServerConfiguration(configuration.server, configuration.port, configuration.useTLS, configuration.fingerprints), UserConfiguration(configuration.nickname, configuration.sasl, configuration.nickserv),
                                    ChannelsConfiguration(configuration.channels), EventConfiguration(events, configuration.shouldLogIncomingLines))

        return factory.create()
    }

    override fun sendRaw(line: String): Boolean {
        val runner = currentRunner
        if (runner == null) {
            LogHelper.info("$id not sending raw line because the irc runner isn't running: $line")
            return false
        }

        runner.eventSink.add { runner.sink.writeRaw(line) }
        return true
    }

    override fun sendMessage(target: String, message: String) {
        val runner = currentRunner
        if (runner == null) {
            LogHelper.info("$id not sending message because the irc runner isn't running: $target $message")
            return
        }

        runner.eventSink.add(SendSomethingEvent(PrivMsgMessage(target = target, message = message), runner.sink))
    }

    override fun sendMessageToAll(message: String) {
        val channels = currentRunner?.lastStateSnapshot?.channels?.joined?.all?.keys
        if (channels == null) {
            LogHelper.info("$id couldn't get channels, not sending message $message")
            return
        }

        for (channel in channels) {
            sendMessage(channel, message)
        }
    }

    override fun stop(shouldReconnect: Boolean): Boolean {
        if (state == WrapperState.READY) {
            LogHelper.error("$id wrapper not running - can't end it")
            return false
        }

        reconnectState.forciblyDisabled = !shouldReconnect

        val thread = currentThread
        if (thread == null) {
            LogHelper.info("$id wrapper doesn't have a thread - must already be dead")
        } else {
            if (thread.isAlive) {
                thread.interrupt()
                try {
                    LogHelper.info("$id waiting for thread to end cleanly for 2 seconds...")
                    thread.join(2000)
                    LogHelper.info("$id done")
                } catch (e: InterruptedException) {

                }
            }
        }

        resetStateRunnerAndThreads()
        return true
    }

    override fun start(): Boolean {
        reconnectState.forciblyDisabled = false

        if (state == WrapperState.RUNNING) {
            LogHelper.error("$id wrapper is already running/reconnecting - can't reuse it, bailing out")
            return false
        } else {
            state = WrapperState.RUNNING
        }

        val runner = createRunner()
        currentRunner = runner

        val thread = thread(name = "Thump - $id") {
            runner.run()

            LogHelper.info("$id irc runner thread ended normally")

            onStoppedRunning()
        }

        thread.setUncaughtExceptionHandler { thread, exception ->
            LogHelper.info("$id irc runner thread ended with exception: $exception")

            onStoppedRunning()
        }

        currentThread = thread

        return true
    }

    private fun onStoppedRunning() {
        if (!reconnectState.shouldReconnect || reconnectState.forciblyDisabled) {
            LogHelper.info("$id not reconnecting because it's (forcibly) disabled")

            resetStateRunnerAndThreads()
            return
        }

        reconnectState.currentReconnectCount += 1

        if (reconnectState.currentReconnectCount > reconnectState.maxConsecutive) {
            LogHelper.warn("$id not reconnecting because it tried too many times (${reconnectState.maxConsecutive})")

            reconnectState.currentReconnectCount = 0
            resetStateRunnerAndThreads()
            return
        }

        state = WrapperState.RECONNECTING
        currentRunner = null
        currentThread = null

        val delayMs = reconnectState.delaySeconds * 1000L
        try {
            if (Thread.currentThread().isInterrupted) {
                resetStateRunnerAndThreads()
                return
            }

            Thread.sleep(delayMs)
        } catch(exception: InterruptedException) {
            resetStateRunnerAndThreads()
            return
        }

        start()
    }

    private fun resetStateRunnerAndThreads() {
        state = WrapperState.READY

        currentRunner = null
        currentThread = null
    }
}