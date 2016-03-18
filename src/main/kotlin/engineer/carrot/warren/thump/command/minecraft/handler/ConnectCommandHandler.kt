package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.connection.ConnectionState
import engineer.carrot.warren.thump.helper.PredicateHelper
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class ConnectCommandHandler(private val manager: ConnectionManager) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        if (parameters.size < 1) {
            sender.addChatMessage(TextComponentString("Incorrect usage."))
            sender.addChatMessage(TextComponentString(" Usage: " + this.usage))
            return
        }

        val id = parameters[0]
        val state = this.manager.getConnectionState(id)
        if (state == null) {
            sender.addChatMessage(TextComponentString("That network ID doesn't exist."))
            return
        }

        if (state === ConnectionState.DISCONNECTING) {
            sender.addChatMessage(TextComponentString("Wait for the network to disconnect first."))
            return
        }

        if (state === ConnectionState.CONNECTING) {
            sender.addChatMessage(TextComponentString("That network is already connecting."))
            return
        }

        if (state === ConnectionState.CONNECTED) {
            sender.addChatMessage(TextComponentString("That network is already connected."))
            return
        }

        sender.addChatMessage(TextComponentString("Connecting network with id: " + id))
        this.manager.startConnection(id)
    }

    override val usage: String
        get() = COMMAND_NAME + " " + COMMAND_USAGE

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        if (parameters.size <= 1) {
            val handlerId = parameters[0]
            return Lists.newArrayList(Iterables.filter(
                    this.manager.allConnections,
                    PredicateHelper.StartsWithPredicate(handlerId)))
        }

        return Lists.newArrayList()
    }

    companion object {

        private val COMMAND_NAME = "connect"
        private val COMMAND_USAGE = "<id>"
    }
}
