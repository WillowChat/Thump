package engineer.carrot.warren.thump.config

import engineer.carrot.warren.thump.plugin.irc.config.IrcServersConfiguration
import net.minecraftforge.common.config.Configuration

import java.io.File

class ModConfiguration {
    private lateinit var generalConfiguration: Configuration

    lateinit var events: EventsConfiguration

    // Mod configuration
    lateinit var general: GeneralConfiguration
    lateinit var commands: CommandsConfiguration
    lateinit var formats: FormatsConfiguration

    fun initialise(configDirectory: File) {
        this.generalConfiguration = Configuration(File(configDirectory, GENERAL_NAME), GENERAL_VERSION)
    }

    fun loadAllConfigurations() {
        this.loadGeneralConfiguration()
    }

    fun saveAllConfigurations() {
        this.saveGeneralConfiguration()
    }

    fun loadGeneralConfiguration() {
        this.generalConfiguration.load()

        this.general = GeneralConfiguration(this.generalConfiguration)
        this.events = EventsConfiguration(this.generalConfiguration)
        this.commands = CommandsConfiguration(this.generalConfiguration)
        this.formats = FormatsConfiguration(this.generalConfiguration)
    }

    fun saveGeneralConfiguration() {
        this.generalConfiguration.save()
    }

    companion object {

        private val GENERAL_NAME = "general.cfg"
        private val GENERAL_VERSION = "1"

    }
}
