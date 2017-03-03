package chat.willow.thump.plugin.irc

import chat.willow.kale.irc.message.rfc1459.PrivMsgMessage
import chat.willow.thump.api.IServiceChatFormatter
import chat.willow.thump.api.IThumpMinecraftSink
import chat.willow.thump.helper.LogHelper
import chat.willow.thump.plugin.irc.config.IrcServerConfiguration
import chat.willow.thump.plugin.irc.config.IrcServicePluginGeneralConfiguration
import chat.willow.thump.plugin.irc.handler.LifecycleHandler
import chat.willow.thump.plugin.irc.handler.MessageHandler
import chat.willow.warren.*
import chat.willow.warren.event.*
import chat.willow.warren.event.internal.SendSomethingEvent
import chat.willow.warren.state.IrcState
import chat.willow.warren.state.LifecycleState
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import kotlin.concurrent.thread

enum class WrapperState { READY, RUNNING, RECONNECTING }

data class ReconnectionState(val shouldReconnect: Boolean, var forciblyDisabled: Boolean, val delaySeconds: Int, val maxConsecutive: Int, var currentReconnectCount: Int = 0)

data class ConfigurationState(val server: String, val port: Int, val useTLS: Boolean, val nickname: String, val serverPassword: String? = null, val channels: Map<String, String?>, val shouldLogIncomingLines: Boolean, val fingerprints: Set<String>?, val sasl: SaslConfiguration?, val nickserv: NickServConfiguration?)

interface IWrapper {
    fun start(): Boolean
    fun stop(shouldReconnect: Boolean = true): Boolean
    fun sendRaw(line: String): Boolean
    fun sendMessage(target: String, message: String)
    fun sendMessageToAll(message: String)

    val server: String
    val channels: Set<String>?
    val nickname: String?
    val state: WrapperState
    val ircState: IrcState?

    val id: String
}

class IrcRunnerWrapper(override val id: String, ircServerConfiguration: IrcServerConfiguration, generalConfiguration: IrcServicePluginGeneralConfiguration, private val sink: IThumpMinecraftSink, private val formatter: IServiceChatFormatter): IWrapper {
    val reconnectState: ReconnectionState
    override val server: String

    val configuration: ConfigurationState
    @Volatile override var state: WrapperState = WrapperState.READY
    @Volatile private var currentClient: IWarrenClient? = null

    @Volatile private var currentThread: Thread? = null
    override val nickname: String?
        get() = currentClient?.state?.connection?.nickname

    override val channels: Set<String>?
        get() = currentClient?.state?.channels?.joined?.all?.keys

    override val ircState: IrcState?
        get() = currentClient?.state

    init {
        reconnectState = generateReconnectState(ircServerConfiguration)
        configuration = generateConfiguration(ircServerConfiguration, generalConfiguration)
        server = ircServerConfiguration.server
    }

    private fun generateConfiguration(ircServerConfiguration: IrcServerConfiguration, generalConfiguration: IrcServicePluginGeneralConfiguration): ConfigurationState {
        val filteredFingerprints = ircServerConfiguration.forciblyAcceptedCertificates.filterNot(String::isBlank).toSet()
        var fingerprints: Set<String>? = null
        if (ircServerConfiguration.forceAcceptCertificates) {
            fingerprints = filteredFingerprints
        }

        val saslConfiguration = if (ircServerConfiguration.identifyWithSasl) {
            SaslConfiguration(account = ircServerConfiguration.saslAccount, password = ircServerConfiguration.saslPassword)
        } else {
            null
        }

        val nickservConfiguration = if (ircServerConfiguration.identifyWithNickServ) {
            NickServConfiguration(account = ircServerConfiguration.nickServAccount, password = ircServerConfiguration.nickServPassword)
        } else {
            null
        }

        return ConfigurationState(ircServerConfiguration.server, ircServerConfiguration.port, ircServerConfiguration.useTLS, ircServerConfiguration.nickname, ircServerConfiguration.serverPassword, ircServerConfiguration.channels, generalConfiguration.logRawIRCLinesToServerConsole, fingerprints, saslConfiguration, nickservConfiguration)
    }

    private fun generateReconnectState(ircServerConfiguration: IrcServerConfiguration): ReconnectionState {
        return ReconnectionState(shouldReconnect = ircServerConfiguration.shouldReconnectAutomatically, forciblyDisabled = false, delaySeconds = ircServerConfiguration.automaticReconnectDelaySeconds, maxConsecutive = ircServerConfiguration.maxConsecutiveReconnectAttempts)
    }

    private fun createClient(): IWarrenClient {
        val fingerprints = configuration.fingerprints
        if (fingerprints != null && fingerprints.isEmpty()) {
            LogHelper.warn("DANGER ZONE: making runner for $id with the \"accept all certificates\" option - it's not secure! Add the expected certificate authority to your Java trust store, or use certificate fingerprints instead!")
        }

        val userString = when (FMLCommonHandler.instance().side) {
            Side.CLIENT -> "thumpClnt"
            Side.SERVER -> "thumpSrv"
            else -> "thump"
        }

        val client = WarrenClient.build {
            server = ServerConfiguration(configuration.server, configuration.port, configuration.useTLS, configuration.fingerprints, configuration.serverPassword)
            user = UserConfiguration(configuration.nickname, userString, configuration.sasl, configuration.nickserv)
            channels = ChannelsConfiguration(configuration.channels)
            events {
               fireIncomingLineEvent = configuration.shouldLogIncomingLines
            }
        }

        client.events.on(ChannelMessageEvent::class) {
            MessageHandler(sink, this, formatter).onChannelMessage(it)
        }

        client.events.on(ChannelActionEvent::class) {
            MessageHandler(sink, this, formatter).onChannelAction(it)
        }

        client.events.on(PrivateMessageEvent::class) {
            MessageHandler(sink, this, formatter).onPrivateMessage(it)
        }

        client.events.on(PrivateActionEvent::class) {
            MessageHandler(sink, this, formatter).onPrivateAction(it)
        }

        if (configuration.shouldLogIncomingLines) {
            client.events.on(RawIncomingLineEvent::class) {
                LogHelper.info("$id >> ${it.line}")
            }
        }

        client.events.on(ConnectionLifecycleEvent::class) {
            when(it.lifecycle) {
                LifecycleState.CONNECTED -> reconnectState.currentReconnectCount = 0
                else -> Unit
            }

            LifecycleHandler(this, sink).onConnectionLifecycleChanged(it)
        }

        return client
    }

    override fun sendRaw(line: String): Boolean {
        val runner = currentClient
        if (runner == null) {
            LogHelper.info("$id not sending raw line because the irc runner isn't running: $line")
            return false
        }

        runner.send(line)

        return true
    }

    override fun sendMessage(target: String, message: String) {
        val runner = currentClient
        if (runner == null) {
            LogHelper.info("$id not sending message because the irc runner isn't running: $target $message")
            return
        }

        runner.send(PrivMsgMessage(target = target, message = message))
    }

    override fun sendMessageToAll(message: String) {
        val channels = currentClient?.state?.channels?.joined?.all?.keys
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

        val runner = createClient()
        currentClient = runner

        val thread = thread(name = "Thump - $id") {
            runner.start()

            LogHelper.info("$id irc runner thread ended normally")

            onStoppedRunning()
        }

        thread.setUncaughtExceptionHandler { _, exception ->
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
        currentClient = null
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

        currentClient = null
        currentThread = null
    }
}