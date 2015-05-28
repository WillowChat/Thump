package engineer.carrot.warren.thump.command.handler;

import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.connection.ConnectionState;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Set;

public class ConnectCommandHandler implements ICommandHandler {
    private ConnectionManager manager;

    private static final String COMMAND_NAME = "connect";
    private static final String COMMAND_USAGE = "<id>";

    public ConnectCommandHandler(ConnectionManager manager) {
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

        if (state == ConnectionState.DISCONNECTING) {
            sender.addChatMessage(new ChatComponentText("Wait for the network to disconnect first."));
            return;
        }

        if (state == ConnectionState.CONNECTING) {
            sender.addChatMessage(new ChatComponentText("That network is already connecting."));
            return;
        }

        if (state == ConnectionState.CONNECTED) {
            sender.addChatMessage(new ChatComponentText("That network is already connected."));
            return;
        }

        sender.addChatMessage(new ChatComponentText("Connecting network with id: " + id));
        this.manager.startConnection(id);
    }

    @Override
    public String getUsage() {
        return COMMAND_NAME + " " + COMMAND_USAGE;
    }
}
