package com.danono.dodgedrive

import android.app.Application
import com.danono.dodgedrive.model.ScoreManager
import com.danono.dodgedrive.utilities.SignalManager


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SignalManager.init(this)
        ScoreManager.init(this)
    }
}
