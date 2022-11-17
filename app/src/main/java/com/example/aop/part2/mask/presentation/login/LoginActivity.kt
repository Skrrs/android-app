package com.example.aop.part2.mask.presentation.login

import android.os.Bundle
import android.widget.Button
//import androidx.appcompat.widget.AppCompatButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aop.part2.mask.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database
import android.content.Intent
import com.example.aop.part2.mask.presentation.main.MainActivity
import com.example.aop.part2.mask.presentation.signup.SignupActivity

//import com.facebook.AccessToken
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginResult
//import com.facebook.login.widget.LoginButton
//import com.google.firebase.auth.FacebookAuthProvider
//import com.google.android.gms.auth.api.signin.GoogleSignInClient

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
//    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        initLoginButton()

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java) //
            startActivity(intent)
            finish()
        }

        initEmailAndPasswordEditText()
    }

    private fun initLoginButton(){
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            Toast.makeText(
                this,
                "로그인 중입니다... 잠시만 기다려 주세요 :)",
                Toast.LENGTH_SHORT
            ).show()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
//                        finish()
                        successLogin()
                    } else {
                        Toast.makeText(
                            this,
                            "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요 :(",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

//    private fun initSignUpButton(){
//        val signUpButton = findViewById<Button>(R.id.signUpButton)
//        signUpButton.setOnClickListener {
//            val intent = Intent(this, SignupActivity::class.java)
//            startActivity(intent)
////            finish()
//        }
//    }

    private fun getInputEmail(): String {
        return findViewById<EditText>(R.id.emailEditText).text.toString()
    }
    private fun getInputPassword(): String {
        return findViewById<EditText>(R.id.passwordEditText).text.toString()
    }
    private fun initEmailAndPasswordEditText() {
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpButton = findViewById<Button>(R.id.signUpButton)

        signUpButton.isEnabled = true
        emailEditText.addTextChangedListener {
            val enable = emailEditText.length() > 5 && passwordEditText.length() > 5
            loginButton.isEnabled = enable
            signUpButton.isEnabled = true
        }
        passwordEditText.addTextChangedListener {
            val enable = emailEditText.length() > 5 && passwordEditText.length() > 5
            loginButton.isEnabled = enable
            signUpButton.isEnabled = true
        }
    }

    private fun successLogin() {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        val email: String = auth.currentUser?.uid.orEmpty()
        val currentUserDb = Firebase.database.reference.child("Users").child(email)
        val user = mutableMapOf<String, Any>()
        user["email"] = email
        currentUserDb.updateChildren(user)

        finish()
    }


//    ######## About Facebook #########
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Pass the activity result back to the Facebook SDK
////        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }
//
//    private fun handleFacebookAccessToken(token: AccessToken) {
//        val credential = FacebookAuthProvider.getCredential(token.token)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    successLogin()
//                } else {
//                    Toast.makeText(this, "페이스북 로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
}
