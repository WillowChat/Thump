package engineer.carrot.warren.thump.helper

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText

object PlayerHelper {
    val allPlayers: List<EntityPlayerMP>
        get() = MinecraftServer.getServer().configurationManager.playerEntityList

    fun sendMessageToAllPlayers(message: String) {
        for (player in allPlayers) {
            player.addChatMessage(ChatComponentText(message))
        }
    }
}
