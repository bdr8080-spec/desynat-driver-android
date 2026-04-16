package com.desynatdriver.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatdriver.R
import com.desynatdriver.base.BaseActivity
import com.desynatdriver.databinding.ActivityAuthBinding
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var controller: NavController
    private var binding: ActivityAuthBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightNavigationBars = false

        initView()
        initControl()
        observer()
    }

    override fun initView() {
        CommonMethod.detectGestureMode(binding?.root!!,binding?.viewBottom!!)

       /* val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        controller.graph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)*/

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController

        val navGraph = controller.navInflater.inflate(R.navigation.auth_navigation_graph)


        navGraph.setStartDestination(
            if (Constants.isLogout) R.id.login
            else R.id.language
        )

        controller.graph = navGraph
    }

    override fun initControl() {
    }

    override fun observer() {
    }
}