package engineer.carrot.warren.thump.config

import com.google.common.collect.Lists
import net.minecraftforge.common.config.Configuration

class GeneralConfiguration(configuration: Configuration) {
    var logIrcToServerConsole = true
    var obfuscateUserSourceFromMinecraft = true

    init {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(LOG_IRC_TO_SERVER_CONSOLE_KEY))

        this.logIrcToServerConsole = configuration.getBoolean(LOG_IRC_TO_SERVER_CONSOLE_KEY, CATEGORY, this.logIrcToServerConsole, "")

        this.obfuscateUserSourceFromMinecraft = configuration.getBoolean(OBFUSCATE_USER_SOURCE_FROM_MINECRAFT, CATEGORY, this.obfuscateUserSourceFromMinecraft, OBFUSCATE_USER_SOURCE_FROM_MINECRAFT_COMMENT)
    }

    companion object {
        private val CATEGORY = "general"
        private val LOG_IRC_TO_SERVER_CONSOLE_KEY = "LogIRCToServerConsole"
        private val OBFUSCATE_USER_SOURCE_FROM_MINECRAFT = "ObfuscateUserSourceFromMinecraft"
        private val OBFUSCATE_USER_SOURCE_FROM_MINECRAFT_COMMENT = "Inserts a zero-width character in to source usernames going from Minecraft to IRC - commonly used to prevent unnecessary pings."
    }
}
