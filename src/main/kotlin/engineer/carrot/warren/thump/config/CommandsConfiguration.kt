package engineer.carrot.warren.thump.config

import com.google.common.collect.Lists
import net.minecraftforge.common.config.Configuration

class CommandsConfiguration(configuration: Configuration) {
    var players = true

    init {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYERS_KEY))
        this.players = configuration.getBoolean(PLAYERS_KEY, CATEGORY, this.players, "")
    }

    companion object {
        private val CATEGORY = "commands"
        private val PLAYERS_KEY = "Players"
    }
}