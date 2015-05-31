package engineer.carrot.warren.thump.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfiguration {
    private Configuration generalConfiguration;
    private Configuration serversConfiguration;

    private static final String GENERAL_NAME = "general.cfg";
    private static final String GENERAL_VERSION = "1";
    private static final String SERVERS_NAME = "servers.cfg";
    private static final String SERVERS_VERSION = "1";

    private EventsConfiguration events;
    private GeneralConfiguration general;
    private ServersConfiguration servers;
    private CommandsConfiguration commands;

    public ModConfiguration() {

    }

    public void initialise(File configDirectory) {
        this.generalConfiguration = new Configuration(new File(configDirectory, GENERAL_NAME), GENERAL_VERSION);
        this.serversConfiguration = new Configuration(new File(configDirectory, SERVERS_NAME), SERVERS_VERSION);
    }

    public void loadAllConfigurations() {
        this.loadGeneralConfiguration();
        this.loadServersConfiguration();
    }

    public void saveAllConfigurations() {
        this.saveGeneralConfiguration();
        this.saveServersConfiguration();
    }

    public void loadGeneralConfiguration() {
        this.generalConfiguration.load();

        this.general = new GeneralConfiguration(this.generalConfiguration);
        this.events = new EventsConfiguration(this.generalConfiguration);
        this.commands = new CommandsConfiguration(this.generalConfiguration);
    }

    public void loadServersConfiguration() {
        this.serversConfiguration.load();

        this.servers = new ServersConfiguration(this.serversConfiguration);
    }

    public void saveGeneralConfiguration() {
        this.generalConfiguration.save();
    }

    public void saveServersConfiguration() {
        this.serversConfiguration.save();
    }

    // Mod configuration
    public GeneralConfiguration getGeneral() {
        return this.general;
    }

    public EventsConfiguration getEvents() {
        return this.events;
    }

    public CommandsConfiguration getCommands() {
        return this.commands;
    }

    // Servers configuration
    public ServersConfiguration getServers() {
        return this.servers;
    }
}
