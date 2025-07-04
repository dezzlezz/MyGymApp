package com.example.mygymapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygymapp.databinding.ActivityMainBinding
import androidx.navigation.fragment.NavHostFragment       // ← Navigation-Host
import androidx.navigation.ui.setupWithNavController   // ← für setupWithNavController()
import com.google.android.material.bottomnavigation.BottomNavigationView  // ← BottomNavView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav)
            .setupWithNavController(navController)
    }
}
