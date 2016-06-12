package engineer.carrot.warren.thump.command.minecraft

import com.google.common.base.Joiner
import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import engineer.carrot.warren.thump.api.IThumpServicePlugins
import engineer.carrot.warren.thump.command.minecraft.handler.*
import engineer.carrot.warren.thump.helper.PredicateHelper
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import java.util.*

class CommandThump(private val servicePlugins: IThumpServicePlugins) : CommandBase() {

    private val handlers: MutableMap<String, ICommandHandler>

    init {

        this.handlers = Maps.newHashMap<String, ICommandHandler>()
//        this.handlers.put("status", StatusCommandHandler(servicePlugins))
//        this.handlers.put("connect", ConnectCommandHandler(servicePlugins))
//        this.handlers.put("disconnect", DisconnectCommandHandler(servicePlugins))
        this.handlers.put("reload", ReloadCommandHandler(servicePlugins))
//        this.handlers.put("sendraw", SendRawCommandHandler(servicePlugins))
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

        if (parameters.size < 1 || !this.handlers.containsKey(parameters[0])) {
            sender.addChatMessage(TextComponentString("Invalid usage."))
            sender.addChatMessage(TextComponentString(" Usage: " + this.getCommandUsage(sender)))

            return
        }

        this.handlers[parameters[0]]?.processParameters(sender, Arrays.copyOfRange(parameters, 1, parameters.size))
    }

    override fun getTabCompletionOptions(server: MinecraftServer, sender: ICommandSender, parameters: Array<String>, pos: BlockPos?): List<String> {
        if (parameters.size <= 1) {
            val handlerId =  parameters[0]
            return Lists.newArrayList(Iterables.filter(
                    this.handlers.keys,
                    PredicateHelper.StartsWithPredicate(handlerId)))
        }

        val handlerId = parameters[0]
        if (!this.handlers.containsKey(handlerId)) {
            return Lists.newArrayList()
        }

        return this.handlers[handlerId]?.addTabCompletionOptions(sender, Arrays.copyOfRange(parameters, 1, parameters.size)) ?: Lists.newArrayList()
    }

    override fun getRequiredPermissionLevel(): Int {
        return COMMAND_DEFAULT_PERMISSION_LEVEL
    }

    companion object {

        private val COMMAND_NAME = "thump"
        private val COMMAND_USAGE = ""
        private val COMMAND_DEFAULT_PERMISSION_LEVEL = 2
    }
}
