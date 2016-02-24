package engineer.carrot.warren.thump.helper

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ChatComponentText

object PlayerHelper {
    val allPlayers: List<Any?>
        get() = MinecraftServer.getServer().configurationManager.playerEntityList

    fun sendMessageToAllPlayers(message: String) {
        for (playerEntity in allPlayers) {
            val player = playerEntity as? EntityPlayerMP ?: continue
            player.addChatMessage(ChatComponentText(message))
        }
    }
}
