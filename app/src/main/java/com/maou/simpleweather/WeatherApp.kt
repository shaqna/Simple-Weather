package com.maou.simpleweather

import android.app.Application
import com.maou.simpleweather.data.di.dataModule
import com.maou.simpleweather.helper.di.helperModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class WeatherApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WeatherApp)
            androidLogger(Level.ERROR)
            loadKoinModules(
                listOf(
                    dataModule,
                    helperModule
                )
            )
        }
    }
}