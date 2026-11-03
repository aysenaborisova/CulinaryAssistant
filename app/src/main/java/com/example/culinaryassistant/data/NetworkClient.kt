package com.example.culinaryassistant.data

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object NetworkClient {
    val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
}
