package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;

public class CommandsConfiguration {
    private static final String CATEGORY = "commands";

    public boolean players = true;
    private static final String PLAYERS_KEY = "Players";

    public CommandsConfiguration(Configuration configuration) {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(PLAYERS_KEY));
        this.players = configuration.getBoolean(PLAYERS_KEY, CATEGORY, this.players, "");
    }
}
