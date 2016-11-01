package engineer.carrot.warren.thump.plugin.irc.command.handler

import engineer.carrot.warren.thump.api.ICommandHandler
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class IrcServiceCommandHandler(wrappersManager: IWrappersManager) : ICommandHandler {

    private val handlers = mutableMapOf<String, ICommandHandler>()

    override val command = "irc"
    override val usage: String
        get() = "$command ${handlers.keys.joinToString(separator = ", ")}"

    init {
        handlers.put("connect", ConnectCommandHandler(wrappersManager))
        handlers.put("disconnect", DisconnectCommandHandler(wrappersManager))
        handlers.put("sendraw", SendRawCommandHandler(wrappersManager))
    }

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        if (parameters.size < 1 || !this.handlers.containsKey(parameters[0])) {
            sender.addChatMessage(TextComponentString("Invalid usage."))
            sender.addChatMessage(TextComponentString(" Usage: $usage"))

            return
        }

        this.handlers[parameters[0]]?.processParameters(sender, parameters.drop(1).toTypedArray())
    }

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return when (parameters.size) {
            0 -> handlers.keys.toList()
            1 -> handlers.keys.filter { it.startsWith(parameters[0]) }
            else -> handlers[parameters[0]]?.addTabCompletionOptions(sender, parameters.drop(1).toTypedArray()) ?: listOf()
        }
    }

}
