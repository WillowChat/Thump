package engineer.carrot.warren.thump.api

import net.minecraftforge.common.config.Configuration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ThumpServicePlugin

interface IThumpServicePlugin {

    val id: String

    fun configure(context: ThumpPluginContext)
    fun start()
    fun stop()

    fun onMinecraftMessage(message: String)
    fun anyConnectionsMatch(name: String): Boolean

}

data class ThumpPluginContext(val configuration: Configuration)