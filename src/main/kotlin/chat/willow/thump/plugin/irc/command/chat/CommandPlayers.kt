package chat.willow.thump.plugin.irc.command.chat

import chat.willow.thump.Thump
import chat.willow.thump.api.IThumpServiceSink
import chat.willow.thump.helper.PlayerHelper
import chat.willow.thump.helper.StringHelper
import chat.willow.thump.helper.TokenHelper
import com.google.common.base.Joiner

object CommandPlayers {

    fun handlePlayersCommand(sink: IThumpServiceSink) {
        val players = PlayerHelper.allPlayers

        if (players.isEmpty()) {
            sink.sendToAllServices(Thump.configuration.formats.minecraft.playersOnlineNone)

            return
        }

        val names = players.map { StringHelper.obfuscateNameIfNecessary(it.displayName.unformattedText) }

        val message = TokenHelper().addMessageToken(Joiner.on(", ").join(names)).applyTokens(Thump.configuration.formats.minecraft.playersOnline)
        sink.sendToAllServices(message)
    }

}
