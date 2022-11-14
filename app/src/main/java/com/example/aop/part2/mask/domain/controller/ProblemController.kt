package com.example.aop.part2.mask.domain.controller

import com.example.aop.part2.mask.domain.request.RecordDto
import com.example.aop.part2.mask.domain.response.Problem
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ProblemController
{
    @Multipart
    @POST("/api/problem/grade")
    fun gradeProblem(
//        @Header("Authorization") token : String,
//        @Body recordDto : RecordDto
        @Part record : MultipartBody.Part
    ) : Call<Problem>
}