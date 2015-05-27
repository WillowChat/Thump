package engineer.carrot.warren.thump;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import engineer.carrot.warren.thump.config.ConfigUtils;
import engineer.carrot.warren.thump.config.Configuration;
import engineer.carrot.warren.thump.config.ServerConfiguration;
import engineer.carrot.warren.thump.handler.ChatEventHandler;
import engineer.carrot.warren.thump.listener.MessageListener;
import engineer.carrot.warren.thump.proxy.CommonProxy;
import engineer.carrot.warren.thump.reference.Reference;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, certificateFingerprint = Reference.FINGERPRINT, dependencies = "")
public class Thump {
    @Mod.Instance(Reference.MOD_ID)
    public static Thump instance;

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    public static CommonProxy proxy;

    private final ConnectionManager connectionManager = new ConnectionManager();

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        boolean hasConfiguration = ConfigUtils.doesConfigFileExist();
        if (!hasConfiguration) {
            LogHelper.error("Configuration file did not exist, making '{}' and exiting.", ConfigUtils.configLocation);
            ConfigUtils.createDefaultConfig();

            throw new RuntimeException("Created a new config.json - fill it with details");
        }

        Configuration allConfigurations = ConfigUtils.readConfig();

        LogHelper.info("IRC bot starting up");

        MessageListener messageListener = new MessageListener();

        for (ServerConfiguration configuration : allConfigurations.serverConfigurations) {
            LogHelper.info("Adding to connection manager: {}:{} as {}", configuration.server, configuration.port, configuration.nickname);

            this.connectionManager.addNewConnection(configuration, Lists.<Object>newArrayList(messageListener));
        }

        MinecraftForge.EVENT_BUS.register(new ChatEventHandler(this.connectionManager));
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
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