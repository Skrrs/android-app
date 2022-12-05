package com.example.aop.part2.mask.utils.record

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.example.aop.part2.mask.R

//import com.example.mask_.Test.State

class RecordButton(
    context: Context,
    attrs: AttributeSet
) : AppCompatImageButton(context, attrs) {

    fun updateIconWithState(state: State) {
        when (state) {
            State.BEFORE_RECORDING -> {
                setImageResource(R.drawable.ic_baseline_mic_24)
            }
            State.ON_RECORDING -> {
                setImageResource(R.drawable.ic_baseline_stop_circle_24)
            }
            State.AFTER_RECORDING -> {
                setImageResource(R.drawable.ic_baseline_play_circle_24)
            }
            State.ON_PLAYING -> {
                setImageResource(R.drawable.ic_baseline_stop_circle_24)
            }
            State.AFTER_PLAYING -> {
                setImageResource(R.drawable.ic_baseline_play_circle_24)
            }
        }
    }
}
