package engineer.carrot.warren.thump.handler.minecraft;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import engineer.carrot.warren.thump.ConnectionManager;
import net.minecraftforge.event.ServerChatEvent;

public class ChatEventHandler {
    private ConnectionManager connectionManager;

    public ChatEventHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @SubscribeEvent
    public void onServerChatEvent(ServerChatEvent event) {
        this.connectionManager.sendMessageToAllChannels("<" + event.username + "> " + event.message);
    }
}
