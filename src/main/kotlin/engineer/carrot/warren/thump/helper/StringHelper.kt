package engineer.carrot.warren.thump.helper

import com.google.common.base.CharMatcher
import engineer.carrot.warren.thump.Thump

object StringHelper {
    val IRC_CHARACTER_BLACKLIST_MATCHER: CharMatcher = object : CharMatcher() {
        override fun matches(c: Char): Boolean {
            when (c) {
                '\u200b' -> return true
                0x0f.toChar() -> return true // Formatting: reset
                0x1d.toChar() -> return true // Formatting: italics
                0x1f.toChar() -> return true // Formatting: underline
                0x03.toChar() -> return true // Formatting: colour
                0x16.toChar() -> return true // Formatting: reverse colour
                else -> return false
            }
        }
    }


    fun obfuscateString(name: String): String {
        var nameCopy = name
        if (nameCopy.length >= 2) {
            nameCopy = nameCopy[0] + "\u200b" + nameCopy.substring(1)
        }

        return nameCopy
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
