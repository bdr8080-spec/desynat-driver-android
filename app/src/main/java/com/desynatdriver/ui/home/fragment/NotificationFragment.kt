package com.desynatdriver.ui.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
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

    private var currentPage = 1
    private var lastPage = 1
    private var totalPage = 0
    private var totalItem=0
    private var pageLimit=10
    private var pagination=false

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


            nestedScrollView.setOnScrollChangeListener { nestedScrollView: NestedScrollView, _, scrollY, _, oldScrollY ->
                val lastChild = nestedScrollView.getChildAt(nestedScrollView.childCount - 1)

                if (lastChild != null) {
                    val diff = lastChild.bottom - (nestedScrollView.height + scrollY)
                    if (diff == 0 && scrollY > oldScrollY) {
                        if(totalItem==0)
                            return@setOnScrollChangeListener
                        if (!isObserver && !pagination && currentPage < lastPage) {
                            pagination = true
                            currentPage += 1
                            getNotification(false)
                            binding?.progressBar?.isVisible = true
                        }
                    }
                }
            }
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
        var map = HashMap<String, Any>()
        map["page"] = currentPage
        map["limit"] = pageLimit
        viewModel.getNotification(map)
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
                    ProgressDialogUtils.getInstance().hideProgress()
                    binding?.progressBar?.isVisible = false
                    currentPage = response.data.currentPage
                    totalItem = response.data.total

                    lastPage = response.data.lastPage

                    val oldSize = notificationList.size
                    if (!pagination){
                        notificationList.clear()
                    }
                    val newItems = response.data.data ?: emptyList()

                    notificationList.addAll(newItems)
                    checkData()

                    if (pagination){

                        response.data.data?.size?.let {
                            notificationAdapter?.notifyItemRangeInserted(oldSize,
                                it
                            )
                        }
                    }
                    else{
                        notificationAdapter?.notifyDataSetChanged()
                    }

                    pagination = false
                    isObserver = false
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