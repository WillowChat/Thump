package engineer.carrot.warren.thump.plugin.irc.config

import com.google.common.collect.Sets
import engineer.carrot.warren.thump.helper.PredicateHelper
import net.minecraftforge.common.config.Configuration
import java.util.*

class IrcServersConfiguration(configuration: Configuration) {
    var servers: MutableMap<String, IrcServerConfiguration> = HashMap()

    init {
        val serverIDs = Sets.newHashSet(Sets.filter(
                configuration.categoryNames,
                PredicateHelper.DoesNotContainPredicate(".")))

        if (serverIDs.isEmpty()) {
            serverIDs.add("example")
        }

        for (serverID in serverIDs) {
            val server = IrcServerConfiguration(serverID, configuration)

            if (server.server.isEmpty()) {
                continue
            }

            this.servers.put(server.ID, server)
        }
    }
}
