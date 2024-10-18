package com.timife.cloudiapp

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CloudiApp: Application() {

    override fun onCreate() {
        super.onCreate()
        val config = hashMapOf<String, String>()
        config["cloud_name"] = BuildConfig.CLOUD_NAME
        config["api_key"] = BuildConfig.API_KEY
        config["api_secret"] = BuildConfig.API_SECRET
        MediaManager.init(this, config)
    }
}