package com.example.culinaryassistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.example.culinaryassistant.data.NetworkClient
import com.example.culinaryassistant.ui.CulinaryApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SingletonImageLoader.setSafe { context ->
            ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory(callFactory = { NetworkClient.httpClient }))
                }
                .build()
        }
        setContent { CulinaryApp() }
    }
}
