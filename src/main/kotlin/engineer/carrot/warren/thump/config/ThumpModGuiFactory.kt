package engineer.carrot.warren.thump.config

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory

class ThumpModGuiFactory : IModGuiFactory {
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