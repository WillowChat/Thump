package chat.willow.thump.plugin

import chat.willow.thump.api.IThumpServicePlugin
import chat.willow.thump.api.ThumpServicePlugin
import chat.willow.thump.helper.LogHelper
import net.minecraftforge.fml.common.discovery.ASMDataTable

object ThumpPluginDiscoverer {

    fun discover(asm: ASMDataTable): List<IThumpServicePlugin> {
        val thumpAnnotationName = ThumpServicePlugin::class.java.canonicalName

        return asm.getAll(thumpAnnotationName).mapNotNull {
            try {
                val asmClass = Class.forName(it.className).asSubclass(IThumpServicePlugin::class.java)

                return@mapNotNull extractObjectInstance(asmClass) ?: asmClass.newInstance()
            } catch(exception: ClassNotFoundException) {
                LogHelper.warn("Couldn't find class for Thump plugin with name: ${it.className}")
            } catch(exception: ClassCastException) {
                LogHelper.warn("Couldn't initialise Thump plugin with name (didn't implement IThumpPlugin): ${it.className}")
            } catch(exception: InstantiationException) {
                LogHelper.warn("Couldn't initialise Thump plugin with name (failed to instantiate): ${it.className}")
            } catch(exception: IllegalAccessException) {
                LogHelper.warn("Couldn't initialise Thump plugin with name (illegal access): ${it.className}")
            }

            return@mapNotNull null
        }

    }

    private fun <T> extractObjectInstance(fromClass: Class<T>): T? {
        try {
            @Suppress("UNCHECKED_CAST")
            return fromClass.getDeclaredField("INSTANCE")?.get(null) as? T
        } catch(exception: IllegalArgumentException) {
            LogHelper.warn("Couldn't initialise Thump plugin with name (Kotlin error - object had non null initialiser?): ${fromClass.name}")
        } catch (exception: IllegalAccessException) {
            LogHelper.warn("Couldn't initialise Thump plugin with name (Kotlin error - initialiser wasn't public?): ${fromClass.name}")
        } catch (exception: SecurityException) {
            LogHelper.warn("Couldn't initialise Thump plugin with name (security error): ${fromClass.name}")
        } catch (exception: NoSuchFieldException) {
            // Not an object
        }

        return null
    }

}
