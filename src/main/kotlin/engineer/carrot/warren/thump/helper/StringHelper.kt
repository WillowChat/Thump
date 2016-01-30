package engineer.carrot.warren.thump.helper

import com.google.common.base.CharMatcher
import engineer.carrot.warren.thump.Thump

object StringHelper {
    val IRC_CHARACTER_BLACKLIST_MATCHER: CharMatcher = object : CharMatcher() {
        override fun matches(c: Char): Boolean {
            when (c) {
                '\u200b' -> return true
                else -> return false
            }
        }
    }


    fun obfuscateString(name: String): String {
        var name = name
        if (name.length >= 2) {
            name = name[0] + "\u200b" + name.substring(1)
        }

        return name
    }

    fun obfuscateNameIfNecessary(name: String): String {
        return if (Thump.configuration.general.obfuscateUserSourceFromMinecraft)
            StringHelper.obfuscateString(name)
        else
            name
    }

    fun stripBlacklistedIRCCharacters(original: String): String {
        return IRC_CHARACTER_BLACKLIST_MATCHER.removeFrom(original)
    }
}
