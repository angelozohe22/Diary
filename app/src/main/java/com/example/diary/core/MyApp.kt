package com.example.diary.core

import android.app.Application
import android.content.Context

lateinit var ctx    : Context
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }
}