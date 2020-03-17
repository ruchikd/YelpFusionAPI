package com.example.weedmapschallenge.Utils

import android.util.Log
import com.github.kittinunf.fuel.android.BuildConfig
import com.github.kittinunf.fuel.core.FuelError

fun debugPrintStackTrace(fuelError: FuelError) {
    if (BuildConfig.DEBUG) {
        Log.e(AppData.TAG_ERROR,"=============== PrintStackTrace ===============")
        fuelError.printStackTrace()
    }
}