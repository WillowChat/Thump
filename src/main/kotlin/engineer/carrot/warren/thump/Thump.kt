package engineer.carrot.warren.thump

import engineer.carrot.warren.thump.command.minecraft.CommandThump
import engineer.carrot.warren.thump.config.ModConfiguration
import engineer.carrot.warren.thump.handler.MessageHandler
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.minecraft.ChatEventHandler
import engineer.carrot.warren.thump.proxy.CommonProxy
import engineer.carrot.warren.thump.runner.IWrappersManager
import engineer.carrot.warren.thump.runner.IrcRunnerWrappersManager
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import java.io.File

@Suppress("UNUSED", "UNUSED_PARAMETER")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_ID, version = Reference.MOD_VERSION, modLanguage = "kotlin", modLanguageAdapter = "engineer.carrot.warren.thump.CarrotKotlinAdapter", acceptableRemoteVersions = "*")
object Thump {
    @Mod.Instance(Reference.MOD_ID)
    lateinit var instance: Thump

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    val wrappersManager: IWrappersManager = IrcRunnerWrappersManager()
    val configuration = ModConfiguration()

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        configuration.initialise(File(event.modConfigurationDirectory, "thump"))
        configuration.loadAllConfigurations()
        configuration.saveAllConfigurations()

        this.populateConnectionManager()

        val handler = ChatEventHandler(wrappersManager)
        MinecraftForge.EVENT_BUS.register(handler)
    }

    fun populateConnectionManager() {
        val messageListener = MessageHandler(wrappersManager)

        val servers = configuration.servers.servers

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/servers.cfg!")
        }

        for (configuration in servers.values) {
            LogHelper.info("adding ${configuration.server}:${configuration.port} as ${configuration.nickname}")

            wrappersManager.initialise(configuration)
        }

        MinecraftForge.EVENT_BUS.register(messageListener)
    }

    @Mod.EventHandler
    fun onServerStarting(event: FMLServerStartingEvent) {
        LogHelper.info("server starting - initialising all connections")

        val command = CommandThump(wrappersManager)
        event.registerServerCommand(command)

        this.startAllConnections()
    }

    fun startAllConnections() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Starting ${entry.key}")
            wrappersManager.start(entry.key)
        }
    }

    @Mod.EventHandler
    fun onServerStopped(event: FMLServerStoppedEvent) {
        LogHelper.info("server stopping - stopping all connections")

        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Stopping ${entry.key}")
            wrappersManager.stop(entry.key)
        }
    }
}