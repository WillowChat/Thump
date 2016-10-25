package engineer.carrot.warren.thump.plugin.irc.config

import com.google.common.collect.Lists
import com.google.common.collect.Sets
import engineer.carrot.warren.thump.api.IThumpServicePluginConfig
import engineer.carrot.warren.thump.helper.PredicateHelper
import net.minecraftforge.common.config.Configuration
import java.util.*

class IrcServicePluginConfiguration(val configuration: Configuration): IThumpServicePluginConfig {

    lateinit var general: IrcServicePluginGeneralConfiguration
    lateinit var events: IrcServicePluginEventsConfiguration
    lateinit var formats: IrcServicePluginFormatsConfiguration
    lateinit var connections: IrcServicePluginConnectionsConfiguration

    override fun load() {
        configuration.load()

        this.general = IrcServicePluginGeneralConfiguration(this.configuration)
        this.events = IrcServicePluginEventsConfiguration(this.configuration)
        this.formats = IrcServicePluginFormatsConfiguration(this.configuration)
        this.connections = IrcServicePluginConnectionsConfiguration(this.configuration)
    }

    override fun save() {
        this.configuration.save()
    }

}

class IrcServicePluginGeneralConfiguration(val configuration: Configuration) {
    var logIrcToServerConsole = true
    var logRawIRCLinesToServerConsole = false

    init {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(LOG_IRC_TO_SERVER_CONSOLE_KEY))

        this.logIrcToServerConsole = configuration.getBoolean(LOG_IRC_TO_SERVER_CONSOLE_KEY, CATEGORY, this.logIrcToServerConsole, "")

        this.logRawIRCLinesToServerConsole = configuration.getBoolean(LOG_RAW_INCOMING_LINES_TO_CONSOLE_KEY, CATEGORY, this.logRawIRCLinesToServerConsole, LOG_RAW_INCOMING_LINES_TO_CONSOLE_COMMENT)
    }

    companion object {
        private val CATEGORY = "general"
        private val LOG_IRC_TO_SERVER_CONSOLE_KEY = "LogIRCToServerConsole"
        private val LOG_RAW_INCOMING_LINES_TO_CONSOLE_KEY = "LogRawIncomingIRCLinesToConsole"
        private val LOG_RAW_INCOMING_LINES_TO_CONSOLE_COMMENT = "Dumps raw incoming IRC lines to the console using INFO level - leave disabled when you don't need it!"
    }
}

class IrcServicePluginEventsConfiguration(val configuration: Configuration) {
    var channelMessage = true
    var channelAction = true

    init {
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY))
        this.channelMessage = configuration.getBoolean(CHANNEL_MESSAGE_KEY, CATEGORY, this.channelMessage, "")
        this.channelAction = configuration.getBoolean(CHANNEL_ACTION_KEY, CATEGORY, this.channelAction, "")
    }

    companion object {
        private val CATEGORY = "events"
        private val CHANNEL_MESSAGE_KEY = "ChannelMessage"
        private val CHANNEL_ACTION_KEY = "ChannelAction"
    }
}

class IrcServicePluginFormatsConfiguration(val configuration: Configuration) {
    var channelMessage = "{c}: <{u}> {m}"
    var channelAction = "{c}: * {u} {m}"
    var networkReady = "IRC network ready: {m}"
    var networkDisconnected = "IRC network disconnected: {m}"

    init {
        configuration.setCategoryComment("formats", "Formatting tokens: {u} -> user, {c} -> channel, {m} -> message, {s} -> server")
        configuration.setCategoryPropertyOrder(CATEGORY, Lists.newArrayList(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY,
                NETWORK_READY_KEY, NETWORK_DISCONNECTED_KEY))
        this.channelMessage = configuration.getString(CHANNEL_MESSAGE_KEY, CATEGORY, this.channelMessage, "")
        this.channelAction = configuration.getString(CHANNEL_ACTION_KEY, CATEGORY, this.channelAction, "")
        this.networkReady = configuration.getString(NETWORK_READY_KEY, CATEGORY, this.networkReady, "")
        this.networkDisconnected = configuration.getString(NETWORK_DISCONNECTED_KEY, CATEGORY, this.networkDisconnected, "")
    }

    companion object {
        private val CATEGORY = "formats"
        private val CHANNEL_MESSAGE_KEY = "ChannelMessage"
        private val CHANNEL_ACTION_KEY = "ChannelAction"
        private val NETWORK_READY_KEY = "NetworkReady"
        private val NETWORK_DISCONNECTED_KEY = "NetworkDisconnected"
    }
}

class IrcServicePluginConnectionsConfiguration(configuration: Configuration) {
    var servers: MutableMap<String, IrcServerConfiguration> = HashMap()

    init {
        val serverIDs = Sets.newHashSet(
            Sets.filter(
                configuration.categoryNames,
                PredicateHelper.StartsWithPredicate("connections.")
            ).filter { it.indexOf('.', startIndex = 12) == -1 }
            .map { it.substring(12) }
        )

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
