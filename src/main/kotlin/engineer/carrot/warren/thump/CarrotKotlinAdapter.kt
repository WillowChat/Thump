package engineer.carrot.warren.thump

import cpw.mods.fml.common.FMLModContainer
import cpw.mods.fml.common.ILanguageAdapter
import cpw.mods.fml.common.ModContainer
import cpw.mods.fml.relauncher.Side
import org.apache.logging.log4j.LogManager

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Derived from `Forgelin` by Arkan <arkan@drakon.io> : https://github.com/emberwalker/forgelin
 */
@Suppress("UNUSED")
class CarrotKotlinAdapter : ILanguageAdapter {

    private val logger = LogManager.getLogger("ILanguageAdapter/CarrotKotlin")

    override fun setProxy(target: Field, proxyTarget: Class<*>, proxy: Any) {
        logger.debug("Setting proxy on target: {}.{} -> {}", target.declaringClass.simpleName, target.name, proxy)

        val instanceField = findInstanceFieldOrThrow(proxyTarget)
        val modObject = findModObjectOrThrow(instanceField)

        target.set(modObject, proxy)
    }

    override fun getNewInstance(container: FMLModContainer?, objectClass: Class<*>, classLoader: ClassLoader, factoryMarkedAnnotation: Method?): Any? {
        logger.debug("Constructing new instance of {}", objectClass.simpleName)

        val instanceField = findInstanceFieldOrThrow(objectClass)
        val modObject = findModObjectOrThrow(instanceField)

        return modObject
    }

    override fun supportsStatics() = false
    override fun setInternalProxies(mod: ModContainer?, side: Side?, loader: ClassLoader?) = Unit

    private fun findInstanceFieldOrThrow(targetClass: Class<*>): Field {
        val instanceField: Field = try {
            targetClass.getField("INSTANCE")
        } catch (exception: NoSuchFieldException) {
            throw noInstanceFieldException(exception)
        } catch (exception: SecurityException) {
            throw instanceSecurityException(exception)
        }

        return instanceField
    }

    private fun findModObjectOrThrow(instanceField: Field): Any {
        val modObject = try {
            instanceField.get(null)
        } catch (exception: IllegalArgumentException) {
            throw unexpectedInitialiserSignatureException(exception)
        } catch (exception: IllegalAccessException) {
            throw wrongVisibilityOnInitialiserException(exception)
        }

        return modObject
    }

    private fun noInstanceFieldException(exception: Exception) = KotlinAdapterException("Couldn't find INSTANCE singleton on Kotlin @Mod container", exception)
    private fun instanceSecurityException(exception: Exception) = KotlinAdapterException("Security violation accessing INSTANCE singleton on Kotlin @Mod container", exception)
    private fun modObjectFailedToInitialiseException(exception: Exception) = KotlinAdapterException("Failed to initialise Kotlin @Mod object", exception)
    private fun unexpectedInitialiserSignatureException(exception: Exception) = KotlinAdapterException("Kotlin @Mod object has an unexpected initialiser signature, somehow?", exception)
    private fun wrongVisibilityOnInitialiserException(exception: Exception) = KotlinAdapterException("Initialiser on Kotlin @Mod object isn't `public`", exception)

    private class KotlinAdapterException(message: String, exception: Exception): RuntimeException("Kotlin adapter error - do not report to Forge! " + message, exception)
}