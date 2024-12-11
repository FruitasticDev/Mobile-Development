package com.fruitastic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.fruitastic.BaseActivity
import com.fruitastic.R
import com.fruitastic.data.ViewModelFactory
import com.fruitastic.databinding.ActivityMainBinding
import com.fruitastic.ui.welcome.WelcomeActivity

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                Intent(this, WelcomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

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