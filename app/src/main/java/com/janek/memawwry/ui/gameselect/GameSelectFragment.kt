package com.janek.memawwry.ui.gameselect

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.janek.memawwry.R
import kotlinx.android.synthetic.main.game_select_fragment.*

class GameSelectFragment : Fragment() {

    companion object {
        fun newInstance(activity: GameSelection) = GameSelectFragment().apply { gameSelection = activity }
    }

    private lateinit var gameSelection: GameSelection
    private lateinit var viewModel: GameSelectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.game_select_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GameSelectViewModel::class.java)

        puppy_button.setOnClickListener { startGame() }
    }

    private fun startGame() {
        gameSelection.onGameSelection()
    }

    fun setGameSelection(activity: GameSelection) {
        gameSelection = activity
    }

    interface GameSelection {
        fun onGameSelection()
    }

}
