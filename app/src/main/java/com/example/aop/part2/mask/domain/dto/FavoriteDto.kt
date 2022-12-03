package com.example.aop.part2.mask.domain.dto

import java.io.Serializable

data class FavoriteDto(
    val problem : ArrayList<Int>,
    val corrected : ArrayList<Int>
)
//data class LibraryResult(
//    val index : Int,
//    val sentence: String
//) : Serializable
