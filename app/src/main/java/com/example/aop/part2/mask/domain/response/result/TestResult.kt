package com.example.aop.part2.mask.domain.response.result

data class TestResult(
    val index : Array<Int>,
    val sentence : Array<String>,
    val voiceUrl : Array<String>,
    val pron : Array<String>,
    val english : Array<String>,
)
