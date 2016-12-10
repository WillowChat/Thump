package engineer.carrot.warren.thump.helper

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.ITextComponent
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.fml.common.FMLCommonHandler

object PlayerHelper {
    val allPlayers: List<EntityPlayerMP>
        get() = FMLCommonHandler.instance().minecraftServerInstance.playerList.players

    fun sendMessageToAllPlayers(message: ITextComponent) {
        for (player in allPlayers) {
            // TODO: Add link finding back in
            player.sendMessage(message)
        }
    }

    @Suppress("UNUSED") fun sendMessageToPlayer(player: String, message: ITextComponent) {
        // TODO: Add link finding back in
        allPlayers.find { it.name == player }?.sendMessage(message)
    }
}
