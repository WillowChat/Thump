package chat.willow.thump.minecraft

import chat.willow.thump.Thump
import chat.willow.thump.api.IThumpServiceSink
import chat.willow.thump.helper.StringHelper
import chat.willow.thump.helper.TokenHelper
import com.google.common.base.Joiner
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.DamageSource
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.CommandEvent
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.AchievementEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent


@Suppress("UNUSED")
class MinecraftEventsHandler(private val sink: IThumpServiceSink) {

    @SubscribeEvent
    fun onServerChatEvent(event: ServerChatEvent) {
        if (!Thump.configuration.events.minecraft.playerMessage) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.username)).addMessageToken(event.message).applyTokens(Thump.configuration.formats.minecraft.playerMessage)
        sink.sendToAllServices(message)
    }

    @SubscribeEvent
    fun onCommandEvent(event: CommandEvent) {
        val commandName = event.command.name
        val isServer = event.sender.name == "Server"

        if (commandName.equals("me", ignoreCase = true)) {
            if (isServer && !Thump.configuration.events.minecraft.serverAction) {
                return
            }

            if (!isServer && !Thump.configuration.events.minecraft.playerAction) {
                return
            }

            val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.sender.name)).addMessageToken(Joiner.on(" ").join(event.parameters)).applyTokens(Thump.configuration.formats.minecraft.playerAction)
            sink.sendToAllServices(message)

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
            sink.sendToAllServices(message)
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedInEvent(event: PlayerEvent.PlayerLoggedInEvent) {
        if (!Thump.configuration.events.minecraft.playerJoined) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.player.displayNameString)).applyTokens(Thump.configuration.formats.minecraft.playerJoined)
        sink.sendToAllServices(message)
    }

    @SubscribeEvent
    fun onPlayerLoggedOutEvent(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (!Thump.configuration.events.minecraft.playerLeft) {
            return
        }

        val message = TokenHelper().addUserToken(StringHelper.obfuscateNameIfNecessary(event.player.displayNameString)).applyTokens(Thump.configuration.formats.minecraft.playerLeft)
        sink.sendToAllServices(message)
    }

    @SubscribeEvent
    fun onLivingDeathEvent(event: LivingDeathEvent) {
        if (!Thump.configuration.events.minecraft.playerDeath) {
            return
        }

        val player = event.entityLiving as? EntityPlayer ?: return

        var deathMessage: ITextComponent? = player.combatTracker.deathMessage
        if (deathMessage == null) {
            deathMessage = generateNewDeathMessageFromLastDeath(player, event.source)
        }

        var unformattedText = deathMessage.unformattedText
        if (Thump.configuration.general.obfuscateUserSourceFromMinecraft) {
            val playerDisplayName = player.displayNameString
            val obfuscatedName = StringHelper.obfuscateString(playerDisplayName)

            unformattedText = unformattedText.replace(playerDisplayName, obfuscatedName)
        }

        val relevantEmoji = DeathEmojiPicker.relevantEmojisForDeathMessage(unformattedText)
        val message = TokenHelper().addMessageToken(unformattedText).addDeathEmojiToken(relevantEmoji).applyTokens(Thump.configuration.formats.minecraft.playerDeath)
        sink.sendToAllServices(message)
    }

    private fun generateNewDeathMessageFromLastDeath(player: EntityPlayer, source: DamageSource): ITextComponent {
        return source.getDeathMessage(player)
    }

    @SubscribeEvent
    fun onAchievementEvent(event: AchievementEvent) {
        if (!Thump.configuration.events.minecraft.playerAchievement) {
            return
        }

        val achievement = event.achievement
        val entityPlayer = event.entityPlayer as? EntityPlayerMP ?: return
        val statFile = entityPlayer.statFile

        if (!statFile.canUnlockAchievement(achievement) || statFile.hasAchievementUnlocked(achievement)) {
            return
        }

        val playerDisplayNameComponent = entityPlayer.displayName

        val achievementMessage = TextComponentTranslation("chat.type.achievement", playerDisplayNameComponent, achievement.createChatComponent())

        var unformattedText = achievementMessage.unformattedText
        if (Thump.configuration.general.obfuscateUserSourceFromMinecraft) {
            val playerDisplayName = playerDisplayNameComponent.unformattedText
            val obfuscatedName = StringHelper.obfuscateString(playerDisplayName)

            unformattedText = unformattedText.replace(playerDisplayName, obfuscatedName)
        }

        val message = TokenHelper().addMessageToken(unformattedText).applyTokens(Thump.configuration.formats.minecraft.playerAchievement)
        sink.sendToAllServices(message)
    }

}
