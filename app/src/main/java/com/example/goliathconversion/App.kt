package com.example.goliathconversion

import android.app.Application
import com.example.goliathconversion.dependency.AppComponent
import com.example.goliathconversion.dependency.AppModule
import com.example.goliathconversion.dependency.DaggerAppComponent

class App : Application() {
    val appComponent: AppComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .build()
}
