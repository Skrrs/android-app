package com.example.aop.part2.mask.presentation.mypage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.library.MylibraryActivity
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.google.firebase.auth.FirebaseAuth

class MypageActivity: AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val name = "김가면"
    private val email = "mask@sogang.ac.kr"
    private val attendance = 13

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val nameView: TextView = findViewById(R.id.mypage_name)
        val emailView: TextView = findViewById(R.id.mypage_email)
        val attendanceView: TextView = findViewById(R.id.mypage_attendance)

        nameView.setText(name)
        emailView.setText(email)
        attendanceView.setText("출석 : $attendance 일차")

        val MyLibrary = findViewById<AppCompatButton>(R.id.btnMylibrary)
        MyLibrary.setOnClickListener {
            val intent = Intent(this, MylibraryActivity::class.java) //
            startActivity(intent)
            finish()
        }

        val Logout = findViewById<AppCompatButton>(R.id.btnLogout)
        Logout.setOnClickListener {
            Toast.makeText(
                this,
                "로그아웃 합니다. 사용시 재로그인이 필요합니다.",
                Toast.LENGTH_LONG
            ).show()
            // TODO - currentUser nullize & erase cache ?
            val intent = Intent(this, LoginActivity::class.java) // BeginnerGo
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val Home = findViewById<AppCompatButton>(R.id.btnHome)
        Home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }
    }
}