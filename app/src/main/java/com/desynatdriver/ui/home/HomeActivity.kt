package com.desynatdriver.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.desynatdriver.R
import com.desynatdriver.adapter.BottomNavAdapter
import com.desynatdriver.base.BaseActivity
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.data.view_model.LocationViewModel
import com.desynatdriver.databinding.ActivityHomeBinding
import com.desynatdriver.databinding.DrawerLayoutBinding
import com.desynatdriver.ui.auth.AuthActivity
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import me.leolin.shortcutbadger.ShortcutBadger
import java.io.IOException
import java.util.Locale
import kotlin.text.category

@AndroidEntryPoint
class HomeActivity : BaseActivity() {
    private var binding: ActivityHomeBinding? =null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private lateinit var controller: NavController
    var bottonNavItemList = ArrayList<BottomNavItem>()
    private var bottomAdapter : BottomNavAdapter? =null
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var longitude = 0.0
    var latitude = 0.0
    var locationGet: Boolean = false

    private lateinit var drawerBinding: DrawerLayoutBinding
    var isOpenDrawer = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Constants.location==""){
            getCurrentLocation()
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        controller = navHostFragment.navController
        val navGraph = controller.navInflater.inflate(R.navigation.home_navigation_graph)
        controller.graph = navGraph

        bottonNavItemList.clear()
        bottonNavItemList.add(BottomNavItem("Home",R.drawable.ic_home,R.drawable.ic_selected_home))
        bottonNavItemList.add(BottomNavItem("Deliveries",R.drawable.ic_deliveries,R.drawable.ic_selected_deliveries))
        bottonNavItemList.add(BottomNavItem("Profile",R.drawable.ic_profile,R.drawable.ic_selected_profile))

        setBottomAdapter()

        val bottomNavDestinations = setOf(
            R.id.home,
            R.id.deliveries,
            R.id.profile,
        )


        controller.addOnDestinationChangedListener { _, destination, _ ->

            if (destination.id in bottomNavDestinations) {
                when(destination.id){
                R.id.home -> {
                    bottomAdapter?.setSelectedItem(0)
                    binding?.rvBottomNav?.isVisible = true
                    binding?.viewBackground?.isVisible = true
                    enableDrawer(true)
                }

                    R.id.deliveries -> {
                        bottomAdapter?.setSelectedItem(1)
                        binding?.rvBottomNav?.isVisible = true
                        binding?.viewBackground?.isVisible = true
                        enableDrawer(false)
                    }

                    R.id.profile -> {
                        bottomAdapter?.setSelectedItem(2)
//                        binding?.rvBottomNav?.isVisible = true
//                        binding?.viewBackground?.isVisible = true
//                        enableDrawer(false)
                    }

                else -> {
                    enableDrawer(false)
                    binding?.rvBottomNav?.isVisible = false
                    binding?.viewBackground?.isVisible = false
                }
}
            } else {
                enableDrawer(false)
                binding?.rvBottomNav?.isVisible = false
                binding?.viewBackground?.isVisible = false
            }

        }

        binding?.apply {
            drawerBinding = DrawerLayoutBinding.bind(navigationView.getHeaderView(0))
            setUpDrawerContent()
        }
    }

    fun openDrawer(){
        isOpenDrawer = true
        binding?.drawerLayout?.openDrawer(GravityCompat.START)
    }

    private fun setUpDrawerContent() {
        drawerBinding.apply {

            tvTitle.setOnClickListener {
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

            relLogout.setOnClickListener {
                viewModel.logout()
                isObserver = true
                ProgressDialogUtils.getInstance().showProgress(this@HomeActivity, false)
            }

//            rlSetting.setOnClickListener {
//                controller.navigate(R.id.setting)
//                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
//            }

            rlSupport.setOnClickListener {
                controller.navigate(R.id.support)
                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
            }

//            rlFaq.setOnClickListener {
//                controller.navigate(R.id.faq)
//                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
//            }
//
//            rlLanguage.setOnClickListener {
//                controller.navigate(R.id.changeLanguageFragment)
//                binding?.drawerLayout?.closeDrawer(GravityCompat.START)
//            }
        }
    }

    private fun enableDrawer(enable: Boolean) {
        if (enable) {
            binding?.drawerLayout?.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_UNLOCKED)
            binding?.navigationView?.isVisible = true
        } else {
            binding?.drawerLayout?.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding?.navigationView?.isVisible = false
        }
    }

    override fun onBackPressed() {
        Log.e("ehdf", "onBackPressed: $isOpenDrawer   ${binding?.drawerLayout?.isDrawerOpen(
            GravityCompat.START)}")
        if (isOpenDrawer == true&&binding?.drawerLayout?.isDrawerOpen(GravityCompat.START)==false){
            binding?.drawerLayout?.openDrawer(GravityCompat.START,false)
            super.onBackPressed()
        }
        else if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) {
            isOpenDrawer = false
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun initControl() {
    }

    override fun observer() {

        viewModel.error.observe(this) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.logoutResult.observe(this) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                response?.let {
                    Constants.isLogout = true
                    CommonMethod.clearData(this)
                    startActivity(
                        Intent(this,
                            AuthActivity::class.java)
                    )
                    finish()
                }
                isObserver = false
            }
        }
    }

    fun setBottomAdapter(){
        bottomAdapter = BottomNavAdapter(this,bottonNavItemList, object :BottomNavAdapter.BottomNavItemClick{
            override fun onBottomNavItemClick(position: Int) {
                when (position) {
                    0 -> if (controller.currentDestination?.id != R.id.home)
                        controller.navigate(R.id.home)

                    1 -> if (controller.currentDestination?.id != R.id.deliveries)
                        controller.navigate(R.id.deliveries)

                    2 -> if (controller.currentDestination?.id != R.id.profile)
                        controller.navigate(R.id.profile)
                }
            }


        })
        binding?.rvBottomNav?.adapter = bottomAdapter

    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationViewModel.setLoading(true)

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    // Success
                    // handleLocation(location)
                    locationGet = true
                    latitude = location.latitude
                    longitude = location.longitude
                    updateLocationText(latitude,longitude)
                } else {
                    // Try last known location
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                        if (lastLocation != null) {
                            //handleLocation(lastLocation)
                            latitude = location?.latitude ?: 0.0
                            longitude = location?.longitude ?: 0.0

                            Log.e(
                                "LocationSuccess",
                                "Location is null from both current and last known.latitude:$latitude, longitude:$longitude"
                            )
                            updateLocationText(latitude,longitude)
                        } else {
                            locationViewModel.setLoading(false)
                            Log.e(
                                "LocationError",
                                "Location is null from both current and last known."
                            )
                            Toast.makeText(
                                this,
                                "Unable to fetch location. Try moving outside or checking settings.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
        }
    }

    fun updateLocationText(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val fullAddress = address.getAddressLine(0) ?: "Unknown Location"
                val locality = address.locality ?: "Unknown Locality"   // City or Town
                val subLocality = address.subLocality ?: "Unknown Sublocality" // Area inside city
                val stateh = address.adminArea ?: "N/A"
                val postalCode = address.postalCode ?: "Unknown Pincode" // Zip Code
                val houseNumber = address.subThoroughfare ?: address.premises ?: address.featureName
                locationViewModel.setLoading(false)
                Constants.location = fullAddress
                Constants.latitude = this.latitude
                Constants.longitude = this.longitude
                locationViewModel.setLocation(this.latitude, this.longitude)

            } else {
                Constants.location  = "No address found"
            }
        } catch (e: IOException) {
            Log.e("MapsDebug", "Geocoder failed: ${e.message}")
            Constants.location = "Error fetching address"
        }
    }

    fun hideBottomBar(){
        binding?.rvBottomNav?.isVisible = false
        binding?.viewBackground?.isVisible = false
        enableDrawer(false)
    }

    fun showBottomBar(){
        binding?.rvBottomNav?.isVisible = true
        binding?.viewBackground?.isVisible = true
        enableDrawer(false)
    }

    fun openDeliveriesTab(bundle: Bundle? = null) {
            bottomAdapter?.setSelectedItem(1)

            if (bundle != null) {
                controller.navigate(R.id.deliveries, bundle)
            } else {
                controller.navigate(R.id.deliveries)
            }
        }

    override fun onResume() {
        super.onResume()

//        SharedPreference.get(this).badgeCount = 0
//        ShortcutBadger.removeCount(this)
        //   NotificationManagerCompat.from(this).cancelAll()
    }

}

data class BottomNavItem(
    var name : String,
    var image: Int,
    var selectedImage: Int,
)