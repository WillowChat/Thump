package engineer.carrot.warren.thump.handler.minecraft;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import engineer.carrot.warren.thump.Thump;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.TokenHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;

import java.util.Map;

public class ChatEventHandler {
    private ConnectionManager connectionManager;

    public ChatEventHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event) {
        if (!Thump.configuration.getEvents().minecraft.playerMessage) {
            return;
        }

        String message = new TokenHelper()
                .addUserToken(event.username)
                .addMessageToken(event.message)
                .applyTokens(Thump.configuration.getFormats().minecraft.playerMessage);
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onCommandEvent(CommandEvent event) {
        String commandName = event.command.getCommandName();
        boolean isServer = event.sender.getCommandSenderName().equals("Server");

        if (commandName.equalsIgnoreCase("me")) {
            if (isServer && !Thump.configuration.getEvents().minecraft.serverAction) {
                return;
            }

            if (!isServer && !Thump.configuration.getEvents().minecraft.playerAction) {
                return;
            }

            String message = new TokenHelper()
                    .addUserToken(event.sender.getCommandSenderName())
                    .addMessageToken(Strings.join(event.parameters, " "))
                    .applyTokens(Thump.configuration.getFormats().minecraft.playerAction);
            this.connectionManager.sendMessageToAllChannels(message);

            return;
        }

        if (commandName.equalsIgnoreCase("say")) {
            if (isServer && !Thump.configuration.getEvents().minecraft.serverMessage) {
                return;
            }

            if (!isServer && !Thump.configuration.getEvents().minecraft.playerMessage) {
                return;
            }

            String message = new TokenHelper()
                    .addUserToken(event.sender.getCommandSenderName())
                    .addMessageToken(Strings.join(event.parameters, " "))
                    .applyTokens(Thump.configuration.getFormats().minecraft.playerMessage);
            this.connectionManager.sendMessageToAllChannels(message);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!Thump.configuration.getEvents().minecraft.playerJoined) {
            return;
        }

        String message = new TokenHelper()
                .addUserToken(event.player.getDisplayName())
                .applyTokens(Thump.configuration.getFormats().minecraft.playerJoined);
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!Thump.configuration.getEvents().minecraft.playerLeft) {
            return;
        }

        String message = new TokenHelper()
                .addUserToken(event.player.getDisplayName())
                .applyTokens(Thump.configuration.getFormats().minecraft.playerLeft);
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (!Thump.configuration.getEvents().minecraft.playerDeath) {
            return;
        }

        if (!(event.entityLiving instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) event.entityLiving;
        IChatComponent deathMessage = event.source.func_151519_b(player);
        if (deathMessage == null) {
            return;
        }

        String message = new TokenHelper()
                .addMessageToken(deathMessage.getUnformattedText())
                .applyTokens(Thump.configuration.getFormats().minecraft.playerDeath);
        this.connectionManager.sendMessageToAllChannels(message);
    }

    @SubscribeEvent
    public void onAchievementEvent(AchievementEvent event) {
        if (!Thump.configuration.getEvents().minecraft.playerAchievement) {
            return;
        }

        if (!(event.entityPlayer instanceof EntityPlayerMP)) {
            return;
        }

        boolean hasAchievementUnlocked = ((EntityPlayerMP) event.entityPlayer).func_147099_x().hasAchievementUnlocked(event.achievement);
        if (hasAchievementUnlocked) {
            return;
        }

        IChatComponent achievementMessage = new ChatComponentTranslation("chat.type.achievement", new Object[]{event.entityPlayer.getDisplayName(), event.achievement.func_150955_j()});

        String message = new TokenHelper()
                .addMessageToken(achievementMessage.getUnformattedText())
                .applyTokens(Thump.configuration.getFormats().minecraft.playerAchievement);
        this.connectionManager.sendMessageToAllChannels(message);
    }
}
