package engineer.carrot.warren.thump.helper

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler

object PlayerHelper {
    val allPlayers: List<EntityPlayerMP>
        get() = FMLCommonHandler.instance().getMinecraftServerInstance().playerList.playerList

    fun sendMessageToAllPlayers(message: String) {
        for (player in allPlayers) {
            player.addChatMessage(TextComponentString(message))
        }
    }
}
