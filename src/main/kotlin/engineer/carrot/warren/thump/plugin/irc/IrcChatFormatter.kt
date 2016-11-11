package engineer.carrot.warren.thump.plugin.irc

import engineer.carrot.warren.thump.api.IServiceChatFormatter
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

class IrcChatFormatter : IServiceChatFormatter {

    override fun format(plaintext: String): ITextComponent {
        return TextComponentString(plaintext)
    }

    override fun format(text: ITextComponent): String {
        return text.unformattedText
    }

}