package engineer.carrot.warren.thump.config

import com.google.common.collect.Lists
import net.minecraftforge.common.config.Configuration

class CommandsConfiguration(configuration: Configuration) {
    var players = true
    var prefix = "!"

    init {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PREFIX_KEY, PLAYERS_KEY))
        this.prefix = configuration.getString(PREFIX_KEY, CATEGORY, this.prefix, "")
        this.players = configuration.getBoolean(PLAYERS_KEY, CATEGORY, this.players, "")
    }

    companion object {
        val CATEGORY = "commands"
        private val PREFIX_KEY = "Prefix"
        private val PLAYERS_KEY = "Players"
    }
}