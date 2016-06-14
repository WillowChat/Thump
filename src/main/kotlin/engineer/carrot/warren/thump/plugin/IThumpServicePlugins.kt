package engineer.carrot.warren.thump.plugin

import engineer.carrot.warren.thump.api.IThumpServicePlugin

interface IThumpServicePlugins {

    fun reconfigureAll()

    fun startAll()
    fun stopAll()

    fun statuses(): Map<String, List<String>>

    fun anyServicesMatch(name: String): Boolean

}