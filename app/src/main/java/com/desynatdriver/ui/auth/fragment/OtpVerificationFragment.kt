package com.desynatdriver.ui.auth.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.AuthViewModel
import com.desynatdriver.databinding.FragmentOtpVerificationBinding
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment() {
    private var binding: FragmentOtpVerificationBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var mobileNumber = ""
    var countryCode = ""
    var email = ""
    var from = ""
    var userId = ""
    var type = 0 //0-mobile,1-email
    lateinit var timer: CountDownTimer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOtpVerificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        startTimer()
        mobileNumber = arguments?.getString("mobileNumber").toString()
        countryCode = arguments?.getString("countryCode").toString()
        from = arguments?.getString("from").toString()
        email = arguments?.getString("email").toString()
        userId = arguments?.getString("userId").toString()
        setupOtpInputs()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.otp_verification)
              if (email!=""&&email!="null"){
                type = 1
                  tvMobile.text = email
            }
            else{
                type = 0
                  tvMobile.text = "${countryCode} ${mobileNumber}"
            }

        }

    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()

            }

            tvResendOtp.setOnClickListener {
                resendOtp()
            }

            btnVerify.setOnClickListener {
                val otp = if (
                    resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
                ) {

                    binding?.etOtp6?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp1?.text.toString()

                } else {

                    binding?.etOtp1?.text.toString() +
                            binding?.etOtp2?.text.toString() +
                            binding?.etOtp3?.text.toString() +
                            binding?.etOtp4?.text.toString() +
                            binding?.etOtp5?.text.toString() +
                            binding?.etOtp6?.text.toString()
                }
               // val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast(getString(R.string.please_enter_otp))
                    }
                    otp.length < 6 -> {
                        showToast(getString(R.string.please_enter_a_valid_6_digit_otp))
                    }
                    else -> {
                            verifyOtp(otp)
                    }
                }
            }
        }

    }

    private fun verifyOtp(otp: String){
        val map = HashMap<String,Any>()
        map["user_id"] = userId
        map["otp"] = otp
        viewModel.otpVerification(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun resendOtp(){
        val map = HashMap<String,Any>()
        if (email!=""&&email!="null"){
            map["email_mobile"] = email
        }
        else{
            map["email_mobile"] = mobileNumber
            map["country_code"] = countryCode
        }
        map["type"] = type
        viewModel.resendOtp(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.otpVerifyResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(getString(R.string.otp_verified_successfully))
                    val bundle = Bundle().apply {
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_otpFragment_to_resetPasswordFragment,bundle)

                }
                isObserver = false
            }
        }


        viewModel.resendOtpResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                if (type==0){
                    showToast(getString(R.string.otp_sent_to_mobile_number_successfully))
                }
                else{
                    showToast(getString(R.string.otp_sent_to_email_address_successfully))
                }
                response?.let {
                    startTimer()

                }
                isObserver = false
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding?.tvResendOtp?.text = "${seconds} Sec"
                binding?.tvResendOtp?.isClickable = false
            }

            override fun onFinish() {
                binding?.tvResendOtp?.text = getString(R.string.resend_otp)
                binding?.tvResendOtp?.isClickable = true
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }

    private fun setupOtpInputs() {

        val otpFields = listOf(
            binding?.etOtp1,
            binding?.etOtp2,
            binding?.etOtp3,
            binding?.etOtp4,
            binding?.etOtp5,
            binding?.etOtp6
        )

        val isArabic =
            resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

        // First focus
        if (isArabic) {
            binding?.etOtp6?.requestFocus()
        } else {
            binding?.etOtp1?.requestFocus()
        }

        otpFields.forEachIndexed { index, editText ->

            editText?.addTextChangedListener(object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: android.text.Editable?) {

                    if (s?.length == 1) {

                        if (isArabic) {

                            // Move LEFT in Arabic
                            if (index > 0) {
                                otpFields[index - 1]?.requestFocus()
                            }

                        } else {

                            // Move RIGHT in English
                            if (index < otpFields.size - 1) {
                                otpFields[index + 1]?.requestFocus()
                            }
                        }
                    }
                }
            })

            editText?.setOnKeyListener { _, keyCode, event ->

                if (event.action == android.view.KeyEvent.ACTION_DOWN &&
                    keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    editText.text.isEmpty()
                ) {

                    if (isArabic) {

                        // Back move RIGHT in Arabic
                        if (index < otpFields.size - 1) {
                            otpFields[index + 1]?.requestFocus()
                        }

                    } else {

                        // Back move LEFT in English
                        if (index > 0) {
                            otpFields[index - 1]?.requestFocus()
                        }
                    }
                }

                false
            }
        }
    }

}