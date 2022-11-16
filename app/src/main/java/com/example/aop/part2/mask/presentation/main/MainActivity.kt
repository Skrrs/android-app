package com.example.aop.part2.mask.presentation.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.presentation.mypage.MypageActivity
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.library.MylibraryActivity
import com.example.aop.part2.mask.presentation.login.LoginActivity
import com.example.aop.part2.mask.presentation.test.TestActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userDB = Firebase.database.reference.child("Users")
        val currentUserDB = userDB.child(getCurrentUserID())

        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("name").value == null) {
                    showNameInputPopup()
                    return
                }
//                getUnSelectedUsers()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

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

    private fun getCurrentUserID(): String{
        if (auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun showNameInputPopup() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
//            .setTitle(getString(com.google.firebase.database.R.string.write_name))
            .setView(editText)
            .setPositiveButton("저장") { _, _ ->
                if (editText.text.isEmpty()) {
                    showNameInputPopup()
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String) {
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userID"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)
//        getUnSelectedUsers()
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}