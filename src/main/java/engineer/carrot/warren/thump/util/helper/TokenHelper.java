package engineer.carrot.warren.thump.util.helper;

import com.google.common.collect.Maps;

import java.util.Map;

public class TokenHelper {
    public static final String USER_TOKEN = "{u}";
    public static final String CHANNEL_TOKEN = "{c}";
    public static final String MESSAGE_TOKEN = "{m}";

    private final Map<String, String> tokens;

    public TokenHelper() {
        this.tokens = Maps.newHashMap();
    }

    public String applyTokens(String string) {
        for (String token : this.tokens.keySet()) {
            string = string.replace(token, this.tokens.get(token));
        }

        return string;
    }

    public TokenHelper addUserToken(String user) {
        this.tokens.put(USER_TOKEN, user);
        return this;
    }

    public TokenHelper addChannelToken(String channel) {
        this.tokens.put(CHANNEL_TOKEN, channel);
        return this;
    }

    public TokenHelper addMessageToken(String message) {
        this.tokens.put(MESSAGE_TOKEN, message);
        return this;
    }
}
