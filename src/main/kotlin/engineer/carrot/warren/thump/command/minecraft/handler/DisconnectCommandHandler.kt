package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import engineer.carrot.warren.thump.helper.PredicateHelper
import engineer.carrot.warren.thump.runner.IWrappersManager
import engineer.carrot.warren.thump.runner.WrapperState
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString

class DisconnectCommandHandler(private val manager: IWrappersManager) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        if (parameters.size < 1) {
            sender.addChatMessage(TextComponentString("Incorrect usage."))
            sender.addChatMessage(TextComponentString(" Usage: " + this.usage))
            return
        }

        val id = parameters[0]
        val state = manager.wrappers[id]?.state
        if (state == null) {
            sender.addChatMessage(TextComponentString("That network ID doesn't exist."))
            return
        }

        when (state) {
            WrapperState.READY -> sender.addChatMessage(TextComponentString("That ID isn't running yet"))
            else -> {
                sender.addChatMessage(TextComponentString("Disconnecting network with id: " + id))
                manager.stop(id)
            }
        }
    }

    override val usage: String
        get() = COMMAND_NAME + " " + COMMAND_USAGE

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        if (parameters.size <= 1) {
            val handlerId = parameters[0]
            return Lists.newArrayList(Iterables.filter(
                    manager.wrappers.keys,
                    PredicateHelper.StartsWithPredicate(handlerId)))
        }

        return Lists.newArrayList()
    }

    companion object {

        private val COMMAND_NAME = "disconnect"
        private val COMMAND_USAGE = "<id>"
    }
}
