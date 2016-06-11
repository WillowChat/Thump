package engineer.carrot.warren.thump.api

import net.minecraftforge.common.config.Configuration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ThumpServicePlugin

interface IThumpServicePlugin {

    val name: String
    fun configure(context: ThumpPluginContext)
    fun start()
    fun stop()

}

data class ThumpPluginContext(val configuration: Configuration)