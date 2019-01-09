package com.janek.memawwry.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.janek.memawwry.R
import com.janek.memawwry.ui.gameselect.GameSelectFragment
import com.janek.memawwry.ui.memory.MemoryFragment

class MainActivity : AppCompatActivity(), GameSelectFragment.GameSelection {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, GameSelectFragment.newInstance(this)).commit()
    }

    override fun onGameSelection() {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("puppyMemory")
            .replace(R.id.fragmentContainer, MemoryFragment.newInstance(), null).commit()
    }
}
