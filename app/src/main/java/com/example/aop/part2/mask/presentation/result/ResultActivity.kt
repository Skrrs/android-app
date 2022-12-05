package com.example.aop.part2.mask.presentation.result

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.aop.part2.mask.domain.dto.FavoriteDto
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.example.aop.part2.mask.presentation.signup.SignupActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.util.ArrayList

class ResultActivity : AppCompatActivity(){
    private var email: String = ""
    private var token: String = ""
    private var problem: ArrayList<Int>? = arrayListOf()
    private var correct: ArrayList<Int>? = arrayListOf()
    private var wrong: ArrayList<Int>? = arrayListOf()
    private var index: ArrayList<Int>? = arrayListOf()
    private var sentence: ArrayList<String>? = arrayListOf()
    private var level: Int = 0
    //private lateinit var favoriteDto: FavoriteDto

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        toastMsg("수고하셨습니다.")
        initVariables()
        bindViews()
    }
    private fun bindViews() {
        layoutView(R.id.wrong1, R.id.wrong1_txt, R.id.wrong1_btn, 1, index?.get(0)!!, sentence?.get(0)!!, false)
        layoutView(R.id.wrong2, R.id.wrong2_txt, R.id.wrong2_btn, 2, index?.get(1)!!, sentence?.get(1)!!, false)
        layoutView(R.id.wrong3, R.id.wrong3_txt, R.id.wrong3_btn, 3, index?.get(2)!!, sentence?.get(2)!!, false)
        layoutView(R.id.wrong4, R.id.wrong4_txt, R.id.wrong4_btn, 4, index?.get(3)!!, sentence?.get(3)!!, false)
        layoutView(R.id.wrong5, R.id.wrong5_txt, R.id.wrong5_btn, 5, index?.get(4)!!, sentence?.get(4)!!, false)
//        layoutView(R.id.wrong6, R.id.wrong6_txt, R.id.wrong6_btn, 6, index?.get(5)!!, sentence?.get(5)!!, false)
//        layoutView(R.id.wrong7, R.id.wrong7_txt, R.id.wrong7_btn, 7, index?.get(6)!!, sentence?.get(6)!!, false)
//        layoutView(R.id.wrong8, R.id.wrong8_txt, R.id.wrong8_btn, 8, index?.get(7)!!, sentence?.get(7)!!, false)
//        layoutView(R.id.wrong9, R.id.wrong9_txt, R.id.wrong9_btn, 9, index?.get(8)!!, sentence?.get(8)!!, false)
//        layoutView(R.id.wrong10, R.id.wrong10_txt, R.id.wrong10_btn, 10, index?.get(9)!!, sentence?.get(9)!!, false)

        layoutView(R.id.correct1, R.id.correct1_txt, R.id.correct1_btn, 1, index?.get(0)!!, sentence?.get(0)!!, true)
        layoutView(R.id.correct2, R.id.correct2_txt, R.id.correct2_btn, 2, index?.get(1)!!, sentence?.get(1)!!, true)
        layoutView(R.id.correct3, R.id.correct3_txt, R.id.correct3_btn, 3, index?.get(2)!!, sentence?.get(2)!!, true)
        layoutView(R.id.correct4, R.id.correct4_txt, R.id.correct4_btn, 4, index?.get(3)!!, sentence?.get(3)!!, true)
        layoutView(R.id.correct5, R.id.correct5_txt, R.id.correct5_btn, 5, index?.get(4)!!, sentence?.get(4)!!, true)
//        layoutView(R.id.correct6, R.id.correct6_txt, R.id.correct6_btn, 6, index?.get(5)!!, sentence?.get(5)!!, true)
//        layoutView(R.id.correct7, R.id.correct7_txt, R.id.correct7_btn, 7, index?.get(6)!!, sentence?.get(6)!!, true)
//        layoutView(R.id.correct8, R.id.correct8_txt, R.id.correct8_btn, 8, index?.get(7)!!, sentence?.get(7)!!, true)
//        layoutView(R.id.correct9, R.id.correct9_txt, R.id.correct9_btn, 9, index?.get(8)!!, sentence?.get(8)!!, true)
//        layoutView(R.id.correct10, R.id.correct10_txt, R.id.correct10_btn, 10, index?.get(9)!!, sentence?.get(9)!!, true)

        val btnHome = findViewById<AppCompatButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            callAddFavorite(email, token)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun layoutView(layoutRid: Int, txtRid:Int, btnRid:Int, num : Int, currentIndex : Int, sentence : String, correct_or_wrong : Boolean): LinearLayout? {
        val layout = findViewById<LinearLayout>(layoutRid)
        var arr: ArrayList<Int>? = arrayListOf()
        if(correct_or_wrong){
            arr = correct
        }else{
            arr = wrong
        }
        val visible : Boolean = arr?.contains(currentIndex)!!
        if (!visible) {
            layout.visibility = View.GONE
        }
        else {
            layout.visibility = View.VISIBLE
            val txt: TextView = findViewById(txtRid)
            txt.text = "$num. $sentence"
            val btn = findViewById<AppCompatButton>(btnRid)
            var btnActivation : Boolean = problem?.contains(currentIndex)!!
            if (btnActivation) {
                btn.background = this.resources.getDrawable(R.drawable.btn_add_activate)
            } else {
                btn.background = this.resources.getDrawable(R.drawable.btn_add_deactivate)
            }
            btn.setOnClickListener {
                if (btnActivation) {
                    btnActivation = false
                    btn.background = this.resources.getDrawable(R.drawable.btn_add_deactivate)
                    if (problem?.contains(currentIndex)!!) {
                        problem!!.remove(currentIndex)
                    }
                }
                else {
                    btnActivation = true
                    btn.background = this.resources.getDrawable(R.drawable.btn_add_activate)
                    if (!problem?.contains(currentIndex)!!){
                        problem!!.add(currentIndex)
                    }
                }
            }
        }
        return layout
    }

    // TODO - 즐겨찾기 추가 기능
    private var rtf : Retrofit? = null
    private fun callAddFavorite(email:String, token:String) {
        rtf = RetrofitClass().getRetrofitInstance()
        val api = rtf?.create(API::class.java)
        var favoriteDto = FavoriteDto(problem!!, correct!!)
        val callAPI = api?.requestAddFavorite(email, level, token, favoriteDto)
        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<Any>> {
            override fun onResponse(call: Call<CommonResponse<Any>>, response: Response<CommonResponse<Any>>) {
                if (response.isSuccessful) {
                    Log.d("Result Add Favorite Success", response.code().toString())
                    toastMsg("My Library에 새로운 문장이 추가되었습니다.")
                    val msg = response.body()?.message
                    mainPageMove()
                }else{
                    toastMsg(response.toString())
                    Log.d("Result Add Favorite : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<Any>>, t: Throwable) {
                Log.d("Result Add Favorite : Code 500 Error", t.toString())
            }
        })
    }
    private fun initVariables() {
        if (intent.hasExtra("sentence")){
            sentence = intent.getStringArrayListExtra("sentence")
        }else{
//            sentence = arrayListOf(
//                "지금 몇 시예요?",
//                "세시 삼십 분이에요.",
//                "벌써 그렇게 됐어요?",
//                "서둘러야겠어요.",
//                "왜 그러세요?",
//                "네시에 친구와 약속이 있어요.",
//                "어디서 만나기로 하셨는데요?",
//                "여의도 케이비에스 본관 앞에서 만나기로 했어요.",
//                "지금 출발하면 늦지 않으실 거예요.",
//                "먼저 가서 미안해요. 그럼 내일 또 만나요."
//            )
        }
        if (intent.hasExtra("index")) {
                    index = intent.getIntegerArrayListExtra("index")
        }else{
//            index = arrayListOf(2, 1, 5, 4, 3, 10, 7, 8, 9, 6)
        }
        if (intent.hasExtra("token")) {
            token = intent.getStringExtra("token").toString()
        }
        if(intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }
        if (intent.hasExtra("correct")) {
            correct = intent.getIntegerArrayListExtra("correct")
        }
        if (intent.hasExtra("wrong")) {
            wrong = intent.getIntegerArrayListExtra("wrong")
        }
        if (intent.hasExtra("problem")) {
            problem = intent.getIntegerArrayListExtra("problem")
        }
        if (intent.hasExtra("level")) {
            level = intent.getIntExtra("level", 0)
        }
        for(i: Int in 1..5) {
            if (!correct?.contains(i)!! && !wrong?.contains(i)!!){
                wrong!!.add(i)
            }
        }
    }

    private fun mainPageMove() {
        val intent = Intent(this, MainActivity::class.java) //
//        intent.putExtra("token", token )
//        intent.putExtra("email", email)
//        intent.putExtra("problem", problem)
//        intent.putExtra("correct", correct)
        startActivity(intent)
        finish()
    }

    private fun toastMsg(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_LONG
        ).show()
    }
}