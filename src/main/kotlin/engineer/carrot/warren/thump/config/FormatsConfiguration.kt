package engineer.carrot.warren.thump.config

import com.google.common.collect.Lists
import net.minecraftforge.common.config.Configuration

class FormatsConfiguration(configuration: Configuration) {
    var minecraft: MinecraftFormats
    var irc: IrcFormats

    init {
        configuration.setCategoryComment("formats", "Formatting tokens: {u} -> user, {c} -> channel, {m} -> message\nNote that only tokens listed in the defaults are supported for each message!")

        this.minecraft = MinecraftFormats(configuration)
        this.irc = IrcFormats(configuration)
    }

    class IrcFormats(configuration: Configuration) {
        var channelMessage = "{c}: <{u}> {m}"
        var channelAction = "{c}: * {u} {m}"
        var networkReady = "IRC network ready: {m}"
        var networkDisconnected = "IRC network disconnected: {m}"

        init {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY,
                    NETWORK_READY_KEY, NETWORK_DISCONNECTED_KEY))
            this.channelMessage = configuration.getString(CHANNEL_MESSAGE_KEY, CATEGORY, this.channelMessage, "")
            this.channelAction = configuration.getString(CHANNEL_ACTION_KEY, CATEGORY, this.channelAction, "")
            this.networkReady = configuration.getString(NETWORK_READY_KEY, CATEGORY, this.networkReady, "")
            this.networkDisconnected = configuration.getString(NETWORK_DISCONNECTED_KEY, CATEGORY, this.networkDisconnected, "")
        }

        companion object {
            private val CATEGORY = "formats.irc"
            private val CHANNEL_MESSAGE_KEY = "ChannelMessage"
            private val CHANNEL_ACTION_KEY = "ChannelAction"
            private val NETWORK_READY_KEY = "NetworkReady"
            private val NETWORK_DISCONNECTED_KEY = "NetworkDisconnected"
        }
    }

    class MinecraftFormats(configuration: Configuration) {
        var playerMessage = "<{u}> {m}"
        var playerAction = "* {u} {m}"
        var playerAchievement = "{m} \uD83C\uDF1F"
        var playerDeath = "{m} \uD83D\uDC7B"
        var playerJoined = "{u} has joined the game"
        var playerLeft = "{u} has left the game"
        var playersOnline = "Players online: {m}"
        var playersOnlineNone = "There are no players online"

        init {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYER_MESSAGE_KEY, PLAYER_ACTION_KEY,
                    PLAYER_ACHIEVEMENT_KEY, PLAYER_DEATH_KEY, PLAYER_JOINED_KEY, PLAYER_LEFT_KEY, PLAYERS_ONLINE_KEY,
                    PLAYERS_ONLINE_NONE_KEY))
            this.playerMessage = configuration.getString(PLAYER_MESSAGE_KEY, CATEGORY, this.playerMessage, "")
            this.playerAction = configuration.getString(PLAYER_ACTION_KEY, CATEGORY, this.playerAction, "")
            this.playerAchievement = configuration.getString(PLAYER_ACHIEVEMENT_KEY, CATEGORY, this.playerAchievement, "")
            this.playerDeath = configuration.getString(PLAYER_DEATH_KEY, CATEGORY, this.playerDeath, "")
            this.playerJoined = configuration.getString(PLAYER_JOINED_KEY, CATEGORY, this.playerJoined, "")
            this.playerLeft = configuration.getString(PLAYER_LEFT_KEY, CATEGORY, this.playerLeft, "")
            this.playersOnline = configuration.getString(PLAYERS_ONLINE_KEY, CATEGORY, this.playersOnline, "")
            this.playersOnlineNone = configuration.getString(PLAYERS_ONLINE_NONE_KEY, CATEGORY, this.playersOnlineNone, "")
        }

        companion object {
            private val CATEGORY = "formats.minecraft"
            private val PLAYER_MESSAGE_KEY = "PlayerMessage"
            private val PLAYER_ACTION_KEY = "PlayerAction"
            private val PLAYER_ACHIEVEMENT_KEY = "PlayerAchievement"
            private val PLAYER_DEATH_KEY = "PlayerDeath"
            private val PLAYER_JOINED_KEY = "PlayerJoined"
            private val PLAYER_LEFT_KEY = "PlayerLeft"
            private val PLAYERS_ONLINE_KEY = "PlayersOnline"
            private val PLAYERS_ONLINE_NONE_KEY = "PlayersOnlineNone"
        }
    }
}
