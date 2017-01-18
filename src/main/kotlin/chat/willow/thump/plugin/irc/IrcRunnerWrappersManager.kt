package chat.willow.thump.plugin.irc

import chat.willow.thump.api.IServiceChatFormatter
import chat.willow.thump.api.IThumpMinecraftSink
import chat.willow.thump.helper.LogHelper
import chat.willow.thump.plugin.irc.config.IrcServerConfiguration
import chat.willow.thump.plugin.irc.config.IrcServicePluginGeneralConfiguration

interface IWrappersManager {
    fun sendToAllChannels(message: String)
    fun initialise(ircServerConfiguration: IrcServerConfiguration, generalConfiguration: IrcServicePluginGeneralConfiguration, sink: IThumpMinecraftSink, formatter: IServiceChatFormatter)
    fun removeAll()
    fun start(id: String): Boolean
    fun stop(id: String, shouldReconnect: Boolean = true): Boolean
    fun anyWrappersMatch(nickname: String): Boolean

    val wrappers: Map<String, IWrapper>
}

class IrcRunnerWrappersManager : IWrappersManager {
    override val wrappers = mutableMapOf<String, IWrapper>()

    override fun initialise(ircServerConfiguration: IrcServerConfiguration, generalConfiguration: IrcServicePluginGeneralConfiguration, sink: IThumpMinecraftSink, formatter: IServiceChatFormatter) {
        val id = ircServerConfiguration.ID

        wrappers[id] = IrcRunnerWrapper(id, ircServerConfiguration, generalConfiguration, sink, formatter)
    }

    override fun removeAll() {
        wrappers.clear()
    }

    override fun sendToAllChannels(message: String) {
        for ((id, wrapper) in wrappers) {
            wrapper.sendMessageToAll(message)
        }
    }

    override fun start(id: String): Boolean {
        val wrapper = wrappers[id]
        if (wrapper == null) {
            LogHelper.error("couldn't start $id - wrapper not set up")
            return false
        }

        return wrapper.start()
    }

    override fun stop(id: String, shouldReconnect: Boolean): Boolean {
        val wrapper = wrappers[id]
        if (wrapper == null) {
            LogHelper.error("couldn't stop $id - wrapper not set up")
            return false
        }

        return wrapper.stop(shouldReconnect = shouldReconnect)
    }

    override fun anyWrappersMatch(nickname: String) = wrappers.values.any { nickname.equals(it.nickname, ignoreCase = true) }
}
