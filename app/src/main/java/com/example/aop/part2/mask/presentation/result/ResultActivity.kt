package com.example.aop.part2.mask.presentation.result

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.domain.dto.LoginDto
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.LoginResult
import com.example.aop.part2.mask.presentation.signup.SignupActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class ResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        Toast.makeText(
            this,
            "수고하셨습니다.",
            Toast.LENGTH_SHORT
        ).show()

        Toast.makeText(
            this,
            "수고하셨습니다.",
            Toast.LENGTH_SHORT
        ).show()

        val sentence1 = findViewById<TextView>(R.id.sentence1)
        val sentence2 = findViewById<TextView>(R.id.sentence2)
        val sentence3 = findViewById<TextView>(R.id.sentence3)
        val sentence4 = findViewById<TextView>(R.id.sentence4)
        val sentence5 = findViewById<TextView>(R.id.sentence5)
        val sentence6 = findViewById<TextView>(R.id.sentence6)
        val sentence7 = findViewById<TextView>(R.id.sentence7)
        val sentence8 = findViewById<TextView>(R.id.sentence8)
        val sentence9= findViewById<TextView>(R.id.sentence9)
        val sentence10 = findViewById<TextView>(R.id.sentence10)
    }

}