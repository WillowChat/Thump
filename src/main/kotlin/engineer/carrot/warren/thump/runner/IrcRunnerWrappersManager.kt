package engineer.carrot.warren.thump.runner

import engineer.carrot.warren.thump.config.ServerConfiguration
import engineer.carrot.warren.thump.helper.LogHelper

interface IWrappersManager {
    fun sendToAllChannels(message: String)
    fun initialise(configuration: ServerConfiguration)
    fun removeAll()
    fun start(id: String): Boolean
    fun stop(id: String, shouldReconnect: Boolean = true): Boolean
    fun anyWrappersMatch(nickname: String): Boolean

    val wrappers: Map<String, IWrapper>
}

class IrcRunnerWrappersManager : IWrappersManager {
    override val wrappers = mutableMapOf<String, IWrapper>()

    override fun initialise(configuration: ServerConfiguration) {
        val id = configuration.ID

        wrappers[id] = IrcRunnerWrapper(id, configuration, this)
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

    override fun anyWrappersMatch(nickname: String): Boolean = wrappers.values.any { nickname.equals(it.nickname, ignoreCase = true) }
}
