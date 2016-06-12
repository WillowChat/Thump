package engineer.carrot.warren.thump.plugin.irc

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.api.IThumpServicePlugin
import engineer.carrot.warren.thump.api.ThumpPluginContext
import engineer.carrot.warren.thump.api.ThumpServicePlugin
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.plugin.irc.config.IrcServicePluginConfiguration
import engineer.carrot.warren.thump.plugin.irc.handler.MessageHandler
import net.minecraftforge.common.MinecraftForge

@ThumpServicePlugin
object IrcServicePlugin : IThumpServicePlugin {

    override val id = "irc"

    lateinit var configuration: IrcServicePluginConfiguration

    val wrappersManager: IWrappersManager = IrcRunnerWrappersManager()

    private lateinit var sink: IThumpMinecraftSink

    override fun configure(context: ThumpPluginContext) {
        val config = context.configuration
        sink = context.minecraftSink

        configuration = IrcServicePluginConfiguration(configuration = config)
        configuration.load()

        wrappersManager.removeAll()

        populateConnectionManager()
    }

    override fun start() {
        this.startAllConnections()
    }

    override fun stop() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Stopping ${entry.key}")
            wrappersManager.stop(entry.key, shouldReconnect = false)
        }
    }

    override fun anyConnectionsMatch(name: String): Boolean {
        return wrappersManager.anyWrappersMatch(name)
    }

    override fun onMinecraftMessage(message: String) {
        wrappersManager.wrappers.forEach { entry ->
            entry.value.sendMessageToAll(message)
        }
    }

    fun populateConnectionManager() {
        val messageListener = MessageHandler(Thump)

        val servers = configuration.connections.servers

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/services/irc.cfg!")
        }

        for (serverConfiguration in servers.values) {
            LogHelper.info("adding ${serverConfiguration.server}:${serverConfiguration.port} as ${serverConfiguration.nickname}")

            wrappersManager.initialise(serverConfiguration, IrcServicePlugin.configuration.general, sink)
        }

        MinecraftForge.EVENT_BUS.register(messageListener)
    }

    fun startAllConnections() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Starting ${entry.key}")
            wrappersManager.start(entry.key)
        }
    }

}