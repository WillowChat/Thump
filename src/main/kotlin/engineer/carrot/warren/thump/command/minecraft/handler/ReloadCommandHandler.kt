package engineer.carrot.warren.thump.command.minecraft.handler

import com.google.common.collect.Lists
import engineer.carrot.warren.thump.IThumpServicePlugins
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.helper.LogHelper
import engineer.carrot.warren.thump.minecraft.ChatEventHandler
import engineer.carrot.warren.thump.plugin.irc.IWrappersManager
import net.minecraft.command.ICommandSender
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.MinecraftForge

class ReloadCommandHandler(private val servicePlugins: IThumpServicePlugins) : ICommandHandler {

    override val command: String
        get() = COMMAND_NAME

    override fun processParameters(sender: ICommandSender, parameters: Array<String>) {
        LogHelper.info("Player '{}' triggered a reload (stopping and starting services - the server might lag for a few seconds)...", sender.name)
        sender.addChatMessage(TextComponentString("Reloading Thump (stopping and starting services - the server might lag for a few seconds)..."))

        servicePlugins.stopAll()

        LogHelper.info("Stopped services, reloading configurations...")
        sender.addChatMessage(TextComponentString("Stopped services, reloading configurations..."))

        Thump.configuration.loadAllConfigurations()
        Thump.configuration.saveAllConfigurations()

        LogHelper.info("Reloading services...")
        sender.addChatMessage(TextComponentString("Reloading connection manager..."))

        servicePlugins.startAll()

        LogHelper.info("Reload complete!")
        sender.addChatMessage(TextComponentString("Thump reloaded! Check services with /thump status"))
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
