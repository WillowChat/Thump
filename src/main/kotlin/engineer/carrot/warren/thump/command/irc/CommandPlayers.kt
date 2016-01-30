package engineer.carrot.warren.thump.command.irc

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper

object CommandPlayers {
    fun handlePlayersCommand(manager: ConnectionManager) {
        val players = PlayerHelper.allPlayers

        if (players.isEmpty()) {
            manager.sendMessageToAllChannels(Thump.configuration.formats.minecraft.playersOnlineNone)

            return
        }

        val names = Lists.newArrayList<String>()
        for (player in players) {
            names.add(StringHelper.obfuscateNameIfNecessary(player.displayNameString))
        }

        val message = TokenHelper().addMessageToken(Joiner.on(", ").join(names)).applyTokens(Thump.configuration.formats.minecraft.playersOnline)
        manager.sendMessageToAllChannels(message)
    }
}
