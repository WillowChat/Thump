package engineer.carrot.warren.thump.command.minecraft.handler

import engineer.carrot.warren.thump.plugin.IThumpServicePlugins
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class StatusCommandHandler(private val plugins: IThumpServicePlugins) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        for ((name, statusLines) in plugins.statuses()) {
            sender.addChatMessage(TextComponentString("Status for $name:"))
            statusLines.forEach { sender.addChatMessage(TextComponentString(it)) }
        }
    }

    override val usage: String
        get() = COMMAND_NAME + " " + COMMAND_USAGE

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return listOf()
    }

    companion object {
        private val COMMAND_NAME = "status"
        private val COMMAND_USAGE = ""
    }
}
