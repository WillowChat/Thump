package engineer.carrot.warren.thump.runner

import engineer.carrot.warren.kale.irc.message.rfc1459.PrivMsgMessage
import engineer.carrot.warren.thump.config.ServerConfiguration
import engineer.carrot.warren.thump.handler.LifecycleHandler
import engineer.carrot.warren.thump.handler.MessageHandler
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.warren.IrcRunner
import engineer.carrot.warren.warren.SendSomethingEvent
import engineer.carrot.warren.warren.WarrenEventDispatcher
import engineer.carrot.warren.warren.WarrenRunner
import engineer.carrot.warren.warren.state.IrcState
import kotlin.concurrent.thread

enum class WrapperState { READY, RUNNING }

data class ReconnectionState(val shouldReconnect: Boolean, var forciblyDisabled: Boolean, val delaySeconds: Int, val maxConsecutive: Int, var currentReconnectCount: Int = 0)

data class ConfigurationState(val server: String, val port: Int, val nickname: String, val password: String?, val channels: Map<String, String?>)

interface IWrapper {
    fun start(): Boolean
    fun stop(): Boolean
    fun sendMessage(target: String, message: String)
    fun sendMessageToAll(message: String)

    val channels: Set<String>?
    val nickname: String?
    val state: WrapperState
    val ircState: IrcState?
}

class IrcRunnerWrapper(val id: String, serverConfiguration: ServerConfiguration, private val manager: IWrappersManager): IWrapper {
    val reconnectState: ReconnectionState
    val configuration: ConfigurationState
    @Volatile override var state: WrapperState = WrapperState.READY

    @Volatile private var currentRunner: IrcRunner? = null
    @Volatile private var currentThread: Thread? = null

    override val nickname: String?
        get() = currentRunner?.lastStateSnapshot?.connection?.nickname

    override val channels: Set<String>?
        get() = currentRunner?.lastStateSnapshot?.channels?.joined?.keys

    override val ircState: IrcState?
        get() = currentRunner?.lastStateSnapshot

    init {
        reconnectState = generateReconnectState(serverConfiguration)
        configuration = generateConfiguration(serverConfiguration)
    }

    private fun generateConfiguration(serverConfiguration: ServerConfiguration): ConfigurationState {
        var password: String? = null
        if (serverConfiguration.identifyWithNickServ) {
            password = serverConfiguration.nickServPassword
        }

        return ConfigurationState(serverConfiguration.server, serverConfiguration.port, serverConfiguration.nickname, password, serverConfiguration.channels)
    }

    private fun generateReconnectState(serverConfiguration: ServerConfiguration): ReconnectionState {
        return ReconnectionState(shouldReconnect = serverConfiguration.shouldReconnectAutomatically, forciblyDisabled = false, delaySeconds = serverConfiguration.automaticReconnectDelaySeconds, maxConsecutive = serverConfiguration.maxConsecutiveReconnectAttempts)
    }

    private fun createRunner(): IrcRunner {
        val eventDispatcher = WarrenEventDispatcher()
        eventDispatcher.onChannelMessageListeners += {
            MessageHandler(manager).onChannelMessage(it)
        }

        eventDispatcher.onChannelActionListeners += {
            MessageHandler(manager).onChannelAction(it)
        }

        eventDispatcher.onPrivateMessageListeners += {
            MessageHandler(manager).onPrivateMessage(it)
        }

        eventDispatcher.onPrivateActionListeners += {
            MessageHandler(manager).onPrivateAction(it)
        }

        eventDispatcher.onConnectionLifecycleListeners += {
            LifecycleHandler(id).onConnectionLifecycleChanged(it)
        }

        return WarrenRunner.createRunner(configuration.server, configuration.port, configuration.nickname, configuration.password, configuration.channels, eventDispatcher)
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
        val channels = currentRunner?.lastStateSnapshot?.channels?.joined?.keys
        if (channels == null) {
            LogHelper.info("$id couldn't get channels, not sending message $message")
            return
        }

        for (channel in channels) {
            sendMessage(channel, message)
        }
    }

    override fun stop(): Boolean {
        if (state != WrapperState.RUNNING) {
            LogHelper.error("$id wrapper not running - can't end it")
            return false
        }

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
                    LogHelper.info("$id didn't end thread in time, assuming it's dead")
                }
            }
        }

        state = WrapperState.READY
        return true
    }

    override fun start(): Boolean {
        if (state == WrapperState.RUNNING) {
            LogHelper.error("$id wrapper is already running - can't reuse it, bailing out")
            return false
        } else {
            state = WrapperState.RUNNING
        }

        val runner = createRunner()
        currentRunner = runner

        val thread = thread {
            runner.run()

            state = WrapperState.READY

            currentRunner = null
            currentThread = null

            LogHelper.info("$id irc runner thread ended normally")
        }

        thread.setUncaughtExceptionHandler { thread, exception ->
            state = WrapperState.READY

            currentRunner = null
            currentThread = null

            LogHelper.info("$id irc runner thread ended with exception: $exception")
        }

        currentThread = thread

        return true
    }
}