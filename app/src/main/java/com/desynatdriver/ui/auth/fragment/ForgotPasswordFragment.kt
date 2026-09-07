package com.desynatdriver.ui.auth.fragment

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.AuthViewModel
import com.desynatdriver.databinding.FragmentForgotPasswordBinding
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment() {
    private var binding: FragmentForgotPasswordBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    private var type = "mobile"
    private var countryList = ArrayList<GetCountryResponse.Data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        countryList.clear()
        SharedPreference.get(requireActivity()).getCountryList()?.let { countryList.addAll(it) }
        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmail)
            toolbar.tvToolbar.text = getString(R.string.forgot_password)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            tvCountryPicker.setOnClickListener {
                CommonMethod.showCountryPicker(requireActivity(),clMobile,countryList, isDialCode = true){selectedCountry->
                    tvCountryPicker.text = selectedCountry.dialCode
                }
            }

            cbEmail.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    type = "email"
                    clearData()
                    cbMobile.isChecked = false
                    clMobile.isVisible = false
                    tvInputTitle.text = getString(R.string.email_address)
                    etEmail.isVisible = true
                }
            }

            cbMobile.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    type = "mobile"
                    clearData()
                    cbEmail.isChecked = false
                    clMobile.isVisible = true
                    etEmail.isVisible = false
                    tvInputTitle.text = getString(R.string.mobile_number)

                }
            }

            btnContinue.setOnClickListener {
                if (validation()) {
                    val map = HashMap<String, Any>()
                    if (binding?.cbMobile?.isChecked == true) {
                        map["country_code"] = binding?.tvCountryPicker?.text.toString()
                        map["mobile"] = binding?.etMobile?.text.toString().trim()
                        map["type"] = 0
                    }
                    else {
                        map["email"] = binding?.etEmail?.text.toString().trim()
                        map["type"] = 1
                    }

                    viewModel.forgotPassword(map)
                    ProgressDialogUtils.getInstance().showProgress(requireActivity())
                    isObserver = true

                }
            }
        }
    }


    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }

        }

        viewModel.forgotPasswordResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    val bundle = Bundle().apply {
                        if (binding?.cbMobile?.isChecked == true) {
                            putString("countryCode", binding?.tvCountryPicker?.text.toString())
                            putString("mobileNumber", binding?.etMobile?.text.toString().trim())
                        }
                        else {
                            putString("email", binding?.etEmail?.text.toString().trim())
                        }
                        putString("from", "forgot")
                        putString("userId", response.data.id)
                    }
                    findNavController().navigate(R.id.action_forgotFragment_to_otpFragment,bundle)
                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{
        val email = binding?.etEmail?.text.toString().trim()

        if (binding?.etMobile?.text?.isEmpty() == true&&binding?.cbMobile?.isChecked==true) {
            showToast(getString(R.string.please_enter_mobile_number))
            return false
        }
        else if (binding?.etMobile?.text?.length!! < 7&&binding?.cbMobile?.isChecked==true) {
            showToast(getString(R.string.please_enter_valid_mobile_number_between_7_to_15_digits))
            return false
        }
        else if (email.isBlank()&&binding?.cbEmail?.isChecked==true) {
            showToast(getString(R.string.please_enter_email))
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.all { it.isDigit() }&&binding?.cbEmail?.isChecked==true) {
            showToast(getString(R.string.please_enter_valid_email))
            return false
        }
        return true
    }

    private fun clearData() {
        binding?.etMobile?.setText("")
        binding?.etEmail?.setText("")
    }

}