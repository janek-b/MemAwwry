package com.janek.memawwry.data

enum class CardState {
    COVERED, SELECTED, UNCOVERED
}

data class MemoryCard(
    val cardId: Int = 0,
    val imageUrl: String = "",
    val state: CardState = CardState.COVERED
)