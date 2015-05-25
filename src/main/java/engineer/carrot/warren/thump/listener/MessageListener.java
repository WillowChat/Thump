package engineer.carrot.warren.thump.listener;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.warren.event.ChannelMessageEvent;
import engineer.carrot.warren.warren.event.PrivateMessageEvent;

public class MessageListener {

    @Subscribe
    public void handleChannelMessage(ChannelMessageEvent event) {
        LogHelper.info("{}: <{}> {}", event.channel, event.fromUser.getNameWithoutAccess(), event.contents);
    }

    @Subscribe
    public void handlePrivateMessage(PrivateMessageEvent event) {
        LogHelper.info("PM from {}: {}", event.fromUser, event.contents);
    }
}
