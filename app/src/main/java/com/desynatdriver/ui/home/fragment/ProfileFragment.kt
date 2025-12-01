package com.desynatdriver.ui.home.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.databinding.FragmentProfileBinding
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {
    private var binding: FragmentProfileBinding?=null
    var profileInfo : GetProfileResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        profileInfo = SharedPreference.get(requireActivity()).getUser()
        binding?.apply {
            ivProfileImage.load(profileInfo?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvName.text = profileInfo?.fullNameEnglish
            tvEmail.text = profileInfo?.email
            tvAddress.text = "${profileInfo?.addressLine1} ${profileInfo?.addressLine2} ${profileInfo?.city} ${profileInfo?.state}"
            tvPhone.text = "${profileInfo?.countryCode} ${profileInfo?.mobile}"
            tvZone.text = profileInfo?.zoneInfo?.nameEnglish

            etVehicleName.setText(profileInfo?.vehicleName)
            etVehichleRegistrationNumber.setText(profileInfo?.vehicleRegNo)

            if (!profileInfo?.registrationCertificate.isNullOrEmpty()) {
                if(profileInfo?.registrationCertificate?.get(0)==null){
                    ivRegistration1.isVisible = false
                }
                if(profileInfo?.registrationCertificate?.get(1)==null){
                    ivRegistration2.isVisible = false
                }

                loadDocument(ivRegistration1, profileInfo?.registrationCertificate?.get(0)?.documentPath)
                loadDocument(ivRegistration2, profileInfo?.registrationCertificate?.get(1)?.documentPath)

            }
            else{
                tvRegistration.isVisible = false
                llRegistration.isVisible = false
            }

            if (!profileInfo?.pollutionPaper.isNullOrEmpty()){
                loadDocument(ivPollutionPaper, profileInfo?.pollutionPaper?.get(0)?.documentPath)
            }
            else{
                tvPollutionPaper.isVisible = false
                llPollutionPaper.isVisible = false
            }

            loadDocument(ivDriving1, profileInfo?.drivingLicense?.get(0)?.documentPath)
            loadDocument(ivDriving2, profileInfo?.drivingLicense?.get(1)?.documentPath)

            loadDocument(ivVehicle1, profileInfo?.vehicleImage?.get(0)?.documentPath)
            loadDocument(ivVehicle2, profileInfo?.vehicleImage?.get(1)?.documentPath)

            loadDocument(ivIdentity1, profileInfo?.identityProof?.get(0)?.documentPath)
            loadDocument(ivIdentity2, profileInfo?.identityProof?.get(1)?.documentPath)

            loadDocument(ivVehicleInsurance, profileInfo?.vehicleInsurance?.get(0)?.documentPath)

        }
    }

    override fun initControl() {
        binding?.apply {

            tvEdit.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
            }

            tvProfile.setOnClickListener {
                tvProfile.setTextColor(requireActivity().getColor(R.color.white))
                tvVehicleInfo.setTextColor(requireActivity().getColor(R.color.black))
                tvProfile.setBackgroundResource(R.drawable.selected_left_tab)
                tvVehicleInfo.background = null
                clDriverProfile.isVisible = true
                llVehicleInfo.isVisible = false
            }

            tvVehicleInfo.setOnClickListener {
                tvVehicleInfo.setTextColor(requireActivity().getColor(R.color.white))
                tvProfile.setTextColor(requireActivity().getColor(R.color.black))
                tvVehicleInfo.setBackgroundResource(R.drawable.selected_right_tab)
                tvProfile.background = null
                clDriverProfile.isVisible = false
                llVehicleInfo.isVisible = true
            }

            ivVehicle1.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.vehicleImage?.get(0)?.documentPath ?: "")
            }

            ivVehicle2.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.vehicleImage?.get(1)?.documentPath ?: "")
            }

            ivVehicleInsurance.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.vehicleInsurance?.get(0)?.documentPath ?: "")
            }

            ivPollutionPaper.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.pollutionPaper?.get(0)?.documentPath ?: "")
            }

            ivIdentity1.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.identityProof?.get(0)?.documentPath ?: "")
            }

            ivIdentity2.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.identityProof?.get(1)?.documentPath ?: "")
            }

            ivDriving1.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.drivingLicense?.get(0)?.documentPath ?: "")
            }

            ivDriving2.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.drivingLicense?.get(1)?.documentPath ?: "")
            }


            ivRegistration1.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.registrationCertificate?.get(0)?.documentPath ?: "")
            }

            ivRegistration2.setOnClickListener {
                openPdfInBrowser(requireContext(), profileInfo?.registrationCertificate?.get(1)?.documentPath ?: "")
            }

        }
    }

    override fun observe() {
    }

    fun loadDocument(imageView: ImageView, url: String?, placeHolder:Int = R.drawable.ic_placeholder) {
        if (url.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder)
            return
        }

        if (url.endsWith(".pdf", ignoreCase = true)) {
            imageView.setImageResource(R.drawable.pdf)
        } else {
            imageView.load(url) {
                crossfade(true)
                placeholder(placeHolder)
                error(placeHolder)
            }
        }
    }

    fun openPdfInBrowser(context: Context, pdfUrl: String) {
        if (pdfUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

}