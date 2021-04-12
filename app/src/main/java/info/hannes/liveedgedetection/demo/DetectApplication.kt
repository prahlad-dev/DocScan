package info.hannes.liveedgedetection.demo

import android.annotation.SuppressLint
import android.app.Application
import info.hannes.timber.FileLoggingTree
import timber.log.Timber

class DetectApplication : Application() {

    @SuppressLint("HardwareIds")
    override fun onCreate() {
        super.onCreate()

        val oldHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            @Suppress("ControlFlowWithEmptyBody")
            oldHandler?.uncaughtException(t, e)
        }
    }
}