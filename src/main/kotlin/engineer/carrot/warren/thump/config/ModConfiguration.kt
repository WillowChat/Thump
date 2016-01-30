package engineer.carrot.warren.thump.config

import net.minecraftforge.common.config.Configuration

import java.io.File

class ModConfiguration {
    private lateinit var generalConfiguration: Configuration
    private lateinit var serversConfiguration: Configuration

    lateinit var events: EventsConfiguration

    // Mod configuration
    lateinit var general: GeneralConfiguration

    // Servers configuration
    lateinit var servers: ServersConfiguration
    lateinit var commands: CommandsConfiguration
    lateinit var formats: FormatsConfiguration

    fun initialise(configDirectory: File) {
        this.generalConfiguration = Configuration(File(configDirectory, GENERAL_NAME), GENERAL_VERSION)
        this.serversConfiguration = Configuration(File(configDirectory, SERVERS_NAME), SERVERS_VERSION)
    }

    fun loadAllConfigurations() {
        this.loadGeneralConfiguration()
        this.loadServersConfiguration()
    }

    fun saveAllConfigurations() {
        this.saveGeneralConfiguration()
        this.saveServersConfiguration()
    }

    fun loadGeneralConfiguration() {
        this.generalConfiguration.load()

        this.general = GeneralConfiguration(this.generalConfiguration)
        this.events = EventsConfiguration(this.generalConfiguration)
        this.commands = CommandsConfiguration(this.generalConfiguration)
        this.formats = FormatsConfiguration(this.generalConfiguration)
    }

    fun loadServersConfiguration() {
        this.serversConfiguration.load()

        this.servers = ServersConfiguration(this.serversConfiguration)
    }

    fun saveGeneralConfiguration() {
        this.generalConfiguration.save()
    }

    fun saveServersConfiguration() {
        this.serversConfiguration.save()
    }

    companion object {

        private val GENERAL_NAME = "general.cfg"
        private val GENERAL_VERSION = "1"
        private val SERVERS_NAME = "servers.cfg"
        private val SERVERS_VERSION = "1"
    }
}
