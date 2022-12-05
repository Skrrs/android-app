package com.example.aop.part2.mask.presentation.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aop.part2.mask.OnDelClickListener
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.adapter.SentenceAdapter
import com.example.aop.part2.mask.databinding.ActivityMylibraryBinding
import com.example.aop.part2.mask.domain.dto.FavoriteDto
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.FavoriteItem
import com.example.aop.part2.mask.domain.response.result.FavoriteResult
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.ArrayList

class MylibraryActivity : AppCompatActivity() {
    private var rtf: Retrofit? = null
    private var email: String = ""
    private var token: String = ""

    private var problem: ArrayList<Int>? = arrayListOf()
    private var sentence: ArrayList<String>? = arrayListOf()
    private var voiceUrl: ArrayList<String>? = arrayListOf()
    private var size: Int = 0

    private lateinit var binding: ActivityMylibraryBinding
    lateinit var adapter: SentenceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMylibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariables()
        initSentenceRecyclerView()
        bindViews()
    }

    fun initSentenceRecyclerView(){
        adapter = SentenceAdapter()
        binding.sentenceRecyclerView.layoutManager = LinearLayoutManager(this )
        binding.sentenceRecyclerView.adapter = adapter
    }

    private fun initVariables() {
        if (intent.hasExtra("token")) {
            token = intent.getStringExtra("token").toString()
        }
        if (intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }
        callFavorite()
    }

    private fun bindViews() {
        val homeBtn = findViewById<AppCompatButton>(R.id.btnHome)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }
        adapter.setOnDelClickListener(object : OnDelClickListener{
            override fun onDeleteClick(
                holder: SentenceAdapter.SentenceItemViewHolder,
                view: View,
                index: Int,
                position: Int
            ) {
                println(position)
                adapter.notifyItemRemoved(position)
                adapter.notifyDataSetChanged()
                callDeleteSentence(index)
            }
        })

    }
    private fun callDeleteSentence(index : Int){
        val rtf = RetrofitClass().getRetrofitInstance()
        val api = rtf?.create(API::class.java)
        // TODO - FavoriteDto & ADD
        val callAPI = api?.requestDeleteFavorite(email = email, index = index, token = token)
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<Any>> {
            override fun onResponse(call: Call<CommonResponse<Any>>, response: Response<CommonResponse<Any>>) {
                if (response.isSuccessful) {
                    Log.d("Favorite Delete Success", response.code().toString())

                } else{
                    toastMsg(response.toString())
                    Log.d("Favorite Delete : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<Any>>, t: Throwable) {
                Log.d("Favorite Delete : Code 500 Error", t.toString())
            }
        })
    }
    private fun callFavorite() {
        toastMsg("나만의 노트를 불러오고 있습니다 ...")
        rtf = RetrofitClass().getRetrofitInstance()
        val api = rtf?.create(API::class.java)
        val callAPI = api?.requestFavorite(email = email, token = token)
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<FavoriteResult>> {
            override fun onResponse(
                call: Call<CommonResponse<FavoriteResult>>,
                response: Response<CommonResponse<FavoriteResult>>
            ) {
                if (response.isSuccessful) {
                    Log.d("Get Favorites Success", response.code().toString())
                    problem = response.body()?.result?.index
                    sentence = response.body()?.result?.sentence
                    voiceUrl = response.body()?.result?.voiceUrl
                    size = problem?.size!!
                    val favoriteItems : ArrayList<FavoriteItem>? = arrayListOf()
                    for(i in 0 until size) {
                        favoriteItems?.add(FavoriteItem(
                            sentence = sentence?.get(i)!! ,
                            voiceUrl = voiceUrl?.get(i)!!,
                            index = problem?.get(i)!!,
                            email = email,
                            token = token))
                    }
                    adapter.submitList(favoriteItems)
//                    val msg = response.body()?.message
                } else {
                    toastMsg(response.toString())
                    Log.d("Get Favorites : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<FavoriteResult>>, t: Throwable) {
                Log.d("Get Favorites : Code 500 Error", t.toString())
            }
        })
    }

//    private fun callDeleteSentence(index : Int){
//        rtf = RetrofitClass().getRetrofitInstance()
//        val api = rtf?.create(API::class.java)
//        // TODO - FavoriteDto & ADD
//        val callAPI = api?.requestDeleteFavorite(email = email, index = index, token = token)
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<Any>> {
//            override fun onResponse(call: Call<CommonResponse<Any>>, response: Response<CommonResponse<Any>>) {
//                if (response.isSuccessful) {
//                    Log.d("Favorite Delete Success", response.code().toString())
//                } else{
//                    Log.d("Favorite Delete : Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<Any>>, t: Throwable) {
//                Log.d("Favorite Delete : Code 500 Error", t.toString())
//            }
//        })
//    }

    private fun toastMsg(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_LONG
        ).show()
    }
}

