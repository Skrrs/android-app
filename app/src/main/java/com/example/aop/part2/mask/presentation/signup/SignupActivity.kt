package com.example.aop.part2.mask.presentation.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.data.User
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignupActivity: AppCompatActivity()  {
    // DB
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
//    private lateinit var userDB: DatabaseReference
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java) //
            startActivity(intent)
            finish()
        }
        initSignUpButton()
        initEmailAndPasswordEditText()

    }

    private fun getCurrentUserID(): String{
        if (auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun initSignUpButton() {
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()
            Toast.makeText(
                this,
                "회원가입 중입니다... 잠시만 기다려 주세요 :)",
                Toast.LENGTH_SHORT
            ).show()

//            userDB = Firebase.database.reference.child("Users")
//            val currentUserDB = userDB.child(getCurrentUserID())
            val uid = auth.currentUser?.uid.orEmpty()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Firebase DB에 user 초기화 및 추가 (User class)
                        val user = User(email)
                        val emailReference = database.getReference("email")
                        emailReference.setValue(email)

                        Toast.makeText(
                            this,
                            "회원가입을 성공했습니다! 로그인 버튼을 눌러 로그인해주세요 :)",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다 :(", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }

    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }

    private fun initEmailAndPasswordEditText() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        emailEditText.addTextChangedListener {
            val enable = emailEditText.length() > 5 && passwordEditText.length() > 5
            loginButton.isEnabled = true
            signUpButton.isEnabled = enable
        }
        passwordEditText.addTextChangedListener {
            val enable = emailEditText.length() > 5 && passwordEditText.length() > 5
            loginButton.isEnabled = true
            signUpButton.isEnabled = enable
        }
    }
}