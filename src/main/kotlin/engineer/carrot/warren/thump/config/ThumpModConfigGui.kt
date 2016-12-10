package engineer.carrot.warren.thump.config

import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.plugin.irc.IrcServicePlugin
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.config.DummyConfigElement
import net.minecraftforge.fml.client.config.GuiConfig
import net.minecraftforge.fml.client.config.IConfigElement

class ThumpModConfigGui : GuiConfig {

    @Suppress("UNUSED")
    constructor(parent: GuiScreen): super(parent, generateConfigElements(), engineer.carrot.warren.thump.Reference.MOD_ID, false, false, "Thump configuration")

    companion object {
        fun generateConfigElements(): List<IConfigElement> {
            return listOf(
                    createCategoryElement("Minecraft / General", Thump.configuration.generateConfigElements()),
                    createCategoryElement("IRC", IrcServicePlugin.configuration.connections.connectionConfigElements())
            )
        }

        fun createCategoryElement(name: String, childElements: List<IConfigElement>): IConfigElement {
            return DummyConfigElement.DummyCategoryElement(name, "", childElements)
        }
    }

    override fun initGui() {
        super.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)
    }
}