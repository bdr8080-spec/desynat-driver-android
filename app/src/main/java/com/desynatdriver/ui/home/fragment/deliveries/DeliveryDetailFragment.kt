package com.desynatdriver.ui.home.fragment.deliveries

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdriver.R
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.FragmentDeliveryDetailBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.DeliveryDetailResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class DeliveryDetailFragment : BaseFragment() {
    private var binding: FragmentDeliveryDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var deliveryData: GetAllOrderResponse.Data.Data?=null
    var deliveryDetailData: DeliveryDetailResponse.Data?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        val itemJson = arguments?.getString("deliveryData")
        if (!itemJson.isNullOrEmpty()) {
            deliveryData = Gson().fromJson(itemJson, GetAllOrderResponse.Data.Data::class.java)
            setData()
            getOrderDetail()
        }
        binding?.apply {
        }
    }

    private fun setData() {
        binding?.apply {

            ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }

            tvDeliveryIdValue.text = "#${deliveryDetailData?.id}"
            tvOrderIdValue.text = "#${deliveryDetailData?.id}"
            tvTotalDistanceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.distance?:0.0)} Km"
            tvPriceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.order?.totalAmount?:0.0) } KD"
            tvPaymentStatus.text = if (deliveryDetailData?.paymentStatus=="1"){
                "Paid"
            }
            else{
                "Pending"
            }
            tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
            tvSize.text = "Size: ${deliveryDetailData?.variationDetails?.size?.name}"
            deliveryDetailData?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(Color.parseColor(it)) }
            tvQuantityValue.text = "Size: ${deliveryDetailData?.quantity}"
            ivOrder.load(deliveryDetailData?.variationDetails?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvStore.text = deliveryDetailData?.productDetails?.productNameEn
            tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
            tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.address
            tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
            tvDeliveryOnValue.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")
            when(deliveryDetailData?.deliveryStatus){
                "1"->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                }
                "7"->{
                    tvStatus.text = "Picked"
                    tvStatus.setTextColor(Color.parseColor("#2A74B8"))
                }
                "3"->{
                    tvStatus.text = "Delivered"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                }

                "8"->{
                    tvStatus.text = "Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvReason.isVisible = true
                    tvReasonForNotDelivered.isVisible = true
                    tvReasonForNotDelivered.text = deliveryData?.notDeliveredReason
                }
                else->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
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

        viewModel.orderDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryDetailData = response.data
                    setData()
                }
                isObserver = false
            }
        }

    }

    private fun getOrderDetail() {
        val map = HashMap<String,Any>()
        map["id"] = deliveryData?.id.toString()
        viewModel.orderDetail(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

}