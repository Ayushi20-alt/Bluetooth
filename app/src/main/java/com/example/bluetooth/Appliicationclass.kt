package com.example.bluetooth

import android.app.Application
import com.mazenrashed.printooth.Printooth

class Appliicationclass : Application() {

    override fun onCreate() {
        super.onCreate()
        Printooth.init(this)
    }
}