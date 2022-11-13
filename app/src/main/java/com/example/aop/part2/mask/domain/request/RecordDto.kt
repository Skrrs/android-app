package com.example.aop.part2.mask.domain.request

import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File

data class RecordDto(
    val record : MultipartBody.Part // 녹음 파일
)
