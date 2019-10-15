package com.example.lenovo.meme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_meme.*

class MemeActivity : AppCompatActivity() {

    private var state = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme)

        setLoading()
    }

    override fun onStart() {
        super.onStart()

        revealUI(state)
    }

    private fun setLoading() {

        tvInstructionHint1.alpha = 0f
        tvInstructionHint2.alpha = 0f
        btnPick.alpha = 0f
    }

    private fun setupAnimation(viewElement: View,
                               startDelay: Long = 0,
                               duration: Long = 1000) {
        viewElement.animate()
                .setStartDelay(startDelay)
                .alpha(1f)
                .duration = duration
    }

    private fun revealUI(level: Int) {
        when (level) {
            0 -> doStepZero()
           /* 1 -> setTickVisible(ivTick1)
            2 -> setTickVisible(ivTick2)
            3 -> {
                setButtonText(R.string.setup_button_finish)
                setTickVisible(ivTick3)
            }*/
        }
        state++
    }

    private fun doStepZero() {
        setupAnimation(tvInstructionHint1)
        setupAnimation(tvInstructionHint2, 1000, 500)
        setupAnimation(btnPick, 1500, 500)
    }
}
