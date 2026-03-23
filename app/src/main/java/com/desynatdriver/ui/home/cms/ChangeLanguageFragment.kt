package com.desynatdriver.ui.home.cms

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.databinding.FragmentChangeLanguageBinding
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeLanguageFragment : BaseFragment() {
    private var binding: FragmentChangeLanguageBinding?=null
    private var from =""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeLanguageBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        from = arguments?.getString("from").toString()
        requestPermissions()
        binding?.apply {
            Constants.language = SharedPreference.get(requireActivity()).language
            if(Constants.language=="en"){
                cbEnglish.isChecked = true
                cbArabic.isChecked = false
            }
            else if(Constants.language=="ar"){
                cbEnglish.isChecked = false
                cbArabic.isChecked = true
            }
        }
    }

    override fun initControl() {
        binding?.apply {

            tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            cbArabic.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbEnglish.isChecked = false
                    Constants.language = Constants.ARABIC
                }
                else {
                    if (!cbEnglish.isChecked) {
                        cbArabic.isChecked = true
                    }
                }
            }

            cbEnglish.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    cbArabic.isChecked = false
                    Constants.language = Constants.ENGLISH
                }
                else {
                    if (!cbArabic.isChecked) {
                        cbEnglish.isChecked = true
                    }
                }
            }

            btnSave.setOnClickListener {
                if (Constants.language==""){
                    showToast("Please select language")
                }
                else{
                    SharedPreference.get(requireActivity()).language = Constants.language
                    CommonMethod.showSuccessDialog(requireActivity(),"Language changed successfully"){
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun observe() {
    }

    private val requestAllPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val notificationGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions[Manifest.permission.POST_NOTIFICATIONS] ?: false
            } else {
                true
            }
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (notificationGranted && (fineLocationGranted || coarseLocationGranted)) {

            } else {

            }
        }

    private fun requestPermissions() {
        val permissionsList = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsList.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        requestAllPermissions.launch(permissionsList.toTypedArray())
    }

}