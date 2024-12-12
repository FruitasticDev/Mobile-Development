package com.fruitastic.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.fruitastic.utils.BaseActivity
import com.fruitastic.R
import com.fruitastic.databinding.ActivitySplashScreenBinding
import com.fruitastic.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashTimeOut: Long = 800

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logoGroup.postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }, splashTimeOut)
    }
}