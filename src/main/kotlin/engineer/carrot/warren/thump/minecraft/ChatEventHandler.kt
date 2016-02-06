package engineer.carrot.warren.thump.minecraft

import com.google.common.base.Joiner
import engineer.carrot.warren.thump.Thump
import engineer.carrot.warren.thump.connection.ConnectionManager
import engineer.carrot.warren.thump.helper.StringHelper
import engineer.carrot.warren.thump.helper.TokenHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.stats.Achievement
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.DamageSource
import net.minecraft.util.IChatComponent
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AchievementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent

@Suppress("UNUSED")
class ChatEventHandler(private val connectionManager: ConnectionManager) {

    @SubscribeEvent
    fun onServerChatEvent(event: ServerChatEvent) {
        if (!Thump.configuration.events.minecraft.playerMessage) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.username)).addMessageToken(event.message).applyTokens(Thump.configuration.formats.minecraft.playerMessage)
        this.connectionManager.sendMessageToAllChannels(message)
    }

    @SubscribeEvent
    fun onCommandEvent(event: CommandEvent) {
        val commandName = event.command.commandName
        val isServer = event.sender.name == "Server"

        if (commandName.equals("me", ignoreCase = true)) {
            if (isServer && !Thump.configuration.events.minecraft.serverAction) {
                return
            }

            if (!isServer && !Thump.configuration.events.minecraft.playerAction) {
                return
            }

            val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.sender.name)).addMessageToken(Joiner.on(" ").join(event.parameters)).applyTokens(Thump.configuration.formats.minecraft.playerAction)
            this.connectionManager.sendMessageToAllChannels(message)

            return
        }

        if (commandName.equals("say", ignoreCase = true)) {
            if (isServer && !Thump.configuration.events.minecraft.serverMessage) {
                return
            }

            if (!isServer && !Thump.configuration.events.minecraft.playerMessage) {
                return
            }

            val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.sender.name)).addMessageToken(Joiner.on(" ").join(event.parameters)).applyTokens(Thump.configuration.formats.minecraft.playerMessage)
            this.connectionManager.sendMessageToAllChannels(message)
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedInEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        if (!Thump.configuration.events.minecraft.playerJoined) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.player.displayNameString)).applyTokens(Thump.configuration.formats.minecraft.playerJoined)
        this.connectionManager.sendMessageToAllChannels(message)
    }

    @SubscribeEvent
    fun onPlayerLoggedOutEvent(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (!Thump.configuration.events.minecraft.playerLeft) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.player.displayNameString)).applyTokens(Thump.configuration.formats.minecraft.playerLeft)
        this.connectionManager.sendMessageToAllChannels(message)
    }

    @SubscribeEvent
    fun onLivingDeathEvent(event: LivingDeathEvent) {
        if (!Thump.configuration.events.minecraft.playerDeath) {
            return
        }

        val player = event.entityLiving as? EntityPlayer ?: return

        var deathMessage: IChatComponent? = player.combatTracker.deathMessage
        if (deathMessage == null) {
            deathMessage = generateNewDeathMessageFromLastDeath(player, event.source)
        }

        var unformattedText = deathMessage.unformattedText
        if (Thump.configuration.general.obfuscateUserSourceFromMinecraft) {
            val playerDisplayName = player.displayNameString
            val obfuscatedName = StringHelper.obfuscateString(playerDisplayName)

            unformattedText = unformattedText.replace(playerDisplayName, obfuscatedName)
        }

        val message = TokenHelper().addMessageToken(unformattedText).applyTokens(Thump.configuration.formats.minecraft.playerDeath)
        this.connectionManager.sendMessageToAllChannels(message)
    }

    private fun generateNewDeathMessageFromLastDeath(player: EntityPlayer, source: DamageSource): IChatComponent {
        return source.getDeathMessage(player)
    }

    @SubscribeEvent
    fun onAchievementEvent(event: AchievementEvent) {
        if (!Thump.configuration.events.minecraft.playerAchievement) {
            return
        }

        val entityPlayer = event.entityPlayer as? EntityPlayerMP ?: return

        val hasAchievementUnlocked = entityPlayer.statFile.hasAchievementUnlocked(event.achievement)
        if (hasAchievementUnlocked) {
            return
        }

        var hasParentAchievementsUnlocked = true
        var achievement: Achievement? = event.achievement.parentAchievement
        var depth = 0

        // NOTE: depth is included just in case the achievement graph isn't acyclic for some reason
        while (achievement != null) {
            if (!(event.entityPlayer as EntityPlayerMP).statFile.hasAchievementUnlocked(achievement)) {
                hasParentAchievementsUnlocked = false
                break
            }

            depth++
            if (depth >= 30) {
                hasParentAchievementsUnlocked = false
                break
            }

            achievement = achievement.parentAchievement
        }

        if (!hasParentAchievementsUnlocked) {
            return
        }

        val playerDisplayNameComponent = event.entityPlayer.displayName

        val achievementMessage = ChatComponentTranslation("chat.type.achievement", playerDisplayNameComponent, event.achievement.func_150955_j())

        var unformattedText = achievementMessage.unformattedText
        if (Thump.configuration.general.obfuscateUserSourceFromMinecraft) {
            val playerDisplayName = playerDisplayNameComponent.unformattedText
            val obfuscatedName = StringHelper.obfuscateString(playerDisplayName)

            unformattedText = unformattedText.replace(playerDisplayName, obfuscatedName)
        }

        val message = TokenHelper().addMessageToken(unformattedText).applyTokens(Thump.configuration.formats.minecraft.playerAchievement)
        this.connectionManager.sendMessageToAllChannels(message)
    }
}
