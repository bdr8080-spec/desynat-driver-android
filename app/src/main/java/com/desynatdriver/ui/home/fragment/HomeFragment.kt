package com.desynatdriver.ui.home.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.desynatdriver.R
import com.desynatdriver.adapter.NewDeliveriesAdapter
import com.desynatdriver.adapter.OngoingDeliveriesAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentHomeBinding
import com.desynatdriver.ui.home.HomeActivity
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.SharedPreference
import com.desynatdriver.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var binding: FragmentHomeBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    var isObserver = false
    var isNewObserver = false
    var isCompletedObserver = false
    var isOngoingObserver = false
    var isProfileObserver = false
    private var newDeliveriesAdapter: NewDeliveriesAdapter?=null
    private var ongoingDeliveriesAdapter: OngoingDeliveriesAdapter?=null
    private var ongoingDeliveriesList = ArrayList<GetAllOrderResponse.Data.Data>()
    private var completedDeliveriesList = ArrayList<GetAllOrderResponse.Data.Data>()
    private var newDeliveriesList = ArrayList<GetAllOrderResponse.Data.Data>()
    private var user: GetProfileResponse.Data?=null
    private var status = 0

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

            newDeliveriesAdapter = NewDeliveriesAdapter(requireActivity(), newDeliveriesList,object :NewDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${newDeliveriesList[position]?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.countryCode+newDeliveriesList[position]?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storePhone}")
                            }
                            startActivity(intent)

                        }
                        2 -> {

                            if (newDeliveriesList[position].deliveryStatus!="3") {
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(newDeliveriesList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_homeFragment_to_newOrderFragment,
                                    bundle
                                )
                            }
                            else{
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(newDeliveriesList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_homeFragment_to_deliveryDetailFragment,
                                    bundle
                                )
                            }
                        }
                    }
                }

            })
            rvNewDeliveries.adapter = newDeliveriesAdapter

            ongoingDeliveriesAdapter = OngoingDeliveriesAdapter(requireActivity(), ongoingDeliveriesList,object :OngoingDeliveriesAdapter.ClickOnItem{
                override fun onClickItem(position: Int, type: Int) {
                    when(type){
                        1->{
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${newDeliveriesList[position]?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.countryCode+newDeliveriesList[position]?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storePhone}")
                            }
                            startActivity(intent)

                        }
                        2 -> {

                            if (ongoingDeliveriesList[position].deliveryStatus!="3") {
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(ongoingDeliveriesList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_deliveriesFragment_to_newOrderFragment,
                                    bundle
                                )
                            }
                            else{
                                val bundle = Bundle().apply {
                                    putString("deliveryData", Gson().toJson(ongoingDeliveriesList[position]))
                                }

                                findNavController().navigate(
                                    R.id.action_deliveriesFragment_to_deliveryDetailFragment,
                                    bundle
                                )
                            }
                        }
                    }
                }

            })
            rvOngoingDeliveries.adapter = ongoingDeliveriesAdapter

        }
        getNewOrder()
        getOngoingOrder()
        getCompletedOrder()

    }

    private fun getProfile() {
        viewModel.getProfile()
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isProfileObserver = true
    }

    override fun initControl() {

        binding?.apply {
            tvSwitch.setOnClickListener {
                updateDutyStatus()
            }

            etFrom.setOnClickListener {

            }

            etTo.setOnClickListener {

            }

            viewAllNewDeliveries.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("DELIVERY_TYPE", 2) // 2 = NEW
                }
                (requireActivity() as? HomeActivity)?.openDeliveriesTab(bundle)
            }

            viewAllOngoingDeliveries.setOnClickListener {
                val bundle = Bundle().apply {
                    putInt("DELIVERY_TYPE", 3) // 3 = ONGOING
                }
                (requireActivity() as? HomeActivity)?.openDeliveriesTab(bundle)
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

    private fun updateDutyStatus() {
        val map = HashMap<String,Any>()
        map["delivery_boy_id"] = user?.id.toString()
        viewModel.dutyOnOff(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(status==1){
                tvSwitch.text = "On Duty"
                tvSwitch.setTextColor(Color.parseColor("#000000"))
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
            if (isObserver || isProfileObserver || isOngoingObserver || isNewObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                errorMessage?.let { showToast(it) }
                isObserver = false
                isProfileObserver = false
                isNewObserver = false
                isOngoingObserver = false
            }
        }

        viewModel.profileResult.observe(viewLifecycleOwner) { response ->
            if (isProfileObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                user = response.data
                    SharedPreference.get(requireActivity()).saveUser(response.data)
                    status = response.data.onDuty
                    updateStatus()
                }
                isProfileObserver = false
            }
        }

        viewModel.dutyOnOffResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                user?.onDuty = response.data.onDuty
                if (response.data.onDuty==1){
                    showToast("Duty on successfully")
                }
                else{
                    showToast("Duty off successfully")
                }

                user?.let { SharedPreference.get(requireActivity()).saveUser(it) }
                status = response.data.onDuty
                updateStatus()
                isObserver = false
            }
        }

        viewModel.getNewOrderResult.observe(viewLifecycleOwner) { response ->
            if (isNewObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    newDeliveriesList.clear()
                    newDeliveriesList.addAll(response.data.data)
                    newDeliveriesAdapter?.notifyDataSetChanged()

                    if(newDeliveriesList.size!=0){
                        binding?.viewAllNewDeliveries?.isVisible = true
                        binding?.tvNoNewDeliveriesFound?.isVisible = false
                        binding?.tvNewDeliveries?.text = newDeliveriesList.size.toString()
                    }
                    else{
                        binding?.viewAllNewDeliveries?.isVisible = false
                        binding?.tvNoNewDeliveriesFound?.isVisible = true
                    }
                }
                isNewObserver = false
            }
        }

        viewModel.getOngoingOrderResult.observe(viewLifecycleOwner) { response ->
            if (isOngoingObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    ongoingDeliveriesList.clear()
                    ongoingDeliveriesList.addAll(response.data.data)
                    ongoingDeliveriesAdapter?.notifyDataSetChanged()
                    if(ongoingDeliveriesList.size!=0){
                        binding?.viewAllOngoingDeliveries?.isVisible = true
                        binding?.tvNoOngoingDeliveriesFound?.isVisible = false
                        binding?.tvOngoingDeliveries?.text = ongoingDeliveriesList.size.toString()
                    }
                    else{
                        binding?.viewAllOngoingDeliveries?.isVisible = false
                        binding?.tvNoOngoingDeliveriesFound?.isVisible = true
                    }
                }
                isOngoingObserver = false
            }
        }

        viewModel.getCompletedOrderResult.observe(viewLifecycleOwner) { response ->
            if (isCompletedObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    completedDeliveriesList.clear()
                    completedDeliveriesList.addAll(response.data.data)
                    if(completedDeliveriesList.size!=0){
                        binding?.tvCompletedDeliveries?.text = completedDeliveriesList.size.toString()
                    }

                }
                isCompletedObserver = false
            }
        }



    }

    private fun getNewOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getNewOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isNewObserver = true
    }

    private fun getCompletedOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getCompletedOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isCompletedObserver = true
    }

    private fun getOngoingOrder(search:String?=null) {
        val map = HashMap<String,Any>()
        search?.let { map["search"] = it }
        viewModel.getOngoingOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isOngoingObserver = true
    }

}