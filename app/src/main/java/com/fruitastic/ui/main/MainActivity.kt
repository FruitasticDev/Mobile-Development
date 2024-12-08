package com.fruitastic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.fruitastic.R
import com.fruitastic.data.pref.AppPreferences
import com.fruitastic.data.pref.ViewModelFactory
import com.fruitastic.databinding.ActivityMainBinding
import com.fruitastic.data.pref.dataStore
import com.fruitastic.ui.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels { ViewModelFactory(AppPreferences.getInstance((this).application.dataStore)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val isDarkModeActive = AppPreferences.getInstance(application.dataStore).getThemeSetting().first()

            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                Intent(this, WelcomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_history, R.id.navigation_setting
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateBottomNavIcons(destination.id)
        }
    }

    private fun updateBottomNavIcons(activeId: Int) {
        val navView: BottomNavigationView = binding.navView

        for (i in 0 until navView.menu.size()) {
            val menuItem = navView.menu.getItem(i)
            when (menuItem.itemId) {
                R.id.navigation_home -> menuItem.setIcon(
                    if (menuItem.itemId == activeId) R.drawable.ic_home_fill_24dp else R.drawable.ic_home_outline_24dp
                )
                R.id.navigation_history -> menuItem.setIcon(
                    if (menuItem.itemId == activeId) R.drawable.ic_history_fill_24dp else R.drawable.ic_history_outline_24dp
                )
                R.id.navigation_setting -> menuItem.setIcon(
                    if (menuItem.itemId == activeId) R.drawable.ic_setting_fill_24dp else R.drawable.ic_setting_outline_24dp
                )
            }
        }
    }
}