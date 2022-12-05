package com.example.aop.part2.mask.presentation.mypage

import android.annotation.SuppressLint
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
    private var name : String = "김가면"
    private var email : String = "mask@sogang.ac.kr"
    private var token : String = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXNrQHNvZ2FuZy5hYy5rciIsImV4cCI6MTY3Nzc1OTUxMX0.qtE8MVzm6LBBOp7QDco6LYgCTKX436NEhqfdlvNBRmDikuKr92TrgS_FD8Uui4hMYQtXa_qOQDtureeD_K-afA"
    private var attendance : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        initVariables()
        bindViews()
    }

    private fun initVariables(){
        if(intent.hasExtra("name")) {
            name = intent.getStringExtra("name").toString()
        }
        if(intent.hasExtra("email")) {
            email = intent.getStringExtra("email").toString()
        }
        if(intent.hasExtra("attendance")) {
            attendance = intent.getIntExtra("attendance", 0)
        }
        if(intent.hasExtra("token")) {
            token = intent.getStringExtra("token").toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindViews(){
//        val nameView: TextView = findViewById(R.id.mypage_name)
        val emailView: TextView = findViewById(R.id.mypage_email)
//        val attendanceView: TextView = findViewById(R.id.mypage_attendance)

//        nameView.setText(name)
        emailView.setText(email)
//        attendanceView.setText("출석 : $attendance 일차")

        val MyLibrary = findViewById<AppCompatButton>(R.id.btnMylibrary)
        MyLibrary.setOnClickListener {
            val intent = Intent(this, MylibraryActivity::class.java) //
            intent.putExtra("token", token )
            intent.putExtra("email", email)
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