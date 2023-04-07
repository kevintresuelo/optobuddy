package com.kevintresuelo.clinicus.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import org.json.JSONObject
import java.security.SecureRandom
import java.text.DecimalFormat
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

fun getValidatedPower(text: String): String {
    val filteredChars = text.filterIndexed { index, char ->
        char in "0123456789" ||                                        // Take all digits
                (char == '.' && text.indexOf('.') == index) ||    // Take only the first decimal
                (char == '-' && text.indexOf('-') == index && text.indexOf('-') == 0)       // Take only the first negation
    }
    // Now we need to remove extra digits from the input
    return if(filteredChars.contains('.')) {
        val beforeDecimal = filteredChars.substringBefore('.')
        val afterDecimal = filteredChars.substringAfter('.')
        beforeDecimal + "." + afterDecimal.take(2)    // If decimal is present, take first 3 digits before decimal and first 2 digits after decimal
    } else {
        filteredChars                     // If there is no decimal, just take the first 3 digits
    }
}

fun getValidatedAxis(text: String): String {
    val filteredChars = text.filterIndexed { index, c ->
        c in "0123456789"   // Take all digits
    }
    return if (filteredChars.isNotEmpty()) { filteredChars.toInt().coerceIn(1, 180).toString() } else { filteredChars }
}

fun getValidatedAge(text: String): String {
    val filteredChars = text.filterIndexed { index, c ->
        c in "0123456789"   // Take all digits
    }
    return if (filteredChars.isNotEmpty()) { filteredChars.toInt().coerceIn(0, 100).toString() } else { filteredChars }
}

fun argbToHex(argb: Int): String {
    return Integer.toHexString(argb)
}

fun String.formatDiopter(round: Boolean = false, withSign: Boolean = false): String {
    return this.toFloat().formatDiopter(round, withSign)
}

fun Double.formatDiopter(round: Boolean = false, withSign: Boolean = false): String {
    val sign = if (this > 0 && withSign) "+" else ""
    val dioptricPower = if (round) 0.25 * (Math.round(this / 0.25)) else this

    return sign + String.format("%.2f", dioptricPower)
}

fun Float.formatDiopter(round: Boolean = false, withSign: Boolean = false): String {
    val sign = if (this > 0 && withSign) "+" else ""
    val dioptricPower = if (round) 0.25 * (Math.round(this / 0.25)) else this

    return sign + String.format("%.2f", dioptricPower)
}

fun Any.formatDecimal(): String {
    val df = DecimalFormat("#.#####")

    return df.format(this)
}