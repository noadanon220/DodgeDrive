package com.danono.carrace

import android.app.Application
import com.danono.carrace.utilities.SignalManager


class App: Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
    }
}