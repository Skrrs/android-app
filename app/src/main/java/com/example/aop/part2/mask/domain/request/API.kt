package com.example.aop.part2.mask.domain.request

import com.example.aop.part2.mask.domain.dto.FavoriteDto
import com.example.aop.part2.mask.domain.dto.LoginDto
import com.example.aop.part2.mask.domain.dto.LogoutDto
import com.example.aop.part2.mask.domain.dto.GradeDto
import com.example.aop.part2.mask.domain.response.CommonResponse
import com.example.aop.part2.mask.domain.response.result.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface API {
    // #### LOGIN ####
    // 로그인 요청
    @POST("/api/login")
    fun requestLogin(
        @Body loginDto: LoginDto
    ) : Call<CommonResponse<LoginResult>>

    // #### MAIN ####
    // 난이도 별 달성도 정보 요청
    @GET("/api/user/{email}")
    fun requestLevelAchievement (
        @Path("email") email: String,
        @Header("Authorization") token : String
        // ##################
        // TODO main page 하단
        // ##################
    ) : Call<CommonResponse<LevelAchievementResult>>

    // #### RESULT ####
    // 즐겨찾기 추가 요청
    @POST("/api/user/{email}/fav/{level}")
    fun requestAddFavorite(
        @Path("email") email: String,
        @Path("level") level: Int,
        @Header("Authorization") token : String,
        @Body favoriteDto: FavoriteDto
    ) : Call<CommonResponse<Any>>

    // #### MY LIBRARY ####
    // 즐겨찾기 페이지(정보) 요청
    @GET("/api/user/{email}/fav")
    fun requestFavorite(
        @Path("email") email: String,
        @Header("Authorization") token : String
    ) : Call<CommonResponse<FavoriteResult>>
    // 즐겨찾기 제거 요청
    @DELETE("/api/user/{email}/fav/{index}")
    fun requestDeleteFavorite(
        @Path("email") email: String,
        @Path("index") index: Int,
        @Header("Authorization") token : String
    ) : Call<CommonResponse<Any>>

    // #### MY PAGE ####
    // 로그아웃 요청
    @POST("/api/logout")
    fun requestLogout(
        @Header("Authorization") token : String,
        @Body logoutDto: LogoutDto,
    ) : Call<CommonResponse<Any>>

    // #### TEST ####
    // 테스트 페이지(정보) 요청
    @GET("/api/user/{email}/test/{level}")
    fun requestTest(
        @Path("email") email: String,
        @Path("level") level: Int,
        @Header("Authorization") token: String
    ) : Call<CommonResponse<TestResult>>
    // 채점 요청
    @Multipart
    @POST("/api/problem/grade")
    fun requestGrade(
        @Header("Authorization") token : String,
        @Part record : MultipartBody.Part,
        @Part ("answer") answer : String
//        @Part ("answer") answer : GradeDto
    ) : Call<CommonResponse<GradeResult>>
}