package com.example.mygymapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)                    // ① Super-Call
        setContentView(R.layout.activity_main)                // ② Layout setzen

        // ③ BottomNavigationView aus dem Layout laden
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        // ④ NavController über die NavHostFragment-ID holen
        val navController = findNavController(R.id.nav_host_fragment)

        // ⑤ BottomNavigation mit NavController verbinden
        navView.setupWithNavController(navController)


    }
}
