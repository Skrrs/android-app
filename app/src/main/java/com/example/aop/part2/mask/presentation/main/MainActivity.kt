package com.example.aop.part2.mask.presentation.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.domain.dto.LoginDto
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.LevelAchievementResult
import com.example.aop.part2.mask.presentation.library.MylibraryActivity
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.example.aop.part2.mask.presentation.mypage.MypageActivity
import com.example.aop.part2.mask.presentation.result.ResultActivity
import com.example.aop.part2.mask.presentation.test.TestActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
//PieChart
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
//Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//retrofit
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    var beginner = 73; var intermediate  = 49; var advanced = 9
    var msg:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = findViewById<AppCompatButton>(R.id.header_user)
        val dictionary = findViewById<AppCompatButton>(R.id.header_dictionary)
        val beginnerGo = findViewById<AppCompatButton>(R.id.beginner_go)
        val intermediateGo = findViewById<AppCompatButton>(R.id.intermediate_go)
        val advancedGo = findViewById<AppCompatButton>(R.id.advanced_go)

        // TODO - Piechart (beginner, intermediate, advanced)
        showPieChart(beginner, intermediate, advanced)

        user.setOnClickListener{
            val intent = Intent(this, MypageActivity::class.java) //
            startActivity(intent)
        }
        dictionary.setOnClickListener{
//            val intent = Intent(this, MylibraryActivity::class.java) //
            val intent = Intent(this, ResultActivity::class.java) //
            startActivity(intent)
        }
        beginnerGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // BeginnerGo
//            intent.putExtra("level", "beginner")
            startActivity(intent)
            finish()
        }
        intermediateGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // IntermediateGo
//            intent.putExtra("level", "intermediate")
            startActivity(intent)
            finish()
        }
        advancedGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // AdvancedGo
//            intent.putExtra("level", "advanced")
            startActivity(intent)
            finish()
        }
    }

    // TODO - callMain (request)

    // Retrofit (API)
    private var rtf : Retrofit? = null
    //    private var tkList : List<token>? = null
    private fun callLevelAchievement(token: String, email:String){
        rtf = RetrofitClass().getRetrofitInstance()
        val api = rtf?.create(API::class.java)
        val strToken = "Bearer ${token}"

        // TODO - 주석해제
//        val callAPI = api?.requestLevelAchievement(email = email, token = strToken)
//        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<LevelAchievementResult>> {
//            override fun onResponse(call: Call<CommonResponse<LevelAchievementResult>>, response: Response<CommonResponse<LevelAchievementResult>>) {
//                if (response.isSuccessful) {
//                    Log.d("Main Success", response.code().toString())
//                    beginner = response.body()?.result?.beginner!!
//                    intermediate = response.body()?.result?.beginner!!
//                    advanced = response.body()?.result?.beginner!!
//                    msg = response.body()?.message.toString()
//                } else{
//                    Log.d(" Main Request: Code 400 Error", response.toString())
//                }
//            }
//            override fun onFailure(call: Call<CommonResponse<LevelAchievementResult>>, t: Throwable) {
//                Log.d("Main Request : Code 500 Error", t.toString())
//            }
//        })
    }

    private var beginnerChart : PieChart? = null
    private var intermediateChart : PieChart? = null
    private var advancedChart : PieChart? = null;
    private fun showPieChart(beginner: Int, intermediate: Int, advanced: Int){
        beginnerChart = findViewById<PieChart>(R.id.beginnerChart)
        beginnerChart?.apply {
            this.isRotationEnabled = true
            this.holeRadius = 70f
            this.setTransparentCircleAlpha(0)
            this.centerText = "$beginner%"
            this.setCenterTextSize(15f)
            setCenterTextColor(Color.BLACK)
            this.setDrawEntryLabels(false)
            this.description.isEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val yValuesBeginner:ArrayList<PieEntry> = ArrayList()
        yValuesBeginner.add(PieEntry(beginner.toFloat(), ""))
        yValuesBeginner.add(PieEntry((100-beginner).toFloat(), ""))
        val dataSetBeginner: PieDataSet = PieDataSet(yValuesBeginner, "")
        dataSetBeginner.sliceSpace = 2f
        dataSetBeginner.valueTextSize = 0f
        dataSetBeginner.setColors(Color.GREEN, Color.DKGRAY)
        var beginnerLegend = beginnerChart?.legend
        beginnerLegend?.isEnabled = false
        var beginnerPieData = PieData(dataSetBeginner)
        beginnerChart?.data = beginnerPieData
        beginnerChart?.invalidate()

        intermediateChart = findViewById<PieChart>(R.id.intermediateChart)
        intermediateChart?.apply {
            this.isRotationEnabled = true
            this.holeRadius = 70f
            this.setTransparentCircleAlpha(0)
            this.centerText = "$intermediate%"
            this.setCenterTextSize(15f)
            setCenterTextColor(Color.BLACK)
            this.setDrawEntryLabels(false)
            this.description.isEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val yValuesIntermediate:ArrayList<PieEntry> = ArrayList()
        yValuesIntermediate.add(PieEntry(intermediate.toFloat(), ""))
        yValuesIntermediate.add(PieEntry((100-intermediate).toFloat(), ""))

        val dataSetIntermediate: PieDataSet = PieDataSet(yValuesIntermediate, "")
        dataSetIntermediate.sliceSpace = 2f
        dataSetIntermediate.valueTextSize = 0f
        dataSetIntermediate.setColors(Color.GREEN, Color.DKGRAY)

        var intermediateLegend = intermediateChart?.legend
        intermediateLegend?.isEnabled = false

        var intermediatePieData = PieData(dataSetIntermediate)
        intermediateChart?.data = intermediatePieData
        intermediateChart?.invalidate()

        advancedChart = findViewById<PieChart>(R.id.advancedChart)
        advancedChart?.apply {
            this.isRotationEnabled = true
            this.holeRadius = 70f
            this.setTransparentCircleAlpha(0)
            this.centerText = "$advanced%"
            this.setCenterTextSize(15f)
            setCenterTextColor(Color.BLACK)
            this.setDrawEntryLabels(false)
            this.description.isEnabled = false
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val yValuesAdvanced:ArrayList<PieEntry> = ArrayList()
        yValuesAdvanced.add(PieEntry(advanced.toFloat(), ""))
        yValuesAdvanced.add(PieEntry((100-advanced).toFloat(), ""))
        val dataSetAdvanced: PieDataSet = PieDataSet(yValuesAdvanced, "")
        dataSetAdvanced.sliceSpace = 2f
        dataSetAdvanced.valueTextSize = 0f
        dataSetAdvanced.setColors(Color.GREEN, Color.DKGRAY)

        var advancedLegend = advancedChart?.legend
        advancedLegend?.isEnabled = false

        var advancedPieData = PieData(dataSetAdvanced)
        advancedChart?.data = advancedPieData
        advancedChart?.invalidate()
    }

    private fun getCurrentUserID(): String{
        if (auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
    private fun getCurrentUserEmail(): String{
        if (auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.email.orEmpty()
    }
    private fun toastErrorMsg(){
        Toast.makeText(
            this,
            "Error!",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        else{
            // TODO - Main 요청
            val email: String = auth.currentUser?.email.orEmpty()
            val token: String = ""
            databaseReference.child("token").get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                callLevelAchievement(it.value as String, email)
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
}