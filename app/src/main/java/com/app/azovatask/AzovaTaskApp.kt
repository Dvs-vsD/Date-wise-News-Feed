package com.app.azovatask

import android.app.Application
import com.zhuinden.monarchy.Monarchy
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class AzovaTaskApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)


        val config = RealmConfiguration.Builder()
            .allowQueriesOnUiThread(true)
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()

        Realm.setDefaultConfiguration(config)

       // Monarchy.init(this); // need to call this only once

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}