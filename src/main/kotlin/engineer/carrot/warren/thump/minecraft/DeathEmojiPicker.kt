package engineer.carrot.warren.thump.minecraft

import java.util.*

object DeathEmojiPicker {
    val radiation = mapOf(setOf("radiation") to listOf("\u2622"))
    val shot = mapOf(setOf("shot by") to listOf("\uD83C\uDFF9"))
    val cactus = mapOf(setOf("pricked", "cactus", "cacti") to listOf("\uD83C\uDF35"))
    val drown = mapOf(setOf("drowned") to listOf("\uD83C\uDF0A"))
    val kinetic = mapOf(setOf("kinetic") to listOf("\uD83D\uDCA8"))
    val blownUp = mapOf(setOf("blew up", "blown up") to listOf("\uD83D\uDCA5", "\uD83D\uDCA3"))
    val flames = mapOf(setOf("flames", "burned", "burnt", "fire", "lava") to listOf("\uD83D\uDD25", "\uD83D\uDE92"))
    val lightning = mapOf(setOf("lightning") to listOf("\uD83C\uDF29"))
    val electrocuted = mapOf(setOf("electrocut") to listOf("\u26A1"))
    val magic = mapOf(setOf("magic") to listOf("\u2728"))
    val starved = mapOf(setOf("starved") to listOf("\uD83D\uDC80"))
    val wither = mapOf(setOf("wither") to listOf("\uD83D\uDC80"))
    val thorns = mapOf(setOf("while trying to hurt") to listOf("\uD83D\uDDE1"))
    val none = "\uD83D\uDC7B"

    val contextualWordEmojiMaps = listOf(radiation, shot, cactus, drown, kinetic, blownUp, flames, lightning, electrocuted, magic,
                                            starved, wither, thorns)

    private val random = Random()

    fun relevantEmojisForDeathMessage(deathMessage: String): String {
        val relevantEmojis = mutableListOf<String>()

        contextualWordEmojiMaps.forEach { wordEmojiMap ->
            wordEmojiMap.forEach { keywordsToEmojis ->
                val doAnyKeywordsMatch = !keywordsToEmojis.key.filter { deathMessage.contains(it) }.isEmpty()

                if (doAnyKeywordsMatch) {
                    val contextuallyRelevantEmojis = keywordsToEmojis.value
                    relevantEmojis += contextuallyRelevantEmojis[random.nextInt(contextuallyRelevantEmojis.size)]
                }
            }
        }

        if (relevantEmojis.isEmpty()) {
            return none
        }

        return relevantEmojis.joinToString(separator = "")
    }
}