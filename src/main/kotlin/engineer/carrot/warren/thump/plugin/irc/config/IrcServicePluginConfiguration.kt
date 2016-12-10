package engineer.carrot.warren.thump.plugin.irc.config

import engineer.carrot.warren.thump.api.IThumpServicePluginConfig
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.client.config.IConfigElement

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
        configuration.setCategoryPropertyOrder(CATEGORY, arrayListOf(LOG_IRC_TO_SERVER_CONSOLE_KEY))

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
        configuration.setCategoryPropertyOrder(CATEGORY, arrayListOf(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY))
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
        configuration.setCategoryPropertyOrder(CATEGORY, listOf(CHANNEL_MESSAGE_KEY, CHANNEL_ACTION_KEY,
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

class IrcServicePluginConnectionsConfiguration(private val configuration: Configuration) {
    val servers = mutableMapOf<String, IrcServerConfiguration>()
    private val connectionsPrefix = "connections."

    init {
        var serverIDs = configuration.categoryNames
            .filter { it.startsWith(connectionsPrefix) }
            .filter { it.indexOf('.', startIndex = connectionsPrefix.length) == -1 }
            .map { it.substring(connectionsPrefix.length) }

        if (serverIDs.isEmpty()) {
            serverIDs = listOf("example_connection_id_CHANGE_ME")
        }

        serverIDs
            .map { IrcServerConfiguration(it, configuration) }
            .filterNot { it.server.isEmpty() }
            .forEach { this.servers.put(it.ID, it) }
    }

    fun connectionConfigElements(): List<IConfigElement> {
        val validServerCategoryNames = configuration.categoryNames
                .filter { it.startsWith(connectionsPrefix) }
                .filter { it.indexOf('.', startIndex = connectionsPrefix.length) == -1 }

        return validServerCategoryNames.map { ConfigElement(configuration.getCategory(it)) }
    }
}
