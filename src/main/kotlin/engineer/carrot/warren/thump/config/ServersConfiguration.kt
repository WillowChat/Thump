package engineer.carrot.warren.thump.config

import com.google.common.collect.Sets
import engineer.carrot.warren.thump.helper.PredicateHelper
import net.minecraftforge.common.config.Configuration
import java.util.*

class ServersConfiguration(configuration: Configuration) {
    var servers: MutableMap<String, ServerConfiguration> = HashMap()

    init {
        val serverIDs = Sets.newHashSet(Sets.filter(
                configuration.categoryNames,
                PredicateHelper.DoesNotContainPredicate(".")))

        if (serverIDs.isEmpty()) {
            serverIDs.add("example")
        }

        for (serverID in serverIDs) {
            val server = ServerConfiguration(serverID, configuration)

            if (server.server.isEmpty()) {
                continue
            }

            this.servers.put(server.ID, server)
        }
    }
}
