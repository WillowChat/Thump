package engineer.carrot.warren.thump.api

interface IThumpServicePlugins {

    fun sendToAllServices(message: String)
    fun sendToAllMinecraftPlayers(message: String)

    fun reconfigureAll()
    fun startAll()
    fun stopAll()
    fun anyServicesMatch(name: String): Boolean

}