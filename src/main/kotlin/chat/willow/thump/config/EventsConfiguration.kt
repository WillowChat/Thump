package chat.willow.thump.config

import com.google.common.collect.Lists
import net.minecraftforge.common.config.Configuration

class EventsConfiguration(configuration: Configuration) {
    var minecraft: MinecraftEvents

    init {
        this.minecraft = MinecraftEvents(configuration)
    }

    class MinecraftEvents(configuration: Configuration) {
        var playerJoined = true
        var playerLeft = true
        var playerAchievement = true
        var playerMessage = true
        var playerAction = true
        var playerDeath = true
        var serverMessage = true
        var serverAction = true

        init {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYER_JOINED_KEY, PLAYER_LEFT_KEY,
                    PLAYER_ACHIEVEMENT_KEY, PLAYER_MESSAGE_KEY, PLAYER_MESSAGE_KEY, PLAYER_ACTION_KEY, PLAYER_DEATH_KEY,
                    SERVER_MESSAGE_KEY, SERVER_ACTION_KEY))
            this.playerJoined = configuration.getBoolean(PLAYER_JOINED_KEY, CATEGORY, this.playerJoined, "")
            this.playerLeft = configuration.getBoolean(PLAYER_LEFT_KEY, CATEGORY, this.playerLeft, "")
            this.playerAchievement = configuration.getBoolean(PLAYER_ACHIEVEMENT_KEY, CATEGORY, this.playerAchievement, "")
            this.playerMessage = configuration.getBoolean(PLAYER_MESSAGE_KEY, CATEGORY, this.playerMessage, "")
            this.playerAction = configuration.getBoolean(PLAYER_ACTION_KEY, CATEGORY, this.playerAction, "")
            this.playerDeath = configuration.getBoolean(PLAYER_DEATH_KEY, CATEGORY, this.playerDeath, "")

            this.serverMessage = configuration.getBoolean(SERVER_MESSAGE_KEY, CATEGORY, this.serverMessage, "")
            this.serverAction = configuration.getBoolean(SERVER_ACTION_KEY, CATEGORY, this.serverAction, "")
        }

        companion object {
            val CATEGORY = "minecraft_events"
            private val PLAYER_JOINED_KEY = "PlayerJoined"
            private val PLAYER_LEFT_KEY = "PlayerLeft"
            private val PLAYER_ACHIEVEMENT_KEY = "PlayerAchievement"
            private val PLAYER_MESSAGE_KEY = "PlayerMessage"
            private val PLAYER_ACTION_KEY = "PlayerAction"
            private val PLAYER_DEATH_KEY = "PlayerDeath"
            private val SERVER_MESSAGE_KEY = "ServerMessage"
            private val SERVER_ACTION_KEY = "ServerAction"
        }
    }

}
