package com.example.goliathconversion.dependency

import com.example.goliathconversion.ui.activities.MainActivity
import com.example.goliathconversion.ui.activities.TransactionDetailsActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: TransactionDetailsActivity)
}
