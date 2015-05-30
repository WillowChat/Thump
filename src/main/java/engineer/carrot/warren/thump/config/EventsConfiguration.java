package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;

public class EventsConfiguration {
    public MinecraftEvents minecraft;
    public IrcEvents irc;

    public EventsConfiguration(Configuration configuration) {
        this.minecraft = new MinecraftEvents(configuration);
        this.irc = new IrcEvents(configuration);
    }

    public static class MinecraftEvents {
        private static final String CATEGORY = "events.minecraft";

        public boolean playerJoined = true;
        private static final String PLAYER_JOINED_KEY = "PlayerJoined";

        public boolean playerLeft = true;
        private static final String PLAYER_LEFT_KEY = "PlayerLeft";

        public boolean playerAchievement = true;
        private static final String PLAYER_ACHIEVEMENT_KEY = "PlayerAchievement";

        public boolean playerMessage = true;
        private static final String PLAYER_MESSAGE_KEY = "PlayerMessage";

        public boolean playerAction = true;
        private static final String PLAYER_ACTION_KEY = "PlayerAction";

        public boolean playerDeath = true;
        private static final String PLAYER_DEATH_KEY = "PlayerDeath";

        public boolean serverMessage = true;
        private static final String SERVER_MESSAGE_KEY = "ServerMessage";

        public boolean serverAction = true;
        private static final String SERVER_ACTION_KEY = "ServerAction";

        public MinecraftEvents(Configuration configuration) {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYER_JOINED_KEY, PLAYER_LEFT_KEY,
                    PLAYER_ACHIEVEMENT_KEY, PLAYER_MESSAGE_KEY, PLAYER_MESSAGE_KEY, PLAYER_ACTION_KEY, PLAYER_DEATH_KEY,
                    SERVER_MESSAGE_KEY, SERVER_ACTION_KEY
            ));
            this.playerJoined = configuration.getBoolean(PLAYER_JOINED_KEY, CATEGORY, this.playerJoined, "");
            this.playerLeft = configuration.getBoolean(PLAYER_LEFT_KEY, CATEGORY, this.playerLeft, "");
            this.playerAchievement = configuration.getBoolean(PLAYER_ACHIEVEMENT_KEY, CATEGORY, this.playerAchievement, "");
            this.playerMessage = configuration.getBoolean(PLAYER_MESSAGE_KEY, CATEGORY, this.playerMessage, "");
            this.playerAction = configuration.getBoolean(PLAYER_ACTION_KEY, CATEGORY, this.playerAction, "");
            this.playerDeath = configuration.getBoolean(PLAYER_DEATH_KEY, CATEGORY, this.playerDeath, "");

            this.serverMessage = configuration.getBoolean(SERVER_MESSAGE_KEY, CATEGORY, this.serverMessage, "");
            this.serverAction = configuration.getBoolean(SERVER_ACTION_KEY, CATEGORY, this.serverAction, "");
        }
    }

    public static class IrcEvents {
        private static final String CATEGORY = "events.irc";

        public boolean channelMessage = true;
        private static final String CHANNEL_MESSAGE_KEY = "ChannelMessage";

        public boolean channelAction = true;
        private static final String CHANNEL_ACTION_KEY = "ChannelAction";

        public IrcEvents(Configuration configuration) {
            configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY));
            this.channelMessage = configuration.getBoolean(CHANNEL_MESSAGE_KEY, CATEGORY, this.channelMessage, "");
            this.channelAction = configuration.getBoolean(CHANNEL_ACTION_KEY, CATEGORY, this.channelAction, "");
        }
    }
}
