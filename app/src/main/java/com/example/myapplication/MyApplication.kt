package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.repository.AppContainer
import com.example.myapplication.data.repository.EntriContainer

class MyApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()

        // PERBAIKAN: Tambahkan 'this' di dalam kurung
        container = EntriContainer(this)
    }
}