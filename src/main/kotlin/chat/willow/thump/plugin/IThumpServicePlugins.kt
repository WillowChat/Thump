package chat.willow.thump.plugin

interface IThumpServicePlugins {

    fun reconfigureAll()

    fun startAll()
    fun stopAll()

    fun statuses(): Map<String, List<String>>

    fun anyServicesMatch(name: String): Boolean

}