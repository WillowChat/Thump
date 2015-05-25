package engineer.carrot.warren.thump;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import engineer.carrot.warren.thump.config.ConfigUtils;
import engineer.carrot.warren.thump.config.Configuration;
import engineer.carrot.warren.thump.listener.MessageListener;
import engineer.carrot.warren.thump.proxy.CommonProxy;
import engineer.carrot.warren.thump.reference.Reference;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.IRCServerConnection;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION_NUMBER, certificateFingerprint = Reference.FINGERPRINT, dependencies = "")
public class Thump {
    @Mod.Instance(Reference.MOD_ID)
    public static Thump instance;

    @SidedProxy(clientSide = "engineer.carrot.warren.thump.proxy.ClientProxy", serverSide = "engineer.carrot.warren.thump.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void preInit(FMLPreInitializationEvent event) {
        boolean hasConfiguration = ConfigUtils.doesConfigFileExist();
        if (!hasConfiguration) {
            LogHelper.error("Configuration file did not exist, making '{}' and exiting.", ConfigUtils.configLocation);
            ConfigUtils.createDefaultConfig();

            throw new RuntimeException("Created a new config.json - fill it with details");
        }

        Configuration configuration = ConfigUtils.readConfig();

        LogHelper.info("IRC bot starting up");
        LogHelper.info("Connecting to {}:{} as {}", configuration.server, configuration.port, configuration.nickname);

        IRCServerConnection ircServerConnection = new IRCServerConnection(configuration.server, configuration.port, configuration.nickname);
        if (configuration.identifyWithNickServ) {
            ircServerConnection.setNickservPassword(configuration.nickServPassword);
        }

        if (!configuration.autoJoinChannels.isEmpty()) {
            ircServerConnection.setAutoJoinChannels(configuration.autoJoinChannels);
        }

        if (configuration.forceAcceptCertificates) {
            if (!configuration.forciblyAcceptedCertificates.isEmpty()) {
                ircServerConnection.setForciblyAcceptedCertificates(configuration.forciblyAcceptedCertificates);
            }
        }

        ircServerConnection.registerListener(new MessageListener());

        ircServerConnection.connect();
        // TODO: Blocks forever
    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    @SuppressWarnings("unused")
    public void postInit(FMLPostInitializationEvent event) {

    }
}