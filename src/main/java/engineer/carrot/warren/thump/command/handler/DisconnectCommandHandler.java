package engineer.carrot.warren.thump.command.handler;

import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.connection.ConnectionState;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class DisconnectCommandHandler implements ICommandHandler {
    private ConnectionManager manager;

    private static final String COMMAND_NAME = "disconnect";
    private static final String COMMAND_USAGE = "<id>";

    public DisconnectCommandHandler(ConnectionManager manager) {
        this.manager = manager;
    }

    @Override
    public String getCommand() {
        return COMMAND_NAME;
    }

    @Override
    public void processParameters(ICommandSender sender, String[] parameters) {
        if (parameters.length < 1) {
            sender.addChatMessage(new ChatComponentText("Incorrect usage."));
            sender.addChatMessage(new ChatComponentText(" Usage: " + this.getUsage()));
            return;
        }

        String id = parameters[0];
        ConnectionState state = this.manager.getConnectionState(id);
        if (state == null) {
            sender.addChatMessage(new ChatComponentText("That network ID doesn't exist."));
            return;
        }

        if (state == ConnectionState.CONNECTING) {
            sender.addChatMessage(new ChatComponentText("Wait for the network to connect first."));
            return;
        }

        if (state == ConnectionState.DISCONNECTING) {
            sender.addChatMessage(new ChatComponentText("That network is already disconnecting."));
            return;
        }

        if (state == ConnectionState.DISCONNECTED) {
            sender.addChatMessage(new ChatComponentText("That network is already disconnected."));
            return;
        }

        sender.addChatMessage(new ChatComponentText("Disconnecting network with id: " + id));
        this.manager.stopConnection(id);
    }

    @Override
    public String getUsage() {
        return COMMAND_NAME + " " + COMMAND_USAGE;
    }
}
