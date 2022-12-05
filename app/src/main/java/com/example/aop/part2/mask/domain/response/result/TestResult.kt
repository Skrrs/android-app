package com.example.aop.part2.mask.domain.response.result

data class TestResult(
    val index : ArrayList<Int>,
    val sentence : ArrayList<String>,
    val voiceUrl : ArrayList<String>,
    val pron : ArrayList<String>,
    val english : ArrayList<String>,
)
