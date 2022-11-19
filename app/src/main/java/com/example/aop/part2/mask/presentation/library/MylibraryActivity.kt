package com.example.aop.part2.mask.presentation.library

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.main.MainActivity
import retrofit2.Retrofit

class MylibraryActivity : AppCompatActivity() {
    private var rtf : Retrofit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylibrary)

        val homeBtn = findViewById<AppCompatButton>(R.id.btnHome)
        homeBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }

        val delBtn = findViewById<AppCompatButton>(R.id.btnDel)
        delBtn.setOnClickListener {
//            callDeleteSentence()
            Toast.makeText(
                this,
                "문장이 삭제되었습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
//    private fun callDeleteSentence(){
//        val api = rtf?.create(API::class.java)
//        val path = api?.requestDeleteFavorite()
//        val callAPI = api?.requestDeleteFavorite(email =, token =, favoriteDto = )
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<Any>> {
//            override fun onResponse(call: Call<CommonResponse<GradeResult>>, response: Response<CommonResponse<GradeResult>>) {
//                if (response.isSuccessful) {
//                    Log.d("GradeProblem Success", response.code().toString())
//                    when(response.body()?.result){
////                        1 -> perfect 메시지 출력하기
////                        2 -> great 메시지 출력하기
////                        3 -> good 메시지 출력하기
////                        4 -> bad 메시지 출력하기
//                    }
//                } else{
//                    Log.d("GradeProblem : Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<GradeResult>>, t: Throwable) {
//                Log.d("GradeProblem : Code 500 Error", t.toString())
//            }
//        })
//    }
}