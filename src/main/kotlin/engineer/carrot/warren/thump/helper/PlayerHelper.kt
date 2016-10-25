package engineer.carrot.warren.thump.helper

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.fml.common.FMLCommonHandler

object PlayerHelper {
    val allPlayers: List<EntityPlayerMP>
        get() = FMLCommonHandler.instance().minecraftServerInstance.playerList.playerList

    fun sendMessageToAllPlayers(message: String) {
        for (player in allPlayers) {
            player.addChatMessage(ForgeHooks.newChatWithLinks(message))
        }
    }

    fun sendMessageToPlayer(player: String, message: String) {
        allPlayers.find { it.name == player }?.addChatMessage(ForgeHooks.newChatWithLinks(message))
    }
}
