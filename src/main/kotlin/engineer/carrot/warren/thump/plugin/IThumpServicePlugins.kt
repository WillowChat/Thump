package engineer.carrot.warren.thump.plugin

interface IThumpServicePlugins {

    fun reconfigureAll()

    fun startAll()
    fun stopAll()

    fun anyServicesMatch(name: String): Boolean

}