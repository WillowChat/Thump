package engineer.carrot.warren.thump.config;

import com.google.common.collect.Sets;
import engineer.carrot.warren.thump.util.helper.PredicateHelper;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServersConfiguration {
    public Map<String, ServerConfiguration> servers = new HashMap<String, ServerConfiguration>();

    public ServersConfiguration(Configuration configuration) {
        Set<String> serverIDs = Sets.newHashSet(Sets.filter(
                configuration.getCategoryNames(),
                new PredicateHelper.DoesNotContainPredicate(".")
        ));

        if (serverIDs.isEmpty()) {
            serverIDs.add("example");
        }

        for (String serverID : serverIDs) {
            ServerConfiguration server = new ServerConfiguration(serverID, configuration);

            if (server.server.isEmpty()) {
                continue;
            }

            this.servers.put(server.ID, server);
        }
    }
}
