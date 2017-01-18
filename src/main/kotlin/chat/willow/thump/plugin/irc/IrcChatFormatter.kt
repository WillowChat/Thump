package chat.willow.thump.plugin.irc

import chat.willow.thump.api.IServiceChatFormatter
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

object IrcChatFormatter: IServiceChatFormatter {

    override fun format(plaintext: String): ITextComponent {
        return TextComponentString(plaintext)
    }

    override fun format(text: ITextComponent): String {
        return text.unformattedText
    }

}