package engineer.carrot.warren.thump.plugin.irc.command

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.api.IThumpServiceSink
import engineer.carrot.warren.thump.helper.PlayerHelper
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper

object CommandPlayers {

    fun handlePlayersCommand(sink: IThumpServiceSink) {
        val players = PlayerHelper.allPlayers

        if (players.isEmpty()) {
            sink.sendToAllServices(Thump.configuration.formats.minecraft.playersOnlineNone)

            return
        }

        val names = Lists.newArrayList<String>()
        for (player in players) {
            names.add(StringHelper.obfuscateNameIfNecessary(player.displayNameString))
        }

        val message = TokenHelper().addMessageToken(Joiner.on(", ").join(names)).applyTokens(Thump.configuration.formats.minecraft.playersOnline)
        sink.sendToAllServices(message)
    }

}
