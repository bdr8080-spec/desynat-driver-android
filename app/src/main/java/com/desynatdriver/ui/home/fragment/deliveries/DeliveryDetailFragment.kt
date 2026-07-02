package com.desynatdriver.ui.home.fragment.deliveries

import android.content.res.ColorStateList
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
import com.desynatdriver.utils.Constants
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


            tvDeliveryIdValue.text = "#${deliveryDetailData?.id}"
            tvOrderIdValue.text = "#${deliveryDetailData?.id}"
            tvTotalDistanceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.distance?:0.0)} ${getString(R.string.km)}"
            tvPriceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.order?.totalAmount?:0.0) } ${getString(R.string.kd)}"
            tvPaymentStatus.text = if (deliveryDetailData?.paymentStatus=="1"){
                getString(R.string.paid)
            }
            else{
                if (deliveryDetailData?.order?.paymentMethod=="3"){
                    getString(R.string.cod)
                }
                else{
                    getString(R.string.pending)
                }

            }

            tvSize.text = "${getString(R.string.size)}: ${if (Constants.language=="ar") deliveryDetailData?.variationDetails?.size?.name  else deliveryDetailData?.variationDetails?.size?.name}"
            deliveryDetailData?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(Color.parseColor(it)) }
            tvQuantityValue.text = "${deliveryDetailData?.quantity}"
            ivOrder.load(deliveryDetailData?.variationDetails?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvStore.text = if (Constants.language=="ar") deliveryDetailData?.productDetails?.productNameAr else deliveryDetailData?.productDetails?.productNameEn
            if (deliveryDetailData?.userRatingReviewProvide!=null) {
                tvRating.text = deliveryDetailData?.userRatingReviewProvide?.storeRating.toString()

                if (deliveryData?.returnType=="1"||deliveryData?.returnType=="2"){
                    rbStore.isVisible = false
                    tvRating.isVisible = false
                }
                else{
                    rbStore.isVisible = true
                    tvRating.isVisible = true
                }
                rbStore.rating = deliveryDetailData?.userRatingReviewProvide?.storeRating ?: 0f
            }

            tvDeliveryOnValue.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.updatedAt.toString(), outputFormat = "dd/MM/yyyy '${
                getString(R.string.at)
            }' hh:mm a")
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy '${getString(R.string.at)}' hh:mm a")
            when(deliveryDetailData?.deliveryStatus){
                "0"->{
                    when (deliveryData?.returnType) {
                        "1" -> {
                            tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvName.text = deliveryDetailData?.order?.userDetails?.fullName
                            tvCustomerName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            tvCustomer.text = getString(R.string.store_name)
                            tvPrice.isVisible = false
                            tvPriceValue.isVisible = false
                            tvPaymentStatus.isVisible = false
                            ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }
                            tvStatus.text =getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                        }

                        "2" -> {
                            tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvName.text = deliveryDetailData?.order?.userDetails?.fullName
                            tvCustomerName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            tvCustomer.text = getString(R.string.store_name)
                            tvPrice.isVisible = false
                            tvPriceValue.isVisible = false
                            tvPaymentStatus.isVisible = false
                            ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }
                            tvStatus.text = getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                        }

                        else -> {
                            tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.name

                            tvStatus.text = context?.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }

                        }
                    }
                }

                "3"->{
                    when (deliveryData?.returnType) {
                        "1" -> {
                            tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvName.text = deliveryDetailData?.order?.userDetails?.fullName
                            tvCustomerName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            tvCustomer.text = getString(R.string.store_name)
                            tvPrice.isVisible = false
                            tvPriceValue.isVisible = false
                            tvPaymentStatus.isVisible = false
                            ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }
                            tvStatus.text = context?.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                        }

                        "2" -> {
                            tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvName.text = deliveryDetailData?.order?.userDetails?.fullName
                            tvCustomerName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            tvCustomer.text = getString(R.string.store_name)
                            tvPrice.isVisible = false
                            tvPriceValue.isVisible = false
                            tvPaymentStatus.isVisible = false
                            ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }
                            tvStatus.text = context?.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                        }

                        else -> {
                            tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                            tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                            tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.name
                            tvStatus.text = context?.getString(R.string.delivered)
                            tvStatus.setTextColor(Color.parseColor("#17854C"))
                            tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                            ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                                crossfade(true)
                                placeholder(R.drawable.ic_user)
                                error(R.drawable.ic_user)
                            }

                        }
                    }
                }

                "8"->{
                    tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.name
                    tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                    ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    tvStatus.text = getString(R.string.not_delivered)
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvReason.isVisible = true
                    tvReasonForNotDelivered.isVisible = true
                    tvReasonForNotDelivered.text = deliveryData?.notDeliveredReason

                }
                else->{
                    tvStatus.text = getString(R.string.confirmed)
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.name
                    tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                    ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
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