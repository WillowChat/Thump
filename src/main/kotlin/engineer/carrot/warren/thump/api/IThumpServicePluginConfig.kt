package engineer.carrot.warren.thump.api

interface IThumpServicePluginConfig<T> {

    fun load()
    fun save()
    val config: T

}