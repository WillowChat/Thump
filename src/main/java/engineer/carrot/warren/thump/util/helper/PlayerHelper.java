package engineer.carrot.warren.thump.util.helper;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class PlayerHelper {
    public static List<EntityPlayerMP> getAllPlayers() {
        return MinecraftServer.getServer().getConfigurationManager().playerEntityList;
    }

    public static void sendMessageToAllPlayers(String message) {
        for (EntityPlayerMP player : getAllPlayers()) {
            player.addChatMessage(new ChatComponentText(message));
        }
    }
}
