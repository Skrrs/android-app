package com.example.aop.part2.mask.utils.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClass {
    companion object{
        private val BASE_URL: String = "https://localhost:9090"
//        private val BASE_URL: String = "http://210.218.176.36:8000"
//        private val BASE_URL: String = "http://3.209.82.199:8080"
    }
    //    var api: API ? = null
    fun getRetrofitInstance(): Retrofit {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()!!
//        api = retrofit.create(API::class.java)
        return retrofit
    }
}