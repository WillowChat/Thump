package engineer.carrot.warren.thump.plugin.irc

import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class IrcChatFormatterTests {

    lateinit var formatter: IrcChatFormatter

    @Before
    fun setUp() {
        formatter = IrcChatFormatter()
    }

    @Test
    fun `format plaintext wraps input in a string component`() {
        val text = formatter.format("the quick brown fox jumps over the lazy dog")

        assertEquals(TextComponentString("the quick brown fox jumps over the lazy dog"), text)
    }

    @Test
    fun `format textcomponent returns unformatted text in a single string`() {
        val firstComponent = TextComponentString("the quick brown fox jumps over the lazy")
        val secondComponent = TextComponentString(" dog")
        secondComponent.style.bold = true
        secondComponent.style.italic = true
        secondComponent.style.color = TextFormatting.BLUE

        val plaintext = formatter.format(firstComponent.appendSibling(secondComponent))

        assertEquals("the quick brown fox jumps over the lazy dog", plaintext)
    }

}