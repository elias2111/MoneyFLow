package me.elias.unabshop

import android.app.Application
import com.google.firebase.FirebaseApp

class AppInit : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase una sola vez al iniciar la app
        FirebaseApp.initializeApp(this)
    }
}
