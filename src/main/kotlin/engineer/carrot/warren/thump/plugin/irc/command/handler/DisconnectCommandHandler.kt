package engineer.carrot.warren.thump.plugin.irc.command.handler

import engineer.carrot.warren.thump.api.ICommandHandler
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import engineer.carrot.warren.thump.plugin.irc.WrapperState
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class DisconnectCommandHandler(private val wrappersManager: IWrappersManager) : ICommandHandler {

    override val command = COMMAND_NAME
    override val usage = "$COMMAND_NAME $COMMAND_USAGE"

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        if (parameters.isEmpty()) {
            sender.addChatMessage(TextComponentString("Incorrect usage."))
            sender.addChatMessage(TextComponentString(" Usage: " + this.usage))
            return
        }

        val id = parameters[0]
        val state = wrappersManager.wrappers[id]?.state
        if (state == null) {
            sender.addChatMessage(TextComponentString("That network ID doesn't exist."))
            return
        }

        when (state) {
            WrapperState.READY -> sender.addChatMessage(TextComponentString("That ID isn't running yet"))
            else -> {
                sender.addChatMessage(TextComponentString("Disconnecting network with id: " + id))
                wrappersManager.stop(id, shouldReconnect = false)
            }
        }
    }

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return when (parameters.size) {
            0 -> wrappersManager.wrappers.keys.toList()
            1 -> wrappersManager.wrappers.keys.filter { it.startsWith(parameters[0]) }
            else -> listOf()
        }
    }

    companion object {
        private val COMMAND_NAME = "disconnect"
        private val COMMAND_USAGE = "<id>"
    }
}
