package engineer.carrot.warren.thump.command.minecraft.handler;

import net.minecraft.command.ICommandSender;

import java.util.List;

public interface ICommandHandler {
    String getCommand();

    void processParameters(ICommandSender sender, String[] parameters);

    String getUsage();

    List<String> addTabCompletionOptions(ICommandSender sender, String[] parameters);
}
