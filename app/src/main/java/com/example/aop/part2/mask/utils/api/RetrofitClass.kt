package com.example.aop.part2.mask.utils.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClass {
    companion object{
        private val BASE_URL: String = "http://18.178.180.144"
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