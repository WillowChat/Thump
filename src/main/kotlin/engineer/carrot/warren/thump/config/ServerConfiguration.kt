package engineer.carrot.warren.thump.config

import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import engineer.carrot.warren.thump.helper.LogHelper
import net.minecraftforge.common.config.Configuration
import java.util.*

class ServerConfiguration(category: String, configuration: Configuration) {
    var ID = ""
    var server = ""
    var port = 6697
    var nickname = "thump-server"
    var channels: Map<String, String?> = HashMap()

    // Nickserv
    var identifyWithNickServ = false
    var nickServPassword = ""

    // TLS
    var useTLS = true
    var forceAcceptCertificates = false
    var forciblyAcceptedCertificates: Set<String> = HashSet()

    // Reconnect
    var shouldReconnectAutomatically = true
    var automaticReconnectDelaySeconds = 60
    var maxConsecutiveReconnectAttempts = 3

    init {
        this.ID = category

        configuration.setCategoryPropertyOrder(category, Lists.newArrayList(SERVER_KEY, PORT_KEY, NICKNAME_KEY, CHANNELS_KEY))
        this.server = configuration.getString(SERVER_KEY, category, this.server, "")
        this.port = configuration.getInt(PORT_KEY, category, this.port, PORT_MIN, PORT_MAX, "")
        this.nickname = configuration.getString(NICKNAME_KEY, category, this.nickname, "")
        val channels = configuration.getStringList(CHANNELS_KEY, category, emptyArray(), "")
        if (channels.size == 1 && channels[0].isEmpty()) {
            this.channels = Maps.newHashMap()
        } else {
            this.channels = parseChannels(Sets.newHashSet(*channels))
        }

        val nickservCategory = category + ".nickserv"
        configuration.setCategoryPropertyOrder(nickservCategory, Lists.newArrayList(IDENTIFY_WITH_NICKSERV_KEY, NICKSERV_PASSWORD_KEY))
        this.identifyWithNickServ = configuration.getBoolean(IDENTIFY_WITH_NICKSERV_KEY, nickservCategory, this.identifyWithNickServ, "")
        this.nickServPassword = configuration.getString(NICKSERV_PASSWORD_KEY, nickservCategory, this.nickServPassword, "")

        val tlsCategory = category + ".tls"
        configuration.setCategoryPropertyOrder(tlsCategory, Lists.newArrayList(USE_TLS_KEY, FORCE_ACCEPT_CERTIFICATES_KEY, FORCIBLY_ACCEPTED_CERTIFICATES_KEY))
        this.useTLS = configuration.getBoolean(USE_TLS_KEY, tlsCategory, this.useTLS, "")
        this.forceAcceptCertificates = configuration.getBoolean(FORCE_ACCEPT_CERTIFICATES_KEY, tlsCategory, this.forceAcceptCertificates, "")
        val forciblyAcceptedCertificates = configuration.getStringList(FORCIBLY_ACCEPTED_CERTIFICATES_KEY, tlsCategory, emptyArray(), "")
        this.forciblyAcceptedCertificates = Sets.newHashSet(*forciblyAcceptedCertificates)

        val reconnectCategory = category + ".reconnect"
        configuration.setCategoryPropertyOrder(reconnectCategory, Lists.newArrayList(SHOULD_RECONNECT_AUTOMATICALLY_KEY, AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY))
        this.shouldReconnectAutomatically = configuration.getBoolean(SHOULD_RECONNECT_AUTOMATICALLY_KEY, reconnectCategory, this.shouldReconnectAutomatically, "")
        this.automaticReconnectDelaySeconds = configuration.getInt(AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY, reconnectCategory, this.automaticReconnectDelaySeconds, AUTOMATIC_RECONNECT_DELAY_SECONDS_MIN, AUTOMATIC_RECONNECT_DELAY_SECONDS_MAX, "")
        this.maxConsecutiveReconnectAttempts = configuration.getInt(MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY, reconnectCategory, this.maxConsecutiveReconnectAttempts, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MIN, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MAX, "")
    }

    fun parseChannels(configurationStrings: Iterable<String>): Map<String, String?> {
        var channels: MutableMap<String, String?> = HashMap()

        stringsLoop@ for (configurationString in configurationStrings) {
            val equalsLocation = configurationString.indexOf('=')
            if (equalsLocation < 0) {
                channels.put(configurationString, null)
            } else {
                if (equalsLocation + 1 >= configurationString.length) {
                    LogHelper.warn("Channel entry '{}' had an equals in it, but no password set. Check your configuration!", configurationString)
                    continue@stringsLoop
                }

                val channelName = configurationString.take(equalsLocation)
                val channelKey = configurationString.drop(equalsLocation + 1)

                channels.put(channelName, channelKey)
            }
        }

        return channels
    }

    companion object {
        private val SERVER_KEY = "Server"
        private val PORT_KEY = "Port"
        private val PORT_MIN = 1
        private val PORT_MAX = 65535
        private val NICKNAME_KEY = "Nickname"
        private val CHANNELS_KEY = "Channels"
        private val IDENTIFY_WITH_NICKSERV_KEY = "IdentifyWithNickserv"
        private val NICKSERV_PASSWORD_KEY = "NickservPassword"
        private val USE_TLS_KEY = "UseTLS"
        private val FORCE_ACCEPT_CERTIFICATES_KEY = "ForceAcceptCertificates"
        private val FORCIBLY_ACCEPTED_CERTIFICATES_KEY = "ForciblyAcceptedCertificates"
        private val SHOULD_RECONNECT_AUTOMATICALLY_KEY = "ShouldReconnectAutomatically"
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY = "AutomaticReconnectDelaySeconds"
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_MIN = 0
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_MAX = 3600
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY = "MaxConsecutiveReconnectAttempts"
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MIN = 1
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MAX = 5
    }
}
