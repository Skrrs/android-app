package com.example.aop.part2.mask.adapter

import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aop.part2.mask.OnDelClickListener
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.databinding.ItemSentenceBinding
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.FavoriteItem
import retrofit2.Retrofit
import com.example.aop.part2.mask.utils.api.RetrofitClass
import retrofit2.Call
import retrofit2.Response

class SentenceAdapter
    : ListAdapter<FavoriteItem, SentenceAdapter.SentenceItemViewHolder>(diffUtil),
    OnDelClickListener {
    lateinit var listener: OnDelClickListener
    inner class SentenceItemViewHolder(private val binding: ItemSentenceBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(sentenceModel : FavoriteItem) {
            binding.textSentence.text = sentenceModel.sentence
            val mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                setDataSource(sentenceModel.voiceUrl)
                prepare()
            }
            val playBtn = binding.playBtn
            playBtn.setOnClickListener {
                if(!mediaPlayer!!.isPlaying){
                    mediaPlayer!!.start()
                }
            }
            val delBtn = binding.delBtn
            delBtn.setOnClickListener{
                listener.onDeleteClick(this,binding.root,sentenceModel.index, adapterPosition)
            }
//            delBtn.setOnClickListener {
////                currentList.toMutableList().remove(sentenceModel)
////                notifyDataSetChanged()
//                //remove(sentenceModel)
////                this@SentenceAdapter.notifyItemRemoved()
//                callDeleteSentence(sentenceModel.index, sentenceModel.email, sentenceModel.token)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentenceItemViewHolder {
        return SentenceItemViewHolder(ItemSentenceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SentenceItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun setOnDelClickListener(listener: OnDelClickListener){
        this.listener = listener
    }

    override fun onDeleteClick(
        holder: SentenceItemViewHolder,
        view: View,
        index: Int,
        position: Int
    ) {
        listener.onDeleteClick(holder, view, index, position)
    }

//    private fun callDeleteSentence(index : Int, email : String, token : String){
//        val rtf = RetrofitClass().getRetrofitInstance()
//        val api = rtf?.create(API::class.java)
//        // TODO - FavoriteDto & ADD
//        val callAPI = api?.requestDeleteFavorite(email = email, index = index, token = token)
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<Any>> {
//            override fun onResponse(call: Call<CommonResponse<Any>>, response: Response<CommonResponse<Any>>) {
//                if (response.isSuccessful) {
//                    Log.d("Favorite Delete Success", response.code().toString())
//
//                } else{
//                    Log.d("Favorite Delete : Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<Any>>, t: Throwable) {
//                Log.d("Favorite Delete : Code 500 Error", t.toString())
//            }
//        })
//    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FavoriteItem>(){
            override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem.sentence == newItem.sentence
            }
        }
    }
}
