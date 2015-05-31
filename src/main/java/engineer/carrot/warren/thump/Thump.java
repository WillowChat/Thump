package engineer.carrot.warren.thump;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import engineer.carrot.warren.thump.command.minecraft.CommandThump;
import engineer.carrot.warren.thump.config.ModConfiguration;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.handler.minecraft.ChatEventHandler;
import engineer.carrot.warren.thump.listener.MessageListener;
import engineer.carrot.warren.thump.proxy.CommonProxy;
import engineer.carrot.warren.thump.reference.Reference;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Map;
import java.util.Set;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, certificateFingerprint = Reference.FINGERPRINT, dependencies = "", acceptableRemoteVersions = "*")
public class Thump {
    @Mod.Instance(Reference.MOD_ID)
    public static Thump instance;

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    public static CommonProxy proxy;

    private final ConnectionManager connectionManager = new ConnectionManager();

    public static final ModConfiguration configuration = new ModConfiguration();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        configuration.initialise(new File(event.getModConfigurationDirectory(), "thump"));
        configuration.loadAllConfigurations();
        configuration.saveAllConfigurations();

        this.populateConnectionManager();

        ChatEventHandler handler = new ChatEventHandler(this.connectionManager);
        MinecraftForge.EVENT_BUS.register(handler);
        FMLCommonHandler.instance().bus().register(handler);
    }

    public void populateConnectionManager() {
        MessageListener messageListener = new MessageListener(this.connectionManager);

        Map<String, ServerConfiguration> servers = configuration.getServers().servers;

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/servers.cfg!");
        }

        for (ServerConfiguration configuration : servers.values()) {
            LogHelper.info("Adding to connection manager: {}:{} as {}", configuration.server, configuration.port, configuration.nickname);

            this.connectionManager.addNewConnection(configuration, Lists.<Object>newArrayList(messageListener));
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandThump(this.connectionManager));

        this.startAllConnections();
    }

    public void startAllConnections() {
        Set<String> connections = this.connectionManager.getAllConnections();
        for (String connection : connections) {
            LogHelper.info("Starting connection '{}'", connection);
            this.connectionManager.startConnection(connection);
        }
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        this.connectionManager.stopAllConnections();
    }
}