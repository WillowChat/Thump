package engineer.carrot.warren.thump.command.irc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import engineer.carrot.warren.thump.Thump;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.PlayerHelper;
import engineer.carrot.warren.thump.util.helper.TokenHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.Map;

public class CommandPlayers {
    public static void handlePlayersCommand(ConnectionManager manager) {
        List<EntityPlayerMP> players = PlayerHelper.getAllPlayers();

        if (players.isEmpty()) {
            manager.sendMessageToAllChannels(Thump.configuration.getFormats().minecraft.playersOnlineNone);

            return;
        }

        List<String> names = Lists.newArrayList();
        for (EntityPlayerMP player : players) {
            names.add(player.getDisplayName());
        }

        String message = new TokenHelper()
                .addMessageToken(Strings.join(names, ", "))
                .applyTokens(Thump.configuration.getFormats().minecraft.playersOnline);
        manager.sendMessageToAllChannels(message);
    }
}
