package chat.willow.thump.api

import net.minecraft.util.text.ITextComponent

interface IServiceChatFormatter {

    fun format(plaintext: String): ITextComponent
    fun format(text: ITextComponent): String

}