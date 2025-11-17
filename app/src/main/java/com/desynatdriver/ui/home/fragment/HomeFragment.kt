package com.desynatdriver.ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.desynatdriver.R
import com.desynatdriver.adapter.NewDeliveriesAdapter
import com.desynatdriver.adapter.OngoingDeliveriesAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.data.view_model.LocationViewModel
import com.desynatdriver.databinding.FragmentHomeBinding
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    private var newDeliveriesAdapter: NewDeliveriesAdapter?=null
    private var ongoingDeliveriesAdapter: OngoingDeliveriesAdapter?=null
    private var ongoingDeliveriesList = ArrayList<String>()
    private var newDeliveriesList = ArrayList<String>()
    private var user: GetProfileResponse.Data?=null
    private var status = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
       getProfile()
        binding?.apply {
            newDeliveriesList.clear()
            newDeliveriesList.add("")
            newDeliveriesList.add("")
            newDeliveriesList.add("")
            newDeliveriesAdapter = NewDeliveriesAdapter(requireActivity(), newDeliveriesList,object :NewDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                    }
                }

            })
            rvNewDeliveries.adapter = newDeliveriesAdapter

            ongoingDeliveriesList.clear()
            ongoingDeliveriesList.add("")
            ongoingDeliveriesList.add("")
            ongoingDeliveriesList.add("")
            ongoingDeliveriesAdapter = OngoingDeliveriesAdapter(requireActivity(), ongoingDeliveriesList,object :OngoingDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {
                    when(type){
                        1->{

                        }
                    }
                }

            })
            rvOngoingDeliveries.adapter = ongoingDeliveriesAdapter

        }
    }

    private fun getProfile() {
        viewModel.getProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    override fun initControl() {

        binding?.apply {
            tvSwitch.setOnClickListener {
                status = if (status==0) 1 else 0
                updateStatus()
            }

            etFrom.setOnClickListener {

            }

            etTo.setOnClickListener {

            }

            viewAllNewDeliveries.setOnClickListener {

            }

            viewAllOngoingDeliveries.setOnClickListener {

            }

            ivMenu.setOnClickListener {
                (requireActivity() as? HomeActivity)?.openDrawer()
            }

            ivNotification.setOnClickListener {

            }

            ivSupportBot.setOnClickListener {

            }
        }
    }


    private fun updateStatus(){
        binding?.apply {
            if(status==1){
                tvSwitch.text = "On Duty"
                tvSwitch.setTextColor(Color.parseColor("#8B5E3C"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds( 0,0,R.drawable.switch_on,0 )
            }
            else{
                tvSwitch.text = "Off Duty"
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds( 0,0,R.drawable.switch_off,0 )

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

        viewModel.profileResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                user = response.data
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                }
                isObserver = false
            }
        }


    }

}