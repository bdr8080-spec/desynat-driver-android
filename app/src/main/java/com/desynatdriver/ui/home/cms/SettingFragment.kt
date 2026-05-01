package com.desynatdriver.ui.home.cms

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.DeleteAccountDialogBinding
import com.desynatdriver.databinding.FragmentSettingBinding
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import kotlin.text.get

@AndroidEntryPoint
class SettingFragment : BaseFragment() {
    private var binding: FragmentSettingBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var userInfo: GetProfileResponse.Data?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        userInfo = SharedPreference.get(requireActivity()).getUser()
        setData()
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.settings)
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                (requireActivity() as? HomeActivity)?.onBackPressed()
            }

            btnDeleteAccount.setOnClickListener {
                deleteAlertDialog()
            }

            tvInAppNotification.setOnClickListener {
                appNotificationOnOff()
            }

            tvPushNotification.setOnClickListener {
                notificationOnOff()
            }

        }

    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                isObserver = false
            }
        }

        viewModel.notificationOnOffResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    userInfo?.notification = response?.data?.notification?:0
                    userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }

        viewModel.appNotificationOnOffResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    userInfo?.appNotification = response?.data?.appNotification?:0
                    userInfo?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                    setData()
                    CommonMethod.showSuccessDialog(requireActivity(),response?.message.toString())
                }
                isObserver = false
            }
        }
    }

    fun deleteAlertDialog(
    ) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = DeleteAccountDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)


        dialogBinding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnYes.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun notificationOnOff(){
        val map = HashMap<String, Any>()
        viewModel.notificationOnOff(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true

    }

    fun appNotificationOnOff(){
        val map = HashMap<String, Any>()
        viewModel.appNotificationOnOff(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity(), false)
        isObserver = true

    }

    private fun setData(){
        binding?.apply {
            if(userInfo?.appNotification==1){
                tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_on, 0 )
            }
            else{
                tvInAppNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_in_app_notification, 0,R.drawable.switch_off,0 )
            }

            if(userInfo?.notification==1){
                tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_on,0 )
            }
            else{
                tvPushNotification.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_push_notification, 0,R.drawable.switch_off,0 )
            }
        }
    }

}
