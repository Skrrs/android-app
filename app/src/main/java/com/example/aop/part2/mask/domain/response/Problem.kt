package com.example.aop.part2.mask.domain.response
import java.io.Serializable

data class Problem (
    val result: GradeResult,
    val message : String,
)
data class GradeResult(
    val index : String
) : Serializable
