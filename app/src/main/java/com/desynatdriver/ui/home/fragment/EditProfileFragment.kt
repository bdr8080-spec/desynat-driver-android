package com.desynatdriver.ui.home.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentEditProfileBinding
import com.desynatdriver.ui.UpdateLocationActivity
import com.desynatdriver.ui.home.response.GetKuwaitZoneResponse
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ImagePicker
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class EditProfileFragment : BaseFragment() {
    private var binding: FragmentEditProfileBinding?=null
    var profileInfo : GetProfileResponse.Data?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var lat:Double=0.0
    var lng:Double=0.0
    var address:String=""
    var zoneId:String?=null
    private var imageUrl: String = ""
    private var isImageObserver = true
    private lateinit var imagePicker: ImagePicker
    private var kuwaitZoneList = ArrayList<GetKuwaitZoneResponse.Data>()

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            imagePicker.onCameraResult(success)
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imagePicker.onGalleryResult(uri)
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            imagePicker.onPermissionResult(granted)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater,container,false)
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
            toolbar.tvToolbar.text = "Edit"
            setData()

            CommonMethod.removeSpace(etFirstName)
            CommonMethod.removeSpace(etLastName)
            CommonMethod.removeAllTextSpaces(etEmail)
            CommonMethod.removeSpace(etAddressLine2)
            CommonMethod.removeSpace(etCity)
            CommonMethod.removeSpace(etState)

            imagePicker = ImagePicker(
                requireActivity(),
                cameraLauncher,
                galleryLauncher,
                permissionLauncher
            ) { uri ->
                uri?.let {
                    uploadFile(CommonMethod.uriToFile(requireActivity(), it))
                }
            }
        }
    }

    private fun setData() {
        binding?.apply {
            ivProfileImage.load(profileInfo?.profilePic) {
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            etFirstName.setText(profileInfo?.fullNameEnglish)
            etLastName.setText(profileInfo?.fullNameArabic)
            tvCountryPicker.text = profileInfo?.countryCode
            etMobileNumber.setText(profileInfo?.mobile)
            etEmail.setText(profileInfo?.email)
            etAddressLine1.setText(profileInfo?.addressLine1)
            etAddressLine2.setText(profileInfo?.addressLine2)
            etCity.setText(profileInfo?.city)
            etState.setText(profileInfo?.state)
            zoneId = profileInfo?.zoneId.toString()
            imageUrl = profileInfo?.profilePic.toString()
            etZone.setText(profileInfo?.zoneInfo?.nameEnglish)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvChange.setOnClickListener {
                imagePicker.showImageSourceDialog()
            }

            relAddressLine1.setOnClickListener {
                val intent = Intent(requireActivity(), UpdateLocationActivity::class.java)
                intent.putExtra("savedLat", lat)
                intent.putExtra("savedLng", lng)
                intent.putExtra("savedAddress", address)
                Log.e("send", "initControl: $address $lat $lng" )
                locationResultLauncher.launch(intent)
            }

            etZone.setOnClickListener {
                showZoneDropdown(etZone)
            }

            btnEdit.setOnClickListener {
                if (isValid()){

                }
            }
        }
    }


    fun uploadFile(image: File){
        Log.e("imageupload", "uploadFile: $image" )
        viewModel.uploadImage(image)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isImageObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver||isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isImageObserver = false
            }
        }

        viewModel.kuwaitZoneResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    kuwaitZoneList.clear()
                    kuwaitZoneList.addAll(response.data)
                }
                isObserver = false
            }
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { response ->
            if (isImageObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    imageUrl = response.data.url
                }
                isImageObserver = false
            }
        }

    }

    private val locationResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val city = data?.getStringExtra("city") ?: "N/A"
            val street = data?.getStringExtra("street") ?: "N/A"
            val state = data?.getStringExtra("state") ?: "N/A"
            val pincode = data?.getStringExtra("pincode") ?: "N/A"
            val houseno = data?.getStringExtra("houseno") ?: "N/A"
            val addressH = data?.getStringExtra("addressH") ?: "N/A"
            lat = data?.getDoubleExtra("lat", 0.0)?:0.0
            lng = data?.getDoubleExtra("lng", 0.0)?:0.0

            Log.e("send", "getnew: $lat $lng" )

            address = addressH

            val cleaned = cleanAddress(addressH, city, state)

            val (line1, line2) = splitIntoTwoLines(cleaned)

            binding?.etAddressLine1?.setText(line1)
            binding?.etAddressLine2?.setText(line2)

            binding?.etAddressLine1?.setText(line1)
            binding?.etAddressLine2?.setText(line2)

            binding?.etCity?.setText(city)
            binding?.etState?.setText(state)
        }
    }

    private fun showZoneDropdown(view: AppCompatEditText) {
        val list = kuwaitZoneList.map { it.nameEnglish }
        val popup = androidx.appcompat.widget.ListPopupWindow(requireActivity())
        popup.anchorView = view
        popup.setAdapter(ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, list))
        popup.setOnItemClickListener { _, _, pos, _ ->
            zoneId = kuwaitZoneList[pos].id.toString()
            view.setText(kuwaitZoneList[pos].nameEnglish)
            popup.dismiss()
        }
        popup.show()
    }

    private fun cleanAddress(addressH: String, city: String, state: String): String {
        var cleaned = addressH

        // Remove city & state if contained
        if (city.isNotBlank()) cleaned = cleaned.replace(city, "", true)
        if (state.isNotBlank()) cleaned = cleaned.replace(state, "", true)

        // Remove country patterns commonly seen
        cleaned = cleaned
            .replace("United Arab Emirates", "", true)
            .replace("UAE", "", true)

        // Remove lat/lng numeric patterns
        cleaned = cleaned.replace(Regex("\\b\\d+\\.\\d+\\b"), "")

        // Remove double spaces, extra hyphens, commas
        cleaned = cleaned
            .replace(", ,", ",")
            .replace(",,", ",")
            .replace("--", "-")
            .replace(" - ", "-")
            .trim().trim(',', '-', ' ')

        return cleaned
    }

    private fun splitIntoTwoLines(cleaned: String): Pair<String, String> {

        // Split by comma OR hyphen OR space-group
        val parts = cleaned.split(Regex("[,-]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        if (parts.size < 2) {
            return Pair(cleaned, "")   // fallback
        }

        val half = parts.size / 2

        val line1 = parts.take(half).joinToString(", ")
        val line2 = parts.drop(half).joinToString(", ")

        return Pair(line1, line2)
    }

    private fun isValid(): Boolean {

        binding?.apply {

            if (imageUrl == null) {
                showToast("Please Upload Profile Image")
                return false
            }
            if (etFirstName.text.toString().trim().isEmpty()) {
                showToast("Please Enter First Name")
                return false
            }

            if (etLastName.text.toString().trim().isEmpty()) {
                showToast("Please Enter Last Name")
                return false
            }

            val mobile = etMobileNumber.text.toString().trim()
            if (mobile.isEmpty()) {
                showToast("Please Enter Mobile Number")
                return false
            }
            if (mobile.length < 7 ) {
                showToast("Mobile Number in Between 7-15 digits ")
                return false
            }
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                showToast("Please Enter Email Address")
                return false
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Please Enter Valid Email Address"
                return false
            }
            if (etAddressLine1.text.toString().trim().isEmpty()) {
                showToast("Please Enter Address Line 1")
                return false
            }

            if (etAddressLine2.text.toString().trim().isEmpty()) {
                showToast("Please Enter Address Line 2")
                return false
            }

            if (etCity.text.toString().trim().isEmpty()) {
                showToast("Please Enter City/District")
                return false
            }

            if (etState.text.toString().trim().isEmpty()) {
                showToast("Please Enter State")
                return false
            }

            if (etZone.text.toString().trim().isEmpty()) {
                showToast("Please Select Zone")
                return false
            }

        }

        return true
    }


}