package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;

public class FormatsConfiguration {
    public MinecraftFormats minecraft;
    public IrcFormats irc;

    public FormatsConfiguration(Configuration configuration) {
        configuration.setCategoryComment("formats", "Formatting tokens: {u} -> user, {c} -> channel, {m} -> message\nNote that only tokens listed in the defaults are supported for each message!");

        this.minecraft = new MinecraftFormats(configuration);
        this.irc = new IrcFormats(configuration);
    }

    public static class IrcFormats {
        private static final String CATEGORY = "formats.irc";

        public String channelMessage = "{c}: <{u}> {m}";
        private static final String CHANNEL_MESSAGE_KEY = "ChannelMessage";

        public String channelAction = "{c}: * {u} {m}";
        private static final String CHANNEL_ACTION_KEY = "ChannelAction";

        public String networkReady = "IRC network ready: {m}";
        private static final String NETWORK_READY_KEY = "NetworkReady";

        public String networkDisconnected = "IRC network disconnected: {m}";
        private static final String NETWORK_DISCONNECTED_KEY = "NetworkDisconnected";

        public IrcFormats(Configuration configuration) {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY,
                    NETWORK_READY_KEY, NETWORK_DISCONNECTED_KEY));
            this.channelMessage = configuration.getString(CHANNEL_MESSAGE_KEY, CATEGORY, this.channelMessage, "");
            this.channelAction = configuration.getString(CHANNEL_ACTION_KEY, CATEGORY, this.channelAction, "");
            this.networkReady = configuration.getString(NETWORK_READY_KEY, CATEGORY, this.networkReady, "");
            this.networkDisconnected = configuration.getString(NETWORK_DISCONNECTED_KEY, CATEGORY, this.networkDisconnected, "");
        }
    }

    public static class MinecraftFormats {
        private static final String CATEGORY = "formats.minecraft";

        public String playerMessage = "<{u}> {m}";
        private static final String PLAYER_MESSAGE_KEY = "PlayerMessage";

        public String playerAction = "* {u} {m}";
        private static final String PLAYER_ACTION_KEY = "PlayerAction";

        public String playerAchievement = "{m}";
        private static final String PLAYER_ACHIEVEMENT_KEY = "PlayerAchievement";

        public String playerDeath = "{m}";
        private static final String PLAYER_DEATH_KEY = "PlayerDeath";

        public String playerJoined = "{u} has joined the game";
        private static final String PLAYER_JOINED_KEY = "PlayerJoined";

        public String playerLeft = "{u} has left the game";
        private static final String PLAYER_LEFT_KEY = "PlayerLeft";

        public String playersOnline = "Players online: {m}";
        private static final String PLAYERS_ONLINE_KEY = "PlayersOnline";

        public String playersOnlineNone = "There are no players online";
        private static final String PLAYERS_ONLINE_NONE_KEY = "PlayersOnlineNone";

        public MinecraftFormats(Configuration configuration) {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYER_MESSAGE_KEY, PLAYER_ACTION_KEY,
                    PLAYER_ACHIEVEMENT_KEY, PLAYER_DEATH_KEY, PLAYER_JOINED_KEY, PLAYER_LEFT_KEY, PLAYERS_ONLINE_KEY,
                    PLAYERS_ONLINE_NONE_KEY));
            this.playerMessage = configuration.getString(PLAYER_MESSAGE_KEY, CATEGORY, this.playerMessage, "");
            this.playerAction = configuration.getString(PLAYER_ACTION_KEY, CATEGORY, this.playerAction, "");
            this.playerAchievement = configuration.getString(PLAYER_ACHIEVEMENT_KEY, CATEGORY, this.playerAchievement, "");
            this.playerDeath = configuration.getString(PLAYER_DEATH_KEY, CATEGORY, this.playerDeath, "");
            this.playerJoined = configuration.getString(PLAYER_JOINED_KEY, CATEGORY, this.playerJoined, "");
            this.playerLeft = configuration.getString(PLAYER_LEFT_KEY, CATEGORY, this.playerLeft, "");
            this.playersOnline = configuration.getString(PLAYERS_ONLINE_KEY, CATEGORY, this.playersOnline, "");
            this.playersOnlineNone = configuration.getString(PLAYERS_ONLINE_NONE_KEY, CATEGORY, this.playersOnlineNone, "");
        }
    }
}
