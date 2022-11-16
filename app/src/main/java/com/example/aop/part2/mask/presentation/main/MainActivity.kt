package com.example.aop.part2.mask.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.presentation.mypage.MypageActivity
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.library.MylibraryActivity
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.example.aop.part2.mask.presentation.test.TestActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = findViewById<AppCompatButton>(R.id.header_user)
        val dictionary = findViewById<AppCompatButton>(R.id.header_dictionary)
        val beginnerGo = findViewById<AppCompatButton>(R.id.beginner_go)
        val intermediateGo = findViewById<AppCompatButton>(R.id.intermediate_go)
        val advancedGo = findViewById<AppCompatButton>(R.id.advanced_go)

        user.setOnClickListener{
            val intent = Intent(this, MypageActivity::class.java) //
            startActivity(intent)
        }
        dictionary.setOnClickListener{
            val intent = Intent(this, MylibraryActivity::class.java) //
            startActivity(intent)
        }

        beginnerGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // BeginnerGo
            startActivity(intent)
            finish()
        }
        intermediateGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }
        advancedGo.setOnClickListener{
            val intent = Intent(this, TestActivity::class.java) // AdvancedGo
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}