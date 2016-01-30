package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.collect.Lists
import engineer.carrot.warren.thump.connection.ConnectionManager
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText

class StatusCommandHandler(private val manager: ConnectionManager) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        val connections = this.manager.allConnections
        if (connections.isEmpty()) {
            sender.addChatMessage(ChatComponentText("Thump is not configured to connect to any servers."))
            return
        }

        sender.addChatMessage(ChatComponentText("Thump connection statuses:"))

        if (this.manager.allConnections.isEmpty()) {
            sender.addChatMessage(ChatComponentText(" There are no IRC connections available."))

            return
        }

        for (id in this.manager.allConnections) {
            val state = this.manager.getConnectionState(id)

            sender.addChatMessage(ChatComponentText(" " + id + ": " + state.toString()))
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
