package com.example.budgettracker.util

data class EmojiCategory(
    val title: String,
    val icons: List<String>,
    val keywords: List<String> // ▼▼▼ ADDED KEYWORDS LIST ▼▼▼
)

object EmojiHelper {
    private const val BASE_URL = "https://cdn.jsdelivr.net/npm/emoji-datasource-apple/img/apple/64/"

    fun getCategorizedEmojis(): List<EmojiCategory> {
        return listOf(
            EmojiCategory(
                "Smileys & Emotion",
                generateRange(0x1F600, 0x1F64F),
                listOf("smile", "happy", "sad", "face", "emotion", "cry", "laugh")
            ),
            EmojiCategory(
                "People & Body",
                generateRange(0x1F466, 0x1F478),
                listOf("people", "man", "woman", "boy", "girl", "human")
            ),
            EmojiCategory(
                "Transport",
                generateRange(0x1F680, 0x1F6C0),
                listOf("car", "bus", "train", "plane", "travel", "vehicle", "rocket")
            ),
            EmojiCategory(
                "Food & Drink",
                generateRange(0x1F32D, 0x1F37F),
                listOf("food", "drink", "fruit", "apple", "banana", "burger", "pizza", "vegetable")
            ),
            EmojiCategory(
                "Money & Objects",
                generateRange(0x1F4B0, 0x1F4C0),
                listOf("money", "cash", "dollar", "bag", "credit", "card", "office")
            ),
            EmojiCategory(
                "Activities & Flags",
                generateRange(0x1F3A0, 0x1F3C6), // Extended range to include some flags
                listOf("activity", "sport", "game", "ball", "music", "flag", "banner") // Added "flag" keyword
            )
        )
    }

    private fun generateRange(start: Int, end: Int): List<String> {
        val urls = mutableListOf<String>()
        for (i in start..end) {
            urls.add("$BASE_URL${Integer.toHexString(i)}.png")
        }
        return urls
    }
}