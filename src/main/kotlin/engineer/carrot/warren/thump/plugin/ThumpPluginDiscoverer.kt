package engineer.carrot.warren.thump.plugin

import engineer.carrot.warren.thump.api.IThumpServicePlugin
import engineer.carrot.warren.thump.api.ThumpServicePlugin
import engineer.carrot.warren.thump.helper.LogHelper
import net.minecraftforge.fml.common.discovery.ASMDataTable

object ThumpPluginDiscoverer {

    fun discover(asm: ASMDataTable): List<IThumpServicePlugin> {
        val thumpAnnotationName = ThumpServicePlugin::class.java.canonicalName

        return asm.getAll(thumpAnnotationName).mapNotNull {
            try {
                val asmClass = Class.forName(it.className).asSubclass(IThumpServicePlugin::class.java).kotlin

                return@mapNotNull if (asmClass.objectInstance != null) {
                    asmClass.objectInstance
                } else {
                    val firstEmptyConstructor = asmClass.constructors.firstOrNull { it.parameters.isEmpty() }

                    if (firstEmptyConstructor == null) {
                        LogHelper.warn("Couldn't initialise Thump plugin with name (didn't have a non-empty constructor): ${it.className}")
                    }

                    firstEmptyConstructor?.call()
                }
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

}
