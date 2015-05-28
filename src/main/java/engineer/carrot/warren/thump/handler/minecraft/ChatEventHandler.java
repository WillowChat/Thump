package engineer.carrot.warren.thump.handler.minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import joptsimple.internal.Strings;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;

public class ChatEventHandler {
    private ConnectionManager connectionManager;

    public ChatEventHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event) {
        this.connectionManager.sendMessageToAllChannels("<" + event.username + "> " + event.message);
    }

    @SubscribeEvent
    public void onCommandEvent(CommandEvent event) {
        if (!event.command.getCommandName().equalsIgnoreCase("me")) {
            return;
        }

        String message = " * " + event.sender.getCommandSenderName() + " " + Strings.join(event.parameters, " ");
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        String message = " * " + event.player.getDisplayName() + " has joined the game";
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        String message = " * " + event.player.getDisplayName() + " has left the game";
        this.connectionManager.sendMessageToAllChannels(message);
    }
}
