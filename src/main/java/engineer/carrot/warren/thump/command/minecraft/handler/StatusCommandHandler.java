package engineer.carrot.warren.thump.command.minecraft.handler;

import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.connection.ConnectionState;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.Set;

public class StatusCommandHandler implements ICommandHandler {
    private ConnectionManager manager;

    private static final String COMMAND_NAME = "status";
    private static final String COMMAND_USAGE = "";

    public StatusCommandHandler(ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    @Override
    public void processParameters(ICommandSender sender, String[] parameters) {
        Set<String> connections = this.manager.getAllConnections();
        if (connections.isEmpty()) {
            sender.addChatMessage(new ChatComponentText("Thump is not configured to connect to any servers."));
            return;
        }

        sender.addChatMessage(new ChatComponentText("Thump connection statuses:"));

        if (this.manager.getAllConnections().isEmpty()) {
            sender.addChatMessage(new ChatComponentText(" There are no IRC connections available."));

            return;
        }

        for (String id : this.manager.getAllConnections()) {
            ConnectionState state = this.manager.getConnectionState(id);

            sender.addChatMessage(new ChatComponentText(" " + id + ": " + state.toString()));
        }
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
