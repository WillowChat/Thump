package engineer.carrot.warren.thump.api

import net.minecraft.command.ICommandSender

interface ICommandHandler {

    val command: String
    val usage: String

    fun processParameters(sender: ICommandSender, parameters: Array<String>)
    fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String>

}
