package com.example.aop.part2.mask.presentation.library

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.aop.part2.mask.R
import com.example.aop.part2.mask.presentation.main.MainActivity

class MylibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylibrary)

        val Home = findViewById<AppCompatButton>(R.id.btnHome)
        Home.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // IntermediateGo
            startActivity(intent)
            finish()
        }

    }
}