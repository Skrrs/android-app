package com.example.aop.part2.mask.presentation.login

//import androidx.appcompat.widget.AppCompatButton
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.data.User
import com.example.aop.part2.mask.domain.dto.LoginDto
import com.example.aop.part2.mask.domain.request.API
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.LoginResult
import com.example.aop.part2.mask.presentation.signup.SignupActivity
import com.example.aop.part2.mask.utils.api.RetrofitClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

//import com.facebook.AccessToken
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginResult
//import com.facebook.login.widget.LoginButton
//import com.google.firebase.auth.FacebookAuthProvider
//import com.google.android.gms.auth.api.signin.GoogleSignInClient

class LoginActivity : AppCompatActivity() {
    // DB
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = Firebase.database
//    private lateinit var userDB: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        else {
            Toast.makeText(this, "환영합니다.", Toast.LENGTH_SHORT).show()
        }

        // TODO - Login API request
        val email: String = auth.currentUser?.email.orEmpty()

        callLogin(email)

        finish()
    }

    // Retrofit (API)
    private var rtf : Retrofit? = null
//    private var tkList : List<token>? = null
    private fun callLogin(email: String) {
        val loginDto = LoginDto(email)

        rtf = RetrofitClass().getRetrofitInstance()
        val api = rtf?.create(API::class.java)
        val callAPI = api?.requestLogin(loginDto = loginDto)

        callAPI?.enqueue(object : retrofit2.Callback<CommonResponse<LoginResult>> {
            override fun onResponse(call: Call<CommonResponse<LoginResult>>, response: Response<CommonResponse<LoginResult>>) {
                if (response.isSuccessful) {
                    Log.d("login Success", response.code().toString())
                    var token = response.body()?.result?.token ?: String()
                    Log.d("MessageToken : ",token)
                    val tokenReference = database.getReference("token")
                    tokenReference.setValue(token)
//                    saveUserToken(token)
//                    var msg = response.body()?.message
                } else{
                    Log.d("login : Code 400 Error", response.toString())
                }
            }
            override fun onFailure(call: Call<CommonResponse<LoginResult>>, t: Throwable) {
                Log.d("Login : Code 500 Error", t.toString())
            }
        })
    }

//    private fun saveUserToken(token: String) {
//        val userId = getCurrentUserID()
//        val currentUserDB = userDB.child(userId)
//        val user = mutableMapOf<String, Any>()
//        user["userId"] = userId
//        user["token"] = token
//        currentUserDB.updateChildren(user)
//    }

    private fun getCurrentUserID(): String {
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }
}