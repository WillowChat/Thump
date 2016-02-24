package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.collect.Lists
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.helper.LogHelper
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText

class ReloadCommandHandler(private val manager: ConnectionManager) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        LogHelper.info("Player '{}' triggered a reload (disconnecting and reconnecting networks - the server might lag for a few seconds)...", sender.commandSenderName)
        sender.addChatMessage(ChatComponentText("Reloading Thump (disconnecting and reconnecting networks - the server might lag for a few seconds)..."))

        this.manager.stopAllConnections()
        this.manager.removeAllConnections()

        LogHelper.info("Stopped and removed connections, reloading configurations...")
        sender.addChatMessage(ChatComponentText("Stopped and removed connections, reloading configurations..."))

        Thump.configuration.loadAllConfigurations()
        Thump.configuration.saveAllConfigurations()

        LogHelper.info("Repopulating connection manager...")
        sender.addChatMessage(ChatComponentText("Reloading connection manager..."))

        Thump.instance.populateConnectionManager()
        Thump.instance.startAllConnections()

        LogHelper.info("Reload complete!")
        sender.addChatMessage(ChatComponentText("Thump reloaded! Check networks with /thump status"))
    }

    override val usage: String
        get() = COMMAND_NAME + " " + COMMAND_USAGE

    override fun addTabCompletionOptions(sender: ICommandSender, parameters: Array<String>): List<String> {
        return Lists.newArrayList()
    }

    companion object {

        private val COMMAND_NAME = "reload"
        private val COMMAND_USAGE = ""
    }
}
