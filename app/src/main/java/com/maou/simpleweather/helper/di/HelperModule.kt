package com.maou.simpleweather.helper.di

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.maou.simpleweather.helper.GpsHelper
import com.maou.simpleweather.helper.PermissionHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val helperModule = module {
    singleOf(::PermissionHelper)
    singleOf(::GpsHelper)

    single {
        val request = LocationRequest.create().apply {
            interval = 1000
            priority = Priority.PRIORITY_HIGH_ACCURACY
            fastestInterval = 1000
        }

        request
    }
}