package engineer.carrot.warren.thump.plugin

interface IThumpServicePlugins {

    fun reconfigureAll()

    fun startAll()
    fun stopAll()

    fun statuses(): Map<String, List<String>>

    fun anyServicesMatch(name: String): Boolean

}