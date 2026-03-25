package com.desynatdriver.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.adapter.NotificationAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentNotificationBinding
import com.desynatdriver.ui.home.response.GetNotificationResponse
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import kotlin.getValue
@AndroidEntryPoint
class NotificationFragment : BaseFragment() {
    private var binding: FragmentNotificationBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isReadAllObserver = true
    private var isReadObserver = true
    private var notificationList = ArrayList<GetNotificationResponse.Data.Data>()
    private var notificationAdapter: NotificationAdapter?=null
    private var selectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()
        binding?.apply {
            notificationAdapter = NotificationAdapter(requireActivity(),notificationList,object :
                NotificationAdapter.ClickOnItem{
                override fun onClickItem(position: Int) {
                    selectedPos = position
                    readNotification()
                }
            })
            rvNotification.adapter = notificationAdapter
        }

        getNotification()
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvReadAll.setOnClickListener {
                readAllNotification()
            }
        }
    }

    private fun getNotification(isLoading: Boolean=true){
        viewModel.getNotification()
        isObserver = true
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
    }

    private fun readNotification(){
        val id = notificationList[selectedPos].id.toString()
        val idList = arrayListOf(id)
        val map = HashMap<String,Any>()
        map["notification_id"] = idList
        viewModel.readNotification(map)
        isReadObserver = true
    }

    private fun readAllNotification(){
        val map = HashMap<String,Any>()
        viewModel.readAllNotification(map)
        isReadAllObserver = true
    }

    override fun observe() {

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
            }
        }

        viewModel.getNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    notificationList.clear()
                    notificationList.addAll(response.data.data)
                    notificationAdapter?.notifyDataSetChanged()
                    checkData()
                }
            }
        }

        viewModel.readNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isReadObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    notificationList[selectedPos].isRead = 1
                    notificationAdapter?.notifyItemChanged(selectedPos)
                    selectedPos = -1
                    isReadObserver = false
                }
            }
        }
        viewModel.readAllNotificationResult.observe(viewLifecycleOwner) { response ->
            if (isReadAllObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    notificationList.forEach {
                        it.isRead = 1
                    }

                    notificationAdapter?.notifyDataSetChanged()
                    isReadAllObserver = false
                }
            }
        }
    }

    private fun checkData(){
        binding?.tvNoDataFound?.isVisible = notificationList.isEmpty()
      //  binding?.tvReadAll?.isVisible = notificationList.isNotEmpty()
    }
}