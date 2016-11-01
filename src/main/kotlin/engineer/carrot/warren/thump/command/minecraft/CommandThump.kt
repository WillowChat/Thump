package engineer.carrot.warren.thump.command.minecraft

import com.google.common.base.Joiner
import engineer.carrot.warren.thump.api.ICommandHandler
import engineer.carrot.warren.thump.command.minecraft.handler.ReloadCommandHandler
import engineer.carrot.warren.thump.command.minecraft.handler.StatusCommandHandler
import engineer.carrot.warren.thump.plugin.IThumpServicePlugins
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString

class CommandThump(private val servicePlugins: IThumpServicePlugins) : CommandBase() {

    private val handlers = mutableMapOf<String, ICommandHandler>()
    private val serviceHandlers = mutableMapOf<String, ICommandHandler>()

    fun reconfigureHandlers(pluginCommandHandlers: Map<String, ICommandHandler>) {
        handlers.clear()
        serviceHandlers.clear()

        handlers.put("reload", ReloadCommandHandler(servicePlugins))
        handlers.put("status", StatusCommandHandler(servicePlugins))

        serviceHandlers.putAll(pluginCommandHandlers.filterNot { handlers.containsKey(it.key) })

        handlers.put("service", object : ICommandHandler {
            override val command = "service"
            override val usage = "$command ${serviceHandlers.keys.joinToString(separator = ", ")}"

            override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
                if (parameters.isEmpty() || !serviceHandlers.containsKey(parameters[0])) {
                    sender.addChatMessage(TextComponentString("Invalid usage."))
                    sender.addChatMessage(TextComponentString(" Usage: $usage"))
                } else {
                    serviceHandlers[parameters[0]]?.processParameters(sender, parameters.drop(1).toTypedArray())
                }
            }

            override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
                return when (parameters.size) {
                    0 -> serviceHandlers.keys.toList()
                    1 -> serviceHandlers.keys.filter { it.startsWith(parameters[0]) }
                    else -> serviceHandlers[parameters[0]]?.addTabCompletionOptions(sender, parameters.drop(1).toTypedArray()) ?: listOf()
                }
            }
        })
    }

    // CommandBase

    override fun getCommandName(): String {
        return COMMAND_NAME
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/" + this.commandName + " " + Joiner.on(", ").join(this.handlers.keys)
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, parameters: Array<out String>?) {
        if (sender == null || parameters == null) {
            return
        }

        if (parameters.isEmpty() || !this.handlers.containsKey(parameters[0])) {
            sender.addChatMessage(TextComponentString("Invalid usage."))
            sender.addChatMessage(TextComponentString(" Usage: " + this.getCommandUsage(sender)))

            return
        }

        this.handlers[parameters[0]]?.processParameters(sender, parameters.drop(1).toTypedArray())
    }

    override fun getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender, parameters: Array<String>, pos: BlockPos?): List<String> {
        return when (parameters.size) {
            0 -> handlers.keys.toList()
            1 -> handlers.keys.filter { it.startsWith(parameters[0]) }
            else -> handlers[parameters[0]]?.addTabCompletionOptions(sender, parameters.drop(1).toTypedArray()) ?: listOf()
        }
    }

    override fun getRequiredPermissionLevel(): Int {
        return COMMAND_DEFAULT_PERMISSION_LEVEL
    }

    companion object {

        private val COMMAND_NAME = "thump"
        private val COMMAND_DEFAULT_PERMISSION_LEVEL = 2
    }
}
