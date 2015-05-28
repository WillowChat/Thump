package engineer.carrot.warren.thump.command.handler;

import net.minecraft.command.ICommandSender;

public interface ICommandHandler {
    String getCommand();

    void processParameters(ICommandSender sender, String[] parameters);

    String getUsage();
}
