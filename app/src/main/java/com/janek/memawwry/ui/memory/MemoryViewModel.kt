package com.janek.memawwry.ui.memory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.janek.memawwry.data.CardState
import com.janek.memawwry.data.MemoryCard
import com.janek.memawwry.data.MemoryRepository
import com.janek.memawwry.ui.GlideApp
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class MemoryViewModel(
    app: Application,
    repo: MemoryRepository
) : AndroidViewModel(app) {

    private lateinit var viewModelDisposable: Disposable
    private val eventEmitter: PublishSubject<MemoryEvent> = PublishSubject.create()
    private val viewState: BehaviorSubject<MemoryViewState> = BehaviorSubject.create()

    init {
        val viewChanges = eventEmitter
            .compose(eventToResult())
            .startWith(
                repo.getPuppyList()
                    .doOnNext { it.forEach { item -> GlideApp.with(app.applicationContext).load(item.imageUrl).preload() } }
                    .map { ReadyState.Content(MemoryResult.CardSelectResult(it)) }
            )
            .publish()

        viewChanges.compose(resultToViewState()).subscribe(viewState)

        viewChanges.autoConnect(0) { viewModelDisposable = it }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelDisposable.dispose()
    }

    fun processInputs(vararg es: Observable<out MemoryEvent>) {
        return Observable.mergeArray(*es).subscribe(eventEmitter)
    }

    fun viewState(): Observable<MemoryViewState> = viewState

    private fun eventToResult(): ObservableTransformer<MemoryEvent, ReadyState<out MemoryResult>> {
        return ObservableTransformer { upstream ->
            upstream.publish { o ->
                Observable.merge(
                    o.ofType(MemoryEvent.ScreenLoadEvent::class.java).compose(onScreenLoad()),
                    o.ofType(MemoryEvent.CardSelectEvent::class.java).compose(onCardSelect())
                )
            }
        }
    }

    private fun resultToViewState(): ObservableTransformer<ReadyState<out MemoryResult>, out MemoryViewState> {
        return ObservableTransformer { upstream ->
            upstream.scan(viewState.value ?: MemoryViewState()) { vs, result ->
                when (result) {
                    is ReadyState.Content -> {
                        when (result.packet) {
                            is MemoryResult.ScreenLoadResult -> vs
                            is MemoryResult.CardSelectResult -> vs.copy(adapterList = result.packet.cards)
                        }
                    }
                    is ReadyState.Loading -> vs
                    is ReadyState.Error -> vs
                }
            }
                .distinctUntilChanged()
        }
    }

    private fun onScreenLoad(): ObservableTransformer<MemoryEvent.ScreenLoadEvent, ReadyState<out MemoryResult.ScreenLoadResult>> {
        return ObservableTransformer { upstream ->
            upstream.map { ReadyState.Content(MemoryResult.ScreenLoadResult) }
        }
    }

    private fun onCardSelect(): ObservableTransformer<MemoryEvent.CardSelectEvent, ReadyState<MemoryResult.CardSelectResult>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap {
                Observable.just(viewState.value?.adapterList)
                    .flatMap { list ->
                        val selectedList = selectCard(list, it.card)
                        val processedList = processSelection(selectedList)
                        Observable.zip(
                            Observable.just(
                                ReadyState.Content(MemoryResult.CardSelectResult(selectedList)),
                                ReadyState.Content(MemoryResult.CardSelectResult(processedList))
                            ),
                            Observable.interval(0,1000, TimeUnit.MILLISECONDS),
                            BiFunction<ReadyState.Content<MemoryResult.CardSelectResult>,
                                    Long,
                                    ReadyState.Content<MemoryResult.CardSelectResult>> { obs, _-> obs }
                        )
                    }
                }
            }
    }

    private fun selectCard(list: List<MemoryCard>, card: MemoryCard) : List<MemoryCard> {
        return list.map { if (it.cardId == card.cardId) it.copy(state = CardState.SELECTED) else it }
    }

    private fun processSelection(list: List<MemoryCard>) : List<MemoryCard> {
        val selectedItems = list.filter { it.state == CardState.SELECTED }
        return if (selectedItems.size == 1) {
            list
        } else {
            val matchingCards = selectedItems
                .groupingBy { it.imageUrl }
                .eachCount()
                .filter { entry -> entry.value == 2}
                .keys

            list.map {
                if (matchingCards.contains(it.imageUrl)) {
                    it.copy(state = CardState.UNCOVERED)
                } else {
                    if (it.state == CardState.UNCOVERED) it else it.copy(state = CardState.COVERED)
                }
            }

        }
    }

}

sealed class ReadyState<T> {
    class Loading<T> : ReadyState<T>()
    data class Content<T>(val packet: T) : ReadyState<T>()
    data class Error<T>(val packet: T) : ReadyState<T>()
}

class MemoryViewModelFactory(
    private val app: Application,
    private val repo: MemoryRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MemoryViewModel(app, repo) as T
    }


}