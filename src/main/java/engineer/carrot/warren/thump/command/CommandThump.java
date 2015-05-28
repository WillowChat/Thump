package engineer.carrot.warren.thump.command;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import engineer.carrot.warren.thump.command.handler.ConnectCommandHandler;
import engineer.carrot.warren.thump.command.handler.DisconnectCommandHandler;
import engineer.carrot.warren.thump.command.handler.ICommandHandler;
import engineer.carrot.warren.thump.command.handler.StatusCommandHandler;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.PredicateHelper;
import joptsimple.internal.Strings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandThump extends CommandBase {
    private ConnectionManager manager;
    private Map<String, ICommandHandler> handlers;

    private static final String COMMAND_NAME = "thump";
    private static final String COMMAND_USAGE = "";

    public CommandThump(ConnectionManager manager) {
        this.manager = manager;

        this.handlers = Maps.newHashMap();
        this.handlers.put("status", new StatusCommandHandler(manager));
        this.handlers.put("connect", new ConnectCommandHandler(manager));
        this.handlers.put("disconnect", new DisconnectCommandHandler(manager));
    }

    // CommandBase

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " " + Strings.join(Lists.newArrayList(this.handlers.keySet()), ", ");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] parameters) {
        if (parameters.length < 1 || !this.handlers.containsKey(parameters[0])) {
            sender.addChatMessage(new ChatComponentText("Invalid usage."));
            sender.addChatMessage(new ChatComponentText(" Usage: " + this.getCommandUsage(sender)));

            return;
        }

        this.handlers.get(parameters[0]).processParameters(sender, Arrays.copyOfRange(parameters, 1, parameters.length));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters) {
        if (parameters.length <= 1) {
            String handlerId = (parameters[0] == null) ? "" : parameters[0];
            return Lists.newArrayList(Iterables.filter(
                    this.handlers.keySet(),
                    new PredicateHelper.StartsWithPredicate(handlerId)
            ));
        }

        String handlerId = parameters[0];
        if (!this.handlers.containsKey(handlerId)) {
            return null;
        }

        return this.handlers.get(handlerId).addTabCompletionOptions(sender, Arrays.copyOfRange(parameters, 1, parameters.length));
    }
}
