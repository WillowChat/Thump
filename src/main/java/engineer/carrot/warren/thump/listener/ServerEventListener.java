package engineer.carrot.warren.thump.listener;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.util.helper.PlayerHelper;
import engineer.carrot.warren.warren.event.EndOfMOTDEvent;
import engineer.carrot.warren.warren.event.ServerConnectedEvent;
import engineer.carrot.warren.warren.event.ServerDisconnectedEvent;

public class ServerEventListener {
    private String id;

    public ServerEventListener(String id) {
        this.id = id;
    }

    @Subscribe
    public void onConnectedToServer(EndOfMOTDEvent event) {
        PlayerHelper.sendMessageToAllPlayers("IRC network ready: " + this.id);
    }

    @Subscribe
    public void onDisconnectedFromServer(ServerDisconnectedEvent event) {
        PlayerHelper.sendMessageToAllPlayers("IRC network disconnected: " + this.id);
    }
}
