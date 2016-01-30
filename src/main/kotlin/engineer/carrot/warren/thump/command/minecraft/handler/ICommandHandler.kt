package engineer.carrot.warren.thump.command.minecraft.handler

import net.minecraft.command.ICommandSender

interface ICommandHandler {
    val command: String

    fun processParameters(sender: ICommandSender, parameters: Array<String>)

    val usage: String

    fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String>
}
