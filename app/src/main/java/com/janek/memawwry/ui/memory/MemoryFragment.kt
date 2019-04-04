package com.janek.memawwry.ui.memory

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
        fun newInstance() = MemoryFragment()
    }

    private lateinit var viewModel: MemoryViewModel
    private lateinit var listAdapter: MemoryAdapter

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val memoryCardClick: PublishSubject<MemoryEvent.CardSelectEvent> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.memory_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupCardListView()

        viewModel = ViewModelProviders.of(
            this,
            MemoryViewModelFactory(activity!!.application, MemoryRepository())
        ).get(MemoryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        viewModel.processInputs(memoryCardClick)

        disposables.add(
            viewModel
                .viewState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { vs ->
                        listAdapter.submitList(vs.adapterList)
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

    private fun setupCardListView() {
        val layoutManager = GridLayoutManager(activity, 4)
        game_board.layoutManager = layoutManager


        listAdapter = MemoryAdapter { memoryCardClick.onNext(MemoryEvent.CardSelectEvent(it)) }
        game_board.adapter = listAdapter
    }
}
