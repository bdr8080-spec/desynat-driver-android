package com.desynatdriver.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.desynatdriver.R
import com.desynatdriver.base.BaseActivity
import com.desynatdriver.databinding.ActivitySplashBinding
import com.desynatdriver.localization_file.LocaleHelper
import com.desynatdriver.ui.auth.AuthActivity
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.utils.Constants
import com.desynatdriver.utils.SharedPreference

class SplashActivity : BaseActivity() {
    private var binding: ActivitySplashBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        initView()
        initControl()
        observer()
    }

    override fun initView() {
        Constants.language = SharedPreference.get(this).language
        LocaleHelper.setLocale(
            this,
            Constants.language
        )
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreference.get(this).accessToken.isNotEmpty()) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else{
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }

        },500)
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}