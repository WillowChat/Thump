package engineer.carrot.warren.thump.listener;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.Thump;
import engineer.carrot.warren.thump.util.helper.PlayerHelper;
import engineer.carrot.warren.thump.util.helper.TokenHelper;
import engineer.carrot.warren.warren.event.EndOfMOTDEvent;
import engineer.carrot.warren.warren.event.ServerDisconnectedEvent;

public class ServerEventListener {
    private String id;

    public ServerEventListener(String id) {
        this.id = id;
    }

    @Subscribe
    public void onConnectedToServer(EndOfMOTDEvent event) {
        String output = new TokenHelper()
                .addMessageToken(this.id)
                .applyTokens(Thump.configuration.getFormats().irc.networkReady);
        PlayerHelper.sendMessageToAllPlayers(output);
    }

    @Subscribe
    public void onDisconnectedFromServer(ServerDisconnectedEvent event) {
        String output = new TokenHelper()
                .addMessageToken(this.id)
                .applyTokens(Thump.configuration.getFormats().irc.networkDisconnected);
        PlayerHelper.sendMessageToAllPlayers(output);
    }
}
