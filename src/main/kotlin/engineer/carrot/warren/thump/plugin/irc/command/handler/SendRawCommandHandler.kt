package engineer.carrot.warren.thump.plugin.irc.command.handler

import engineer.carrot.warren.thump.api.ICommandHandler
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import engineer.carrot.warren.thump.plugin.irc.WrapperState
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class SendRawCommandHandler(private val wrappersManager: IWrappersManager) : ICommandHandler {

    override val command = COMMAND_NAME
    override val usage = "$COMMAND_NAME $COMMAND_USAGE"

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        if (parameters.size < 2) {
            sender.sendMessage(TextComponentString("Incorrect usage."))
            sender.sendMessage(TextComponentString(" Usage: " + this.usage))
            return
        }

        val id = parameters[0]
        val wrapper = wrappersManager.wrappers[id]
        val state = wrapper?.state
        if (wrapper == null || state == null) {
            sender.sendMessage(TextComponentString("That network ID doesn't exist."))
            return
        }

        when (state) {
            WrapperState.RUNNING -> {
                val line = parameters.drop(1).joinToString(separator = " ")
                if (wrapper.sendRaw(line)) {
                    sender.sendMessage(TextComponentString("Sent message to $id: $line"))
                } else {
                    sender.sendMessage(TextComponentString("Failed to send to $id: $line"))
                }
            }

            else -> {
                sender.sendMessage(TextComponentString("Network $id isn't running - check with /thump status"))
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
        private val COMMAND_NAME = "sendraw"
        private val COMMAND_USAGE = "<id> <raw IRC line>"
    }

}
