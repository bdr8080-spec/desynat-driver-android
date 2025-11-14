package com.desynatdriver.ui.auth.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.desynatdriver.databinding.FragmentLoginBinding
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import com.desynatdriver.utils.togglePasswordVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    private var binding: FragmentLoginBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    var isObserver = false
    var type = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            CommonMethod.removeAllTextSpaces(etEmailMobile)
            CommonMethod.removeAllTextSpaces(etPassword)
            etPassword.togglePasswordVisibility(ivPasswordVisible)
        }
    }

    override fun initControl() {
        binding?.apply {
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            }

            btnLogin.setOnClickListener {
                if (validation()) {
                    loginWithPassword()
                }
            }

                etEmailMobile.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        val input = s.toString().trim()
                        if (input.isNotEmpty()) {
                            if (input.matches(Regex("^[0-9]+$"))) {
                                type = 0
                                ccp.isVisible = true
                                ivEmail.setImageResource(R.drawable.ic_mobile)
                                etEmailMobile.inputType = InputType.TYPE_CLASS_NUMBER
                            } else {
                                type = 1
                                ccp.isVisible = false
                                ivEmail.setImageResource(R.drawable.ic_email)
                                etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            }
                        } else {
                            type = 1
                            ccp.isVisible = false
                            ivEmail.setImageResource(R.drawable.ic_email)
                            etEmailMobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {}
                })


        }
    }

    private fun loginWithPassword(){
        val map = HashMap<String,Any>()

        map["email_mobile"] = binding?.etEmailMobile?.text?.trim().toString()
        if (type==0){
            map["country_code"] = binding?.ccp?.selectedCountryCodeWithPlus?.trim().toString()
        }
        map["type"] = type
        map["password"] = binding?.etPassword?.text?.trim().toString()
        viewModel.login(map)
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

        viewModel.loginResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    showToast(response.message)
                    SharedPreference.get(requireActivity()).accessToken = response.data.accessToken
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    requireActivity().startActivity(
                        Intent(requireActivity(),
                            HomeActivity::class.java)
                    )
                    requireActivity().finish()
                }
                isObserver = false
            }
        }

    }

    private fun validation():Boolean{

        if (binding?.etEmailMobile?.text?.isEmpty() == true) {
            showToast("Please enter mobile number or email address")
            return false
        }
        else if (binding?.etEmailMobile?.text?.length!! < 7&&type==0) {
            showToast("Number must be at least 7 digits long")
            return false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmailMobile?.text.toString()).matches() && type==1) {
            showToast("Please enter valid email")
            return false
        }
        else if (binding?.etPassword?.text?.isEmpty() == true) {
            showToast("Please enter password")
            return false
        }
        return true
    }

}