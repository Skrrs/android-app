package com.example.aop.part2.mask

import android.view.View
import com.example.aop.part2.mask.adapter.SentenceAdapter
import java.text.FieldPosition

interface OnDelClickListener {
    fun onDeleteClick(holder : SentenceAdapter.SentenceItemViewHolder, view: View, index: Int, position: Int)
}