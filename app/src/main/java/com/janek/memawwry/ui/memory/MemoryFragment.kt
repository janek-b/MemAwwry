package com.janek.memawwry.ui.memory

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager

import com.janek.memawwry.R
import com.janek.memawwry.data.MemoryRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.memory_fragment.*

class MemoryFragment : Fragment() {

    companion object {
        fun newInstance(activity: GameSelection) = MemoryFragment().apply { gameSelection = activity }
    }

    private lateinit var gameSelection: GameSelection
    private lateinit var viewModel: MemoryViewModel
    private lateinit var listAdapter: MemoryAdapter
    private lateinit var gameOverDialog: AlertDialog

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val memoryEvents: PublishSubject<MemoryEvent.CardSelectEvent> = PublishSubject.create()
    private val newGameEvents: PublishSubject<MemoryEvent.NewGameEvent> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.memory_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupCardListView()

        gameOverDialog = AlertDialog.Builder(this.activity)
            .setMessage("Play Again?")
            .setTitle("You Won")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> startNewGame() })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> goToMainScreen() })
            .create()

        viewModel = ViewModelProviders.of(
            this,
            MemoryViewModelFactory(activity!!.application, MemoryRepository())
        ).get(MemoryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        viewModel.processInputs(memoryEvents, newGameEvents)

        disposables.add(
            viewModel
                .viewState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        renderViewState(it)
                    },
                    {
                        Log.d("Error", it.message)
                    }
                )
        )
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    private fun startNewGame() {
        newGameEvents.onNext(MemoryEvent.NewGameEvent)
    }

    private fun goToMainScreen() {
        gameSelection.goToGameSelection()
    }

    private fun renderViewState(viewState: MemoryViewState) {
        listAdapter.submitList(viewState.adapterList)
        if (viewState.gameOver) gameOverDialog.show()
    }

    private fun setupCardListView() {
        val layoutManager = GridLayoutManager(activity, 4)
        game_board.layoutManager = layoutManager


        listAdapter = MemoryAdapter { memoryEvents.onNext(MemoryEvent.CardSelectEvent(it)) }
        game_board.adapter = listAdapter
    }

    interface GameSelection{
        fun goToGameSelection()
    }
}
