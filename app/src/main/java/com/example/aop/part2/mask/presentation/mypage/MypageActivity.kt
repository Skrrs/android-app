package com.example.aop.part2.mask.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.example.aop.part2.mask.presentation.main.MainActivity

class MypageActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val MyLibrary = findViewById<AppCompatButton>(R.id.btnMylibrary)
        val Logout = findViewById<AppCompatButton>(R.id.btnLogout)
        val Home = findViewById<AppCompatButton>(R.id.btnHome)

        MyLibrary.setOnClickListener {
            val intent = Intent(this, MypageActivity::class.java) //
            startActivity(intent)
            finish()
        }
        Logout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) // BeginnerGo
            startActivity(intent)
            finish()
        }
        Home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }
    }
}