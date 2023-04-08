package com.kevintresuelo.clinicus.components.ads

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.kevintresuelo.clinicus.utils.ext.findActivity

var mInterstitialAd: InterstitialAd? = null

private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
private const val TOOLS_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7668017069284131/7607764724"

fun loadInterstitial(context: Context) {
    InterstitialAd.load(
        context,
        TEST_AD_UNIT_ID, //Change this with your own AdUnitID!
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        }
    )
}

fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
    val activity = context.findActivity()

    if (mInterstitialAd != null && activity != null) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(e: AdError) {
                mInterstitialAd = null

                onAdDismissed()
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null

                loadInterstitial(context)
                onAdDismissed()
            }
        }
        mInterstitialAd?.show(activity)
    } else {
        onAdDismissed()
    }
}

fun removeInterstitial() {
    mInterstitialAd?.fullScreenContentCallback = null
    mInterstitialAd = null
}