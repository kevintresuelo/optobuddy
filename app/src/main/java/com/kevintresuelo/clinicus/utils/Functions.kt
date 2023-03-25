package com.kevintresuelo.clinicus.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import org.json.JSONObject
import java.security.SecureRandom
import java.util.*

fun getDeviceDetails(): String {
    val map = mapOf(
        "brand" to Build.BRAND,
        "model" to Build.MODEL,
        "manufacturer" to Build.MANUFACTURER,
        "device" to Build.DEVICE,
        "os" to "Android ${Build.VERSION.RELEASE} (API Level ${Build.VERSION.SDK_INT})",
        "id" to Build.ID,
        "user" to Build.USER,
        "type" to Build.TYPE,
        "base" to Build.VERSION_CODES.BASE,
        "incremental" to Build.VERSION.INCREMENTAL,
        "board" to Build.BOARD,
        "host" to Build.HOST,
        "fingerprint" to Build.FINGERPRINT
    )

    return JSONObject(map).toString()
}


private fun capitalize(s: String?): String {
    if (s == null || s.isEmpty()) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        first.uppercaseChar().toString() + s.substring(1)
    }
}



fun autoId(): String {
    val autoIdLength = 20

    val autoIdAlphabet =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    val rand: Random = SecureRandom()

    val builder = StringBuilder()
    val maxRandom = autoIdAlphabet.length
    for (i in 0 until autoIdLength) {
        builder.append(autoIdAlphabet[rand.nextInt(maxRandom)])
    }
    return builder.toString()
}


fun log(message: String, throwable: Throwable? = null, tag: String = "LoremApp") {
    Log.d("LoremApp", message, throwable)
}


fun isOnline(context: Context): Boolean {
    val connMgr: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connMgr.getNetworkCapabilities(connMgr.activeNetwork)
    return if (networkCapabilities == null) {
        false
    } else {
        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
    }
}