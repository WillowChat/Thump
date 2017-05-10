package chat.willow.thump.config

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class ThumpModGuiFactory : IModGuiFactory {

    override fun createConfigGui(parentScreen: GuiScreen?): GuiScreen? {
        if (parentScreen == null) {
            return null
        }

        return ThumpModConfigGui(parentScreen)
    }

    override fun hasConfigGui(): Boolean {
        return true
    }

    override fun initialize(minecraftInstance: Minecraft?) {

    }

    override fun mainConfigGuiClass(): Class<out GuiScreen> {
        return ThumpModConfigGui::class.java
    }

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement>? {
        return null
    }

    override fun getHandlerFor(element: IModGuiFactory.RuntimeOptionCategoryElement?): IModGuiFactory.RuntimeOptionGuiHandler? {
        return null
    }
}