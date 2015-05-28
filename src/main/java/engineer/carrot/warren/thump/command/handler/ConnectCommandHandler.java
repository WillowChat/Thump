package engineer.carrot.warren.thump.command.handler;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.connection.ConnectionState;
import engineer.carrot.warren.thump.util.helper.PredicateHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

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

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
        if (parameters.length <= 1) {
            String handlerId = (parameters[0] == null) ? "" : parameters[0];
            return Lists.newArrayList(Iterables.filter(
                    this.manager.getAllConnections(),
                    new PredicateHelper.StartsWithPredicate(handlerId)
            ));
        }

        return null;
    }
}
