package engineer.carrot.warren.thump.api

import net.minecraft.util.text.ITextComponent

interface IServiceChatFormatter {

    fun format(plaintext: String): ITextComponent
    fun format(text: ITextComponent): String

}