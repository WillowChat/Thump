package engineer.carrot.warren.thump.handler.minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

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
        String commandName = event.command.getCommandName();
        if (commandName.equalsIgnoreCase("me")) {
            String message = " * " + event.sender.getCommandSenderName() + " " + Strings.join(event.parameters, " ");
            this.connectionManager.sendMessageToAllChannels(message);

            return;
        }

        if (commandName.equalsIgnoreCase("say")) {
            String message = "<" + event.sender.getCommandSenderName() + "> " + Strings.join(event.parameters, " ");
            this.connectionManager.sendMessageToAllChannels(message);
        }
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

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        IChatComponent deathMessage = event.source.func_151519_b(player);
        if (deathMessage == null) {
            return;
        }

        this.connectionManager.sendMessageToAllChannels(deathMessage.getUnformattedText());
    }
}
