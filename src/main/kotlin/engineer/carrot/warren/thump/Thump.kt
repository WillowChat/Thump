package engineer.carrot.warren.thump

import engineer.carrot.warren.thump.api.IThumpMinecraftSink
import engineer.carrot.warren.thump.api.IThumpServicePlugin
import engineer.carrot.warren.thump.api.IThumpServiceSink
import engineer.carrot.warren.thump.api.ThumpPluginContext
import engineer.carrot.warren.thump.command.minecraft.CommandThump
import engineer.carrot.warren.thump.config.ModConfiguration
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.minecraft.MinecraftEventsHandler
import engineer.carrot.warren.thump.plugin.IThumpServicePlugins
import engineer.carrot.warren.thump.plugin.ThumpPluginDiscoverer
import engineer.carrot.warren.thump.proxy.CommonProxy
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import java.io.File

@Suppress("UNUSED", "UNUSED_PARAMETER")
@Mod(modid = Reference.MOD_ID, name = Reference.MOD_ID, version = Reference.MOD_VERSION, modLanguage = "kotlin", modLanguageAdapter = "engineer.carrot.warren.thump.CarrotKotlinAdapter", acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.11]")
object Thump : IThumpServicePlugins, IThumpMinecraftSink, IThumpServiceSink {

    @Mod.Instance(Reference.MOD_ID)
    lateinit var instance: Thump

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    val configuration = ModConfiguration()

    val command = CommandThump(this)
    lateinit var servicePlugins: Map<String, IThumpServicePlugin>

    lateinit var baseServiceConfigDirectory: File

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        val modConfigDirectory = File(event.modConfigurationDirectory, "thump")
        configuration.initialise(modConfigDirectory)
        configuration.loadAllConfigurations()
        configuration.saveAllConfigurations()

        servicePlugins = ThumpPluginDiscoverer.discover(event.asmData).associate {
            val id = it.id.toLowerCase().filter(Char::isLetter)

            (id to it)
        }

        baseServiceConfigDirectory = File(modConfigDirectory, "services")

        reconfigureAll()

        val handler = MinecraftEventsHandler(this)
        MinecraftForge.EVENT_BUS.register(handler)
    }

    @Mod.EventHandler
    fun onServerStarting(event: FMLServerStartingEvent) {
        LogHelper.info("server starting - initialising all connections")

        event.registerServerCommand(command)

        startAll()
    }

    @Mod.EventHandler
    fun onServerStopped(event: FMLServerStoppedEvent) {
        LogHelper.info("server stopping - stopping all connections")

        servicePlugins.values.forEach(IThumpServicePlugin::stop)
    }

    // IThumpServicePlugins

    override fun reconfigureAll() {
        servicePlugins.values.forEach {
            val pluginConfig = Configuration(File(baseServiceConfigDirectory, "${it.id}.cfg"), "2")
            val context = ThumpPluginContext(configuration = pluginConfig, minecraftSink = this, serviceSink = this)

            it.configure(context)
        }

        command.reconfigureHandlers(servicePlugins.mapValues { it.value.commandHandler })
    }

    override fun startAll() {
        servicePlugins.values.forEach(IThumpServicePlugin::start)
    }

    override fun stopAll() {
        servicePlugins.values.forEach(IThumpServicePlugin::stop)
    }

    override fun statuses(): Map<String, List<String>> {
        return servicePlugins.mapValues { it.value.status() }
    }

    override fun anyServicesMatch(name: String): Boolean {
        return servicePlugins.values.any { it.anyConnectionsMatch(name) }
    }

    // IThumpServiceSink

    override fun sendToAllPlayers(source: String, message: ITextComponent) {
        if (anyServicesMatch(source)) {
            return
        }

        sendToAllPlayersWithoutCheckingSource(message)
    }

    override fun sendToAllPlayersWithoutCheckingSource(message: ITextComponent) {
        PlayerHelper.sendMessageToAllPlayers(message)
    }

    // IThumpServiceSource

    override fun sendToAllServices(message: ITextComponent) {
        servicePlugins.values.forEach {
            it.onMinecraftMessage(message)
        }
    }

    override fun sendToAllServices(message: String) {
        sendToAllServices(TextComponentString(message))
    }

}