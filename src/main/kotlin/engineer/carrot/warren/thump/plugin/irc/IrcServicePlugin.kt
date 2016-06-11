package engineer.carrot.warren.thump.plugin.irc

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.api.IThumpServicePlugin
import engineer.carrot.warren.thump.api.IThumpServicePluginConfig
import engineer.carrot.warren.thump.api.ThumpServicePlugin
import engineer.carrot.warren.thump.api.ThumpPluginContext
import engineer.carrot.warren.thump.handler.MessageHandler
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.plugin.irc.config.IrcServersConfiguration
import engineer.carrot.warren.thump.plugin.irc.config.IrcServicePluginConfiguration
import net.minecraftforge.common.MinecraftForge

@ThumpServicePlugin
object IrcServicePlugin : IThumpServicePlugin {

    override val name = "irc"

    lateinit var configWrapper: IThumpServicePluginConfig<IrcServersConfiguration>

    val wrappersManager: IWrappersManager = IrcRunnerWrappersManager()

    override fun configure(context: ThumpPluginContext) {
        val config = context.configuration

        configWrapper = IrcServicePluginConfiguration(baseConfig = config)
        configWrapper.load()

        populateConnectionManager()
    }

    override fun start() {
        this.startAllConnections()
    }

    override fun stop() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Stopping ${entry.key}")
            wrappersManager.stop(entry.key)
        }
    }

    fun populateConnectionManager() {
        val messageListener = MessageHandler(wrappersManager)

        val servers = configWrapper.config.servers

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/servers.cfg!")
        }

        for (serverConfiguration in servers.values) {
            LogHelper.info("adding ${serverConfiguration.server}:${serverConfiguration.port} as ${serverConfiguration.nickname}")

            wrappersManager.initialise(serverConfiguration, Thump.configuration.general)
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