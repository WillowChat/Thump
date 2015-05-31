package engineer.carrot.warren.thump.command.irc;

import com.google.common.collect.Lists;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.PlayerHelper;
import joptsimple.internal.Strings;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

public class CommandPlayers {
    public static void handlePlayersCommand(ConnectionManager manager) {
        List<EntityPlayerMP> players = PlayerHelper.getAllPlayers();

        if (players.isEmpty()) {
            manager.sendMessageToAllChannels("There are no players online.");

            return;
        }

        List<String> names = Lists.newArrayList();
        for (EntityPlayerMP player : players) {
            names.add(player.getDisplayName());
        }

        manager.sendMessageToAllChannels("Players online: " + Strings.join(names, ", "));
    }
}
