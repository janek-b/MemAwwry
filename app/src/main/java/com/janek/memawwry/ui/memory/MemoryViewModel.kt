package com.janek.memawwry.ui.memory

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Card(val cardId: String, val imageUrl: String, var unCovered: Boolean = false)
data class Game(val cards: List<Card>, var firstCard: String = "")

class MemoryViewModel : ViewModel() {
    private val cards = (0..5).map { Card("card $it", "url $it") }
    private val cardList = (cards + cards).shuffled()
    private val game: MutableLiveData<Game> = MutableLiveData<Game>().apply { postValue(Game(cardList)) }

    fun getLiveGame() : MutableLiveData<Game> {
        return game
    }

    fun playCard(card: String, position: Int) {
        game.value?.apply {
            val selection = this.cards.mapIndexed { index, item ->
                if (index == position) Card(item.cardId, item.imageUrl, true) else item
            }
            game.postValue(Game(selection, this.firstCard))
        }

        Handler().postDelayed({
            game.value?.apply {
                val newGame = when (this.firstCard) {
                    "" -> {
                        val newCardList = this.cards.mapIndexed { index, item ->
                            if (index == position) Card(item.cardId, item.imageUrl, true) else item
                        }
                        Game(newCardList, card)
                    }
                    card -> {
                        val newCardList = this.cards.map {
                            if (it.cardId == card) Card(it.cardId, it.imageUrl, true) else it
                        }
                        Game(newCardList, "")
                    }
                    else -> {
                        val newCardList = this.cards.map {
                            if (it.cardId == card || it.cardId == this.firstCard) Card(it.cardId, it.imageUrl, false) else it
                        }
                        Game(newCardList, "")
                    }
                }
                game.postValue(newGame)
            }
        }, 500)

    }
}
