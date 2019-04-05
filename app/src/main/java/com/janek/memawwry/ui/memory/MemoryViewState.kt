package com.janek.memawwry.ui.memory

import com.janek.memawwry.data.MemoryCard

data class MemoryViewState(
    val adapterList: List<MemoryCard> = emptyList(),
    val gameOver: Boolean = false
)

sealed class MemoryEvent {
    object ScreenLoadEvent : MemoryEvent()
    object NewGameEvent : MemoryEvent()
    data class CardSelectEvent(val card: MemoryCard) : MemoryEvent()
}

sealed class MemoryResult {
    object ScreenLoadResult : MemoryResult()
    data class NewGameResult(val cards: List<MemoryCard>): MemoryResult()
    data class CardSelectResult(val cards: List<MemoryCard>) : MemoryResult()
}