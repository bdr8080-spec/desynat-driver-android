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

        binding?.apply {
            toolbar.tvToolbar.text = "OTP Verification"
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
                val otp = pinView.text.toString().trim()

                when {
                    otp.isEmpty() -> {
                        showToast("Please enter OTP")
                    }
                    otp.length < 6 -> {
                        showToast("Please enter a valid 6-digit OTP")
                    }
                    else -> {
                            verifyOtp()
                    }
                }
            }
        }

    }

    private fun verifyOtp(){
        val map = HashMap<String,Any>()
        map["user_id"] = userId
        map["otp"] = binding?.pinView?.text?.trim().toString()
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
                    showToast("OTP verified successfully")
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
                    showToast("OTP sent to mobile number successfully")
                }
                else{
                    showToast("OTP sent to email address successfully")
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
}