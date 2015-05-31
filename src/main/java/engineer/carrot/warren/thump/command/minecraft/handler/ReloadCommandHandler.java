package engineer.carrot.warren.thump.command.minecraft.handler;

import engineer.carrot.warren.thump.Thump;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class ReloadCommandHandler implements ICommandHandler {
    private ConnectionManager manager;

    private static final String COMMAND_NAME = "reload";
    private static final String COMMAND_USAGE = "";

    public ReloadCommandHandler(ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    @Override
    public void processParameters(ICommandSender sender, String[] parameters) {
        LogHelper.info("Player '{}' triggered a reload (disconnecting and reconnecting networks - the server might lag for a few seconds)...", sender.getCommandSenderName());
        sender.addChatMessage(new ChatComponentText("Reloading Thump (disconnecting and reconnecting networks - the server might lag for a few seconds)..."));

        this.manager.stopAllConnections();
        this.manager.removeAllConnections();

        LogHelper.info("Stopped and removed connections, reloading configurations...");
        sender.addChatMessage(new ChatComponentText("Stopped and removed connections, reloading configurations..."));

        Thump.configuration.loadAllConfigurations();
        Thump.configuration.saveAllConfigurations();

        LogHelper.info("Repopulating connection manager...");
        sender.addChatMessage(new ChatComponentText("Reloading connection manager..."));

        Thump.instance.populateConnectionManager();
        Thump.instance.startAllConnections();

        LogHelper.info("Reload complete!");
        sender.addChatMessage(new ChatComponentText("Thump reloaded! Check networks with /thump status"));
    }

    @Override
    public String getUsage() {
        return COMMAND_NAME + " " + COMMAND_USAGE;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
        return null;
    }
}
