package com.janek.memawwry.data

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MemoryRepository() {

    private val client: MemoryApi = MemoryClient().getClient()

    fun getPuppyList() : Observable<List<MemoryCard>> {
        return client.getUrl()
            .subscribeOn(Schedulers.io())
            .map { "https://random.dog/${it.string()}" }
            .repeat()
            .distinct()
            .take(10)
            .collect({ mutableListOf<String>() }, { list, item -> list.add(item) })
            .map { convertToCards(it.toList()) }
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
    }

    private fun convertToCards(urls: List<String>): List<MemoryCard> {
        val cards = urls.map { MemoryCard(imageUrl = it) }
        return (cards + cards).shuffled().mapIndexed { index, memoryCard -> memoryCard.copy(cardId = index) }
    }
}