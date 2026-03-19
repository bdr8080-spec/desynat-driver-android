package com.desynatdriver

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import com.desynatdriver.utils.SharedPreference
import com.google.android.libraries.places.api.Places
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : Application(){
    companion object {
        var notificationManager: NotificationManager? = null
    }
override fun onCreate() {
    super.onCreate()
    Log.e("app", "APPLICATION CLASS RUN")
    if (!Places.isInitialized()) {
        Places.initialize(applicationContext, getString(R.string.map_key))
    }

    notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.e("FCM_ERROR", "Token failed", task.exception)
            return@addOnCompleteListener
        }

        val token = task.result
        Log.d("FCM_TOKEN", token)
        SharedPreference.get(this).deviceToken = token
    }
}
}