package engineer.carrot.warren.thump.helper

import com.google.common.collect.Maps

class TokenHelper {

    private val tokens: MutableMap<String, String>

    init {
        this.tokens = Maps.newHashMap<String, String>()
    }

    fun applyTokens(string: String): String {
        var stringCopy = string
        for (token in this.tokens.keys) {
            stringCopy = stringCopy.replace(token, this.tokens[token] ?: "")
        }

        return stringCopy
    }

    fun addUserToken(user: String): TokenHelper {
        this.tokens.put(USER_TOKEN, user)
        return this
    }

    fun addChannelToken(channel: String): TokenHelper {
        this.tokens.put(CHANNEL_TOKEN, channel)
        return this
    }

    fun addMessageToken(message: String): TokenHelper {
        this.tokens.put(MESSAGE_TOKEN, message)
        return this
    }

    companion object {
        val USER_TOKEN = "{u}"
        val CHANNEL_TOKEN = "{c}"
        val MESSAGE_TOKEN = "{m}"
    }
}
