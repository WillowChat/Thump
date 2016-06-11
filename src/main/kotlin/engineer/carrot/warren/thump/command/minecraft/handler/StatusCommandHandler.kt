package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import engineer.carrot.warren.thump.plugin.irc.WrapperState
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting

class StatusCommandHandler(private val manager: IWrappersManager) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        val connections = this.manager.wrappers.values
        if (connections.isEmpty()) {
            sender.addChatMessage(TextComponentString("Thump is not configured to connect to any servers."))
            return
        }

        sender.addChatMessage(TextComponentString("Thump connection statuses:"))

        for ((id, wrapper) in this.manager.wrappers) {
            val state = wrapper.state

            val statusMessage = TextComponentString(" $id: $state")

            if (state == WrapperState.RUNNING) {
                val ircPlugin = Thump.firstIrcPlugin

                // fixme: remove hack
                val channelsToJoin: Set<String> = if (ircPlugin != null) {
                    ircPlugin.configWrapper.config.servers[id]?.channels?.keys ?: Sets.newHashSet()
                } else {
                    Sets.newHashSet()
                }

                val joinedChannels = wrapper.channels ?: setOf()
                val joinedChannelsMessage: TextComponentString = if (channelsToJoin.isEmpty()) {
                    TextComponentString(", no channels configured")
                } else {
                    val text = TextComponentString(", channels: ")

                    val channelsOutput: MutableList<String> = Lists.newArrayList()

                    for (channel in channelsToJoin) {
                        if (joinedChannels.contains(channel)) {
                            channelsOutput.add(TextFormatting.GREEN.toString() + channel + TextFormatting.RESET.toString())
                        } else {
                            channelsOutput.add(TextFormatting.RED.toString() + channel + TextFormatting.RESET.toString())
                        }
                    }

                    text.appendText(Joiner.on(", ").join(channelsOutput))

                    text
                }

                statusMessage.appendSibling(joinedChannelsMessage)
            }

            sender.addChatMessage(statusMessage)

            val ircState = manager.wrappers[id]?.ircState?.connection?.lifecycle
            if (ircState != null) {
                sender.addChatMessage(TextComponentString("  IRC state: $ircState"))
            }
        }
    }

    override val usage: String
        get() = COMMAND_NAME + " " + COMMAND_USAGE

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return Lists.newArrayList()
    }

    companion object {
        private val COMMAND_NAME = "status"
        private val COMMAND_USAGE = ""
    }
}
