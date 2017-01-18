package chat.willow.thump.plugin.irc.config

import chat.willow.thump.helper.LogHelper
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import net.minecraftforge.common.config.Configuration
import java.util.*

class IrcServerConfiguration(id: String, configuration: Configuration) {
    var ID = ""
    var server = ""
    var port = 6697
    var nickname = "thump-server"
    var channels: Map<String, String?> = HashMap()
    var serverPassword: String? = null
    var ignoredNicks: Set<String> = HashSet()

    // Nickserv
    var identifyWithNickServ = false
    var nickServAccount = ""
    var nickServPassword = ""

    // SASL
    var identifyWithSasl = false
    var saslAccount = ""
    var saslPassword = ""

    // TLS
    var useTLS = true
    var forceAcceptCertificates = false
    var forciblyAcceptedCertificates: Set<String> = HashSet()

    // Reconnect
    var shouldReconnectAutomatically = true
    var automaticReconnectDelaySeconds = 60
    var maxConsecutiveReconnectAttempts = 3

    init {
        this.ID = id
        val category = "connections.$ID"

        configuration.setCategoryPropertyOrder(category, Lists.newArrayList(SERVER_KEY, PORT_KEY, NICKNAME_KEY, CHANNELS_KEY, IGNORED_NICKS_KEY))
        this.server = configuration.getString(SERVER_KEY, category, this.server, "")
        this.port = configuration.getInt(PORT_KEY, category, this.port, PORT_MIN, PORT_MAX, "")
        this.nickname = configuration.getString(NICKNAME_KEY, category, this.nickname, "")
        val channels = configuration.getStringList(CHANNELS_KEY, category, emptyArray(), "")
        if (channels.size == 1 && channels[0].isEmpty()) {
            this.channels = Maps.newHashMap()
        } else {
            this.channels = parseChannels(Sets.newHashSet(*channels))
        }
        val rawServerPassword = configuration.getString(SERVER_PASSWORD_KEY, category, "", "")
        if (rawServerPassword == "") {
            this.serverPassword = null
        } else {
            this.serverPassword = rawServerPassword
        }
        val ignoredNicks = configuration.getStringList(IGNORED_NICKS_KEY, category, emptyArray(), "")
        if (ignoredNicks.size == 1 && ignoredNicks[0].isEmpty()) {
            this.ignoredNicks = setOf()
        } else {
            this.ignoredNicks = Sets.newHashSet(*ignoredNicks)
        }

        val nickservCategory = category + ".auth.nickserv"
        configuration.setCategoryPropertyOrder(nickservCategory, Lists.newArrayList(IDENTIFY_WITH_NICKSERV_KEY, NICKSERV_PASSWORD_KEY))
        this.identifyWithNickServ = configuration.getBoolean(IDENTIFY_WITH_NICKSERV_KEY, nickservCategory, this.identifyWithNickServ, "")
        this.nickServAccount = configuration.getString(NICKSERV_ACCOUNT_KEY, nickservCategory, this.nickServAccount, "")
        this.nickServPassword = configuration.getString(NICKSERV_PASSWORD_KEY, nickservCategory, this.nickServPassword, "")

        val saslCategory = category + ".auth.sasl"
        configuration.setCategoryPropertyOrder(nickservCategory, Lists.newArrayList(IDENTIFY_WITH_SASL_KEY, SASL_PASSWORD_KEY))
        this.identifyWithSasl = configuration.getBoolean(IDENTIFY_WITH_SASL_KEY, saslCategory, this.identifyWithSasl, "")
        this.saslAccount = configuration.getString(SASL_ACCOUNT_KEY, saslCategory, this.saslAccount, "")
        this.saslPassword = configuration.getString(SASL_PASSWORD_KEY, saslCategory, this.saslPassword, "")

        val tlsCategory = category + ".tls"
        configuration.setCategoryPropertyOrder(tlsCategory, Lists.newArrayList(USE_TLS_KEY, FORCE_ACCEPT_CERTIFICATES_KEY, FORCIBLY_ACCEPTED_CERTIFICATES_KEY))
        this.useTLS = configuration.getBoolean(USE_TLS_KEY, tlsCategory, this.useTLS, "")
        this.forceAcceptCertificates = configuration.getBoolean(FORCE_ACCEPT_CERTIFICATES_KEY, tlsCategory, this.forceAcceptCertificates, "")
        val forciblyAcceptedCertificates = configuration.getStringList(FORCIBLY_ACCEPTED_CERTIFICATES_KEY, tlsCategory, emptyArray(), FORCIBLY_ACCEPTED_CERTIFICATES_COMMENT)
        this.forciblyAcceptedCertificates = Sets.newHashSet(*forciblyAcceptedCertificates)

        val reconnectCategory = category + ".reconnect"
        configuration.setCategoryPropertyOrder(reconnectCategory, Lists.newArrayList(SHOULD_RECONNECT_AUTOMATICALLY_KEY, AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY))
        this.shouldReconnectAutomatically = configuration.getBoolean(SHOULD_RECONNECT_AUTOMATICALLY_KEY, reconnectCategory, this.shouldReconnectAutomatically, "")
        this.automaticReconnectDelaySeconds = configuration.getInt(AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY, reconnectCategory, this.automaticReconnectDelaySeconds, AUTOMATIC_RECONNECT_DELAY_SECONDS_MIN, AUTOMATIC_RECONNECT_DELAY_SECONDS_MAX, "")
        this.maxConsecutiveReconnectAttempts = configuration.getInt(MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY, reconnectCategory, this.maxConsecutiveReconnectAttempts, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MIN, MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MAX, "")
    }

    fun parseChannels(configurationStrings: Iterable<String>): Map<String, String?> {
        val channels: MutableMap<String, String?> = HashMap()

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
        private val SERVER_PASSWORD_KEY = "ServerPassword"
        private val IGNORED_NICKS_KEY = "IgnoredNicks"
        private val IDENTIFY_WITH_NICKSERV_KEY = "IdentifyWithNickserv"
        private val NICKSERV_ACCOUNT_KEY = "NickservAccount"
        private val NICKSERV_PASSWORD_KEY = "NickservPassword"
        private val IDENTIFY_WITH_SASL_KEY = "IdentifyWithSASL"
        private val SASL_ACCOUNT_KEY = "SASLAccount"
        private val SASL_PASSWORD_KEY = "SASLPassword"
        private val USE_TLS_KEY = "UseTLS"
        private val FORCE_ACCEPT_CERTIFICATES_KEY = "ForceAcceptCertificates"
        private val FORCIBLY_ACCEPTED_CERTIFICATES_KEY = "ForciblyAcceptedCertificates"
        private val FORCIBLY_ACCEPTED_CERTIFICATES_COMMENT = "Leave blank with 'ForceAcceptCertificates' on to enter the danger zone and forcefully accept all presented certificates"
        private val SHOULD_RECONNECT_AUTOMATICALLY_KEY = "ShouldReconnectAutomatically"
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_KEY = "AutomaticReconnectDelaySeconds"
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_MIN = 0
        private val AUTOMATIC_RECONNECT_DELAY_SECONDS_MAX = 3600
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_KEY = "MaxConsecutiveReconnectAttempts"
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MIN = 1
        private val MAX_CONSECUTIVE_RECONNECT_ATTEMPTS_MAX = 5
    }
}
