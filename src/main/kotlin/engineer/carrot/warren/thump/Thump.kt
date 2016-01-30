package engineer.carrot.warren.thump

import com.google.common.collect.Lists
import engineer.carrot.warren.thump.command.minecraft.CommandThump
import engineer.carrot.warren.thump.config.ModConfiguration
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.listener.MessageListener
import engineer.carrot.warren.thump.minecraft.ChatEventHandler
import engineer.carrot.warren.thump.proxy.CommonProxy
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.*
import java.io.File

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_ID, version = Reference.MOD_VERSION, modLanguage = "kotlin", modLanguageAdapter = "engineer.carrot.warren.thump.KotlinAdapter", acceptableRemoteVersions = "*")
public object Thump {
    @Mod.Instance(Reference.MOD_ID)
    lateinit var instance: Thump

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    private val connectionManager = ConnectionManager()

    val configuration = ModConfiguration()

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        configuration.initialise(File(event.modConfigurationDirectory, "thump"))
        configuration.loadAllConfigurations()
        configuration.saveAllConfigurations()

        this.populateConnectionManager()

        val handler = ChatEventHandler(this.connectionManager)
        MinecraftForge.EVENT_BUS.register(handler)
        FMLCommonHandler.instance().bus().register(handler)
    }

    fun populateConnectionManager() {
        val messageListener = MessageListener(this.connectionManager)

        val servers = configuration.servers.servers

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/servers.cfg!")
        }

        for (configuration in servers.values) {
            LogHelper.info("Adding to connection manager: {}:{} as {}", configuration.server, configuration.port, configuration.nickname)

            this.connectionManager.addNewConnection(configuration, Lists.newArrayList<Any>(messageListener))
        }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {

    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {

    }

    @Mod.EventHandler
    fun onServerStarting(event: FMLServerStartingEvent) {
        val command = CommandThump(this.connectionManager)
        event.registerServerCommand(command)

        this.startAllConnections()
    }

    fun startAllConnections() {
        val connections = this.connectionManager.allConnections
        for (connection in connections) {
            LogHelper.info("Starting connection '{}'", connection)
            this.connectionManager.startConnection(connection)
        }
    }

    @Mod.EventHandler
    fun onServerStopped(event: FMLServerStoppedEvent) {
        this.connectionManager.stopAllConnections()
    }
}