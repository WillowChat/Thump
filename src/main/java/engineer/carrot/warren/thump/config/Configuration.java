package engineer.carrot.warren.thump.config;

import java.util.List;

public class Configuration {
    public List<ServerConfiguration> serverConfigurations;

    public Configuration(List<ServerConfiguration> serverConfigurations) {
        this.serverConfigurations = serverConfigurations;
    }
}
