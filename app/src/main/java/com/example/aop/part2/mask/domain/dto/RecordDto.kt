package com.example.aop.part2.mask.domain.dto

import okhttp3.MultipartBody

data class RecordDto(
    val record : MultipartBody.Part // 녹음 파일
)
