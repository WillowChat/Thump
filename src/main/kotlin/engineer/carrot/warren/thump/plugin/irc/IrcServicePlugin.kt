package engineer.carrot.warren.thump.plugin.irc

import engineer.carrot.warren.thump.api.*
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.plugin.irc.command.handler.IrcServiceCommandHandler
import engineer.carrot.warren.thump.plugin.irc.config.IrcServicePluginConfiguration
import engineer.carrot.warren.thump.plugin.irc.handler.MessageHandler
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.common.MinecraftForge

@ThumpServicePlugin
object IrcServicePlugin : IThumpServicePlugin {
    override val id = "irc"

    lateinit var configuration: IrcServicePluginConfiguration

    override lateinit var commandHandler: ICommandHandler

    val wrappersManager: IWrappersManager = IrcRunnerWrappersManager()

    private lateinit var sink: IThumpMinecraftSink

    override fun configure(context: ThumpPluginContext) {
        val config = context.configuration
        sink = context.minecraftSink

        configuration = IrcServicePluginConfiguration(configuration = config)
        configuration.load()
        configuration.save()

        wrappersManager.removeAll()

        populateConnectionManager()

        commandHandler = IrcServiceCommandHandler(wrappersManager)
    }

    override fun start() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Starting ${entry.key}")
            wrappersManager.start(entry.key)
        }
    }

    override fun stop() {
        wrappersManager.wrappers.forEach { entry ->
            LogHelper.info("Stopping ${entry.key}")
            wrappersManager.stop(entry.key, shouldReconnect = false)
        }
    }

    override fun anyConnectionsMatch(name: String): Boolean {
        return wrappersManager.anyWrappersMatch(name)
    }

    override fun onMinecraftMessage(message: String) {
        wrappersManager.wrappers.forEach { entry ->
            entry.value.sendMessageToAll(message)
        }
    }

    fun populateConnectionManager() {
        val servers = configuration.connections.servers

        if (servers.isEmpty()) {
            LogHelper.warn("Found no valid server configurations to load - check thump/services/irc.cfg!")
        }

        for (serverConfiguration in servers.values) {
            LogHelper.info("adding ${serverConfiguration.server}:${serverConfiguration.port} as ${serverConfiguration.nickname}")

            wrappersManager.initialise(serverConfiguration, IrcServicePlugin.configuration.general, sink)
        }
    }

    override fun status(): List<String> {
        val status = mutableListOf<String>()

        val wrappers = wrappersManager.wrappers
        val connections = wrappers.values
        if (connections.isEmpty()) {
            status += "IRC is not configured to connect to any servers."

            return status
        }

        for ((id, wrapper) in wrappers) {
            val state = wrapper.state

            var wrapperStatus = " $id: $state"

            if (state == WrapperState.RUNNING) {
                val channelsToJoin = configuration.connections.servers[id]?.channels?.keys ?: listOf<String>()

                val joinedChannels = wrapper.channels ?: setOf()
                val joinedChannelsMessage = if (channelsToJoin.isEmpty()) {
                    ", no channels configured"
                } else {
                    var text = ", channels: "

                    val channelsOutput = mutableListOf<String>()

                    for (channel in channelsToJoin) {
                        if (joinedChannels.contains(channel)) {
                            channelsOutput.add(TextFormatting.GREEN.toString() + channel + TextFormatting.RESET.toString())
                        } else {
                            channelsOutput.add(TextFormatting.RED.toString() + channel + TextFormatting.RESET.toString())
                        }
                    }

                    text += channelsOutput.joinToString(separator = ", ")

                    text
                }

                wrapperStatus += joinedChannelsMessage

                status += wrapperStatus
            }

            val ircState = wrappers[id]?.ircState?.connection?.lifecycle
            if (ircState != null) {
                status += "  IRC state: $ircState"
            }
        }

        return status
    }

}