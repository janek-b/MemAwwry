package com.janek.memawwry.ui.memory

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.janek.memawwry.R
import kotlinx.android.synthetic.main.memory_fragment.*

class MemoryFragment : Fragment() {

    companion object {
        fun newInstance() = MemoryFragment()
    }

    private lateinit var viewModel: MemoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.memory_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MemoryViewModel::class.java)

        game_board.layoutManager = LinearLayoutManager(activity)

        val adapter = MemoryAdapter { card, i -> cardSelected(card, i) }
        game_board.adapter = adapter
        viewModel.getLiveGame().observe(this, Observer { adapter.setCards(it.cards) })
    }

    private fun cardSelected(card: Card, position: Int) {
        viewModel.playCard(card.cardId, position)
    }
}
