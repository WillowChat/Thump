package engineer.carrot.warren.thump.plugin.irc.config

import engineer.carrot.warren.thump.api.IThumpServicePluginConfig
import net.minecraftforge.common.config.Configuration
import java.io.File

class IrcServicePluginConfiguration(val baseConfig: Configuration): IThumpServicePluginConfig<IrcServersConfiguration> {

    override lateinit var config: IrcServersConfiguration

    override fun load() {
        baseConfig.load()

        this.config = IrcServersConfiguration(this.baseConfig)
    }

    override fun save() {
        this.baseConfig.save()
    }

}