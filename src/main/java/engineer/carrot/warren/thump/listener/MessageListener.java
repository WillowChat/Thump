package engineer.carrot.warren.thump.listener;

import com.google.common.eventbus.Subscribe;
import engineer.carrot.warren.thump.Thump;
import engineer.carrot.warren.thump.command.irc.CommandPlayers;
import engineer.carrot.warren.thump.connection.ConnectionManager;
import engineer.carrot.warren.thump.util.helper.LogHelper;
import engineer.carrot.warren.thump.util.helper.PlayerHelper;
import engineer.carrot.warren.thump.util.helper.TokenHelper;
import engineer.carrot.warren.warren.event.ChannelActionEvent;
import engineer.carrot.warren.warren.event.ChannelMessageEvent;
import engineer.carrot.warren.warren.event.PrivateActionEvent;
import engineer.carrot.warren.warren.event.PrivateMessageEvent;

public class MessageListener {
    private ConnectionManager manager;

    public MessageListener(ConnectionManager manager) {
        this.manager = manager;
    }

    @Subscribe
    public void handleChannelMessage(ChannelMessageEvent event) {
        String user = event.fromUser.getNameWithoutAccess();

        if (manager.usernameMatchesAnyConnection(user)) {
            return;
        }

        String output = new TokenHelper()
                .addUserToken(user)
                .addChannelToken(event.channel.toString())
                .addMessageToken(event.contents)
                .applyTokens(Thump.configuration.getFormats().irc.channelMessage);

        if (Thump.configuration.getGeneral().logIrcToServerConsole) {
            LogHelper.info(output);
        }

        // TODO: Merge in to command system when available
        if (event.contents.equals("!players")) {
            if (Thump.configuration.getCommands().players) {
                CommandPlayers.handlePlayersCommand(manager);
            }
        }

        if (!Thump.configuration.getEvents().irc.channelMessage) {
            return;
        }

        PlayerHelper.sendMessageToAllPlayers(output);
    }

    @Subscribe
    public void handleChannelAction(ChannelActionEvent event) {
        String user = event.fromUser.getNameWithoutAccess();

        if (manager.usernameMatchesAnyConnection(user)) {
            return;
        }

        String output = new TokenHelper()
                .addUserToken(user)
                .addChannelToken(event.channel.toString())
                .addMessageToken(event.contents)
                .applyTokens(Thump.configuration.getFormats().irc.channelAction);

        if (Thump.configuration.getGeneral().logIrcToServerConsole) {
            LogHelper.info(output);
        }

        if (!Thump.configuration.getEvents().irc.channelAction) {
            return;
        }

        PlayerHelper.sendMessageToAllPlayers(output);
    }

    @Subscribe
    public void handlePrivateMessage(PrivateMessageEvent event) {
        String output = "PM from " + event.fromUser + ": " + event.contents;

        if (!Thump.configuration.getGeneral().logIrcToServerConsole) {
            return;
        }

        LogHelper.info(output);
    }

    @Subscribe
    public void handlePrivateAction(PrivateActionEvent event) {
        String output = "PM ACTION from " + event.fromUser + ": " + event.contents;

        if (!Thump.configuration.getGeneral().logIrcToServerConsole) {
            return;
        }

        LogHelper.info(output);
    }
}
