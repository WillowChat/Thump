package engineer.carrot.warren.thump.config

import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.config.IConfigElement
import java.io.File

class ModConfiguration {
    lateinit var minecraftGeneralConfiguration: Configuration

    lateinit var events: EventsConfiguration

    // Mod configuration
    lateinit var general: GeneralConfiguration
    lateinit var commands: CommandsConfiguration
    lateinit var formats: FormatsConfiguration

    fun initialise(configDirectory: File) {
        this.minecraftGeneralConfiguration = Configuration(File(configDirectory, GENERAL_NAME), GENERAL_VERSION)
    }

    fun generateConfigElements(): List<IConfigElement> {
        val categories = listOf(GeneralConfiguration.CATEGORY, CommandsConfiguration.CATEGORY, EventsConfiguration.MinecraftEvents.CATEGORY, FormatsConfiguration.MinecraftFormats.CATEGORY)

        return categories.map { ConfigElement(minecraftGeneralConfiguration.getCategory(it)) }
    }

    fun loadAllConfigurations() {
        this.loadGeneralConfiguration()
    }

    fun saveAllConfigurations() {
        this.saveGeneralConfiguration()
    }

    fun loadGeneralConfiguration() {
        this.minecraftGeneralConfiguration.load()

        this.general = GeneralConfiguration(this.minecraftGeneralConfiguration)
        this.events = EventsConfiguration(this.minecraftGeneralConfiguration)
        this.commands = CommandsConfiguration(this.minecraftGeneralConfiguration)
        this.formats = FormatsConfiguration(this.minecraftGeneralConfiguration)
    }

    fun saveGeneralConfiguration() {
        this.minecraftGeneralConfiguration.save()
    }

    companion object {

        private val GENERAL_NAME = "general.cfg"
        private val GENERAL_VERSION = "1"

    }
}
