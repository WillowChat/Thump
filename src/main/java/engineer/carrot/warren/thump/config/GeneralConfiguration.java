package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;

public class GeneralConfiguration {
    private static final String CATEGORY = "general";

    public boolean logIrcToServerConsole = true;
    private static final String LOG_IRC_TO_SERVER_CONSOLE_KEY = "LogIRCToServerConsole";

    public GeneralConfiguration(Configuration configuration) {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(LOG_IRC_TO_SERVER_CONSOLE_KEY));
        this.logIrcToServerConsole = configuration.getBoolean(LOG_IRC_TO_SERVER_CONSOLE_KEY, CATEGORY, this.logIrcToServerConsole, "");
    }
}
