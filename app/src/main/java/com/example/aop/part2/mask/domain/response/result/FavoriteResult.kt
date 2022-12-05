package com.example.aop.part2.mask.domain.response.result

data class FavoriteResult(
    val index : ArrayList<Int>,
    val sentence : ArrayList<String>,
    val voiceUrl : ArrayList<String>
)

data class FavoriteItem(
    val sentence : String,
    val voiceUrl: String,
    val index : Int,
    val email : String,
    val token : String
)
