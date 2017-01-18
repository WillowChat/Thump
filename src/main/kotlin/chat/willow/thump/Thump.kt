package chat.willow.thump

import chat.willow.thump.api.IThumpMinecraftSink
import chat.willow.thump.api.IThumpServicePlugin
import chat.willow.thump.api.IThumpServiceSink
import chat.willow.thump.api.ThumpPluginContext
import chat.willow.thump.config.ModConfiguration
import chat.willow.thump.helper.LogHelper
import chat.willow.thump.helper.PlayerHelper
import chat.willow.thump.minecraft.CommandThump
import chat.willow.thump.minecraft.MinecraftEventsHandler
import chat.willow.thump.plugin.IThumpServicePlugins
import chat.willow.thump.plugin.ThumpPluginDiscoverer
import chat.willow.thump.plugin.irc.IrcServicePlugin
import chat.willow.thump.proxy.CommonProxy
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File

@Suppress("UNUSED", "UNUSED_PARAMETER")
@Mod(modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.MOD_VERSION,
        modLanguage = "kotlin",
        modLanguageAdapter = "chat.willow.thump.CarrotKotlinAdapter",
        acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.11,1.11.2]",
        guiFactory = "chat.willow.thump.config.ThumpModGuiFactory")
object Thump : IThumpServicePlugins, IThumpMinecraftSink, IThumpServiceSink {

    @Mod.Instance(Reference.MOD_ID)
    lateinit var instance: Thump

    @SidedProxy(clientSide = "chat.willow.thump.proxy.ClientProxy", serverSide = "chat.willow.thump.proxy.CommonProxy")
    lateinit var proxy: CommonProxy

    val configuration = ModConfiguration()

    val command = CommandThump(this)
    lateinit var servicePlugins: Map<String, IThumpServicePlugin>

    lateinit var baseServiceConfigDirectory: File

    private var serverStarted = false

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

        MinecraftForge.EVENT_BUS.register(object {
            @SubscribeEvent
            fun onConfigChanged(event: ConfigChangedEvent.OnConfigChangedEvent) {
                if (event.modID == Reference.MOD_ID) {
                    LogHelper.info("Config changed - saving to file")

                    if (Thump.configuration.minecraftGeneralConfiguration.hasChanged()) {
                        Thump.configuration.minecraftGeneralConfiguration.save()
                    }

                    if (IrcServicePlugin.configuration.configuration.hasChanged()) {
                        IrcServicePlugin.configuration.configuration.save()
                    }

                    reloadConfiguration()
                }
            }
        })
    }

    fun reloadConfiguration() {
        if (serverStarted) {
            LogHelper.info("Stopped services, reloading configurations...")
            this.stopAll()
        }

        Thump.configuration.loadAllConfigurations()
        Thump.configuration.saveAllConfigurations()

        this.reconfigureAll()

        if (serverStarted) {
            LogHelper.info("Reloading services...")
            this.startAll()
        }

        LogHelper.info("Reload complete!")
    }

    @Mod.EventHandler
    fun onServerStarting(event: FMLServerStartingEvent) {
        LogHelper.info("server starting - initialising all connections")

        event.registerServerCommand(command)

        startAll()

        serverStarted = true
    }

    @Mod.EventHandler
    fun onServerStopped(event: FMLServerStoppedEvent) {
        LogHelper.info("server stopping - stopping all connections")

        servicePlugins.values.forEach(IThumpServicePlugin::stop)

        serverStarted = false
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