package com.ronaldo.pace.sample

import android.app.Application
import com.ronaldo.pace.feature.store.storeFeatureModule
import com.ronaldo.pace.network.networkModule
import com.ronaldo.pace.repository.pets.petsRepositoryModule
import com.ronaldo.pace.services.database.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(
                listOf(
                    // services
                    networkModule(),
                    databaseModule(this@App),

                    // repositories
                    petsRepositoryModule(),

                    // features
                    storeFeatureModule(),
                )
            )
        }
    }
}