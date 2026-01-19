package com.desynatdriver.ui.home.fragment.deliveries

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatdriver.R
import com.desynatdriver.adapter.ReasonAdapter
import com.desynatdriver.base.BaseFragment
import com.desynatdriver.data.view_model.HomeViewModel
import com.desynatdriver.databinding.CancelDeliveryDialogBinding
import com.desynatdriver.databinding.ExchangeReturnProductDialogBinding
import com.desynatdriver.databinding.FragmentNewOrderBinding
import com.desynatdriver.databinding.PackageDetailDialogBinding
import com.desynatdriver.databinding.PriceCollectDialogLayoutBinding
import com.desynatdriver.databinding.UpdateDeliveryStatusDialogBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.DeliveryDetailResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetReasonResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.ProgressDialogUtils
import com.desynatdriver.utils.showToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

@AndroidEntryPoint
class NewOrderFragment : BaseFragment(), OnMapReadyCallback {
    private var binding: FragmentNewOrderBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var isReturnObserver = true
    private var selectedDeliveryStatus: String = ""
    var deliveryData: GetAllOrderResponse.Data.Data?=null
    var deliveryDetailData: DeliveryDetailResponse.Data?=null
    var storeLat = 0.0
    var storeLng = 0.0
    var dropLat = 0.0
    var dropLng = 0.0
    private var cancelDialog: Dialog?=null
    private var reasonList = ArrayList<GetReasonResponse.Data>()
    private var reasonAdapter: ReasonAdapter? = null

    private var itemCollected = false
    private var isMapReady = false
    private var isLocationReady = false

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewOrderBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        initView()
        initControl()
        observe()
    }


    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        isMapReady = true
        tryDrawRoute()
    }

    override fun initView() {
        val itemJson = arguments?.getString("deliveryData")
        if (!itemJson.isNullOrEmpty()) {
            deliveryData = Gson().fromJson(itemJson, GetAllOrderResponse.Data.Data::class.java)
        }
        getOrderDetail()
        binding?.apply {
        }
    }

    private fun setData(){
        binding?.apply {

            if(deliveryDetailData?.returnType=="1"){
              returnFlow()
            }
            else if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"){
               exchangeFlow()
            }
            else{
                normalFlow()
            }

            if (storeLat != 0.0 && dropLat != 0.0) {
                isLocationReady = true
                tryDrawRoute()
            }
            if (deliveryDetailData?.userRatingReviewProvide!=null) {
                tvRating.text = deliveryDetailData?.userRatingReviewProvide?.storeRating.toString()
                rbStore.rating = deliveryDetailData?.userRatingReviewProvide?.storeRating ?: 0f
            }

        }
    }

    private fun normalFlow(){
        binding?.apply {
            viewPackageDetail.text = "View Package details"
            tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
            tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
            if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                dropLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
            }
            if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                dropLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
            }

            if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                storeLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
            }
            if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                storeLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
            }


            when(deliveryDetailData?.deliveryStatus){
                "7"->{
                    setDataAfterPickUp()
                    ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    rbStore.isVisible = false
                    tvRating.isVisible = false
                    tvName.text = deliveryDetailData?.order?.deliveryDetails?.name

                }

                else->{
                    ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    if (deliveryDetailData?.userRatingReviewProvide!=null) {
                        rbStore.isVisible = true
                        tvRating.isVisible = true
                    }
                    else{
                        rbStore.isVisible = false
                        tvRating.isVisible = false
                    }
                    tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                    tvOngoingDeliveries.isVisible = false
                    tvStatus.isVisible = false
                    tvUpdateStatus.isVisible = false
                    btnPickedOrder.isVisible = true
                    clPickUp.isVisible = true
                }

            }
        }
    }

    private fun exchangeFlow(){
        binding?.apply {
            viewPackageDetail.text = "View  Exchange Package details"
            itemCollected = deliveryDetailData?.returntostore=="0"&&deliveryDetailData?.deliveryStatus=="3"

            if (deliveryDetailData?.returntostore=="0"&&deliveryDetailData?.deliveryStatus=="3"){
                tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                    storeLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                }
                if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                    storeLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                }

                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                    dropLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                }
                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                    dropLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                }

                ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                    crossfade(true)
                    placeholder(R.drawable.ic_user)
                    error(R.drawable.ic_user)
                }
                if (deliveryDetailData?.userRatingReviewProvide!=null) {
                    rbStore.isVisible = true
                    tvRating.isVisible = true
                }
                else{
                    rbStore.isVisible = false
                    tvRating.isVisible = false
                }
                tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName

                tvOngoingDeliveries.isVisible = false
                tvStatus.isVisible = true
                tvUpdateStatus.isVisible = true
                btnPickedOrder.isVisible = false
                clPickUp.isVisible = false
            }
            else if(deliveryDetailData?.deliveryStatus=="7"){
                tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                    dropLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                }
                if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                    dropLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                }

                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                    storeLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                }
                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                    storeLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                }

                tvOngoingDeliveries.isVisible = false
                tvStatus.isVisible = true
                tvUpdateStatus.isVisible = true
                btnPickedOrder.isVisible = false
                clPickUp.isVisible = false
                ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                    crossfade(true)
                    placeholder(R.drawable.ic_user)
                    error(R.drawable.ic_user)
                }
                rbStore.isVisible = false
                tvRating.isVisible = false
                tvName.text = deliveryDetailData?.order?.deliveryDetails?.name

                tvOngoingDeliveries.isVisible = false
                tvStatus.isVisible = true
                tvUpdateStatus.isVisible = true
                btnPickedOrder.isVisible = false
                clPickUp.isVisible = false
            }
            else{
                tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                    dropLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                }
                if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                    dropLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                }

                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                    storeLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                }
                if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                    storeLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                }

                ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                    crossfade(true)
                    placeholder(R.drawable.ic_user)
                    error(R.drawable.ic_user)
                }
                if (deliveryDetailData?.userRatingReviewProvide!=null) {
                    rbStore.isVisible = true
                    tvRating.isVisible = true
                }
                else{
                    rbStore.isVisible = false
                    tvRating.isVisible = false
                }
                tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                tvOngoingDeliveries.isVisible = false
                tvStatus.isVisible = false
                tvUpdateStatus.isVisible = false
                btnPickedOrder.isVisible = true
                clPickUp.isVisible = true
            }
        }
    }

    private fun returnFlow(){
        binding?.apply {
            viewPackageDetail.text = "View Return details"
            when(deliveryDetailData?.deliveryStatus){
                "1" ->{
                    tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                        storeLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                    }
                    if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                        storeLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                    }

                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                        dropLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                    }
                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                        dropLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                    }
                    ivImage.load(deliveryDetailData?.order?.userDetails?.profileImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    rbStore.isVisible = false
                    tvRating.isVisible = false
                    tvName.text = deliveryDetailData?.order?.deliveryDetails?.name
                    tvOngoingDeliveries.isVisible = false
                    tvStatus.isVisible = false
                    tvUpdateStatus.isVisible = false
                    btnPickedOrder.isVisible = true
                    clPickUp.isVisible = true
                }
                "7" ->{
                    tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                        storeLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                    }
                    if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                        storeLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                    }

                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                        dropLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                    }
                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                        dropLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                    }
                    tvOngoingDeliveries.isVisible = false
                    tvStatus.isVisible = true
                    tvUpdateStatus.isVisible = true
                    btnPickedOrder.isVisible = false
                    clPickUp.isVisible = false
                    ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    if (deliveryDetailData?.userRatingReviewProvide!=null) {
                        rbStore.isVisible = true
                        tvRating.isVisible = true
                    }
                    else {
                        rbStore.isVisible = false
                        tvRating.isVisible = false
                    }
                    tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                }
                "2.2" ->{
                    tvPickupAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvDropAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                        dropLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                    }
                    if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                        dropLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                    }

                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                        storeLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                    }
                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                        storeLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                    }
                }
                else ->{
                    tvDropAddress.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                    tvPickupAddress.text = deliveryDetailData?.order?.deliveryDetails?.address
                    if (deliveryDetailData?.order?.deliveryDetails?.latitude!=0.0){
                        storeLat = deliveryDetailData?.order?.deliveryDetails?.latitude?:0.0
                    }
                    if (deliveryDetailData?.order?.deliveryDetails?.longitude!=0.0){
                        storeLng = deliveryDetailData?.order?.deliveryDetails?.longitude?:0.0
                    }

                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let!=0.0){
                        dropLat = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.let?:0.0
                    }
                    if (deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long !=0.0){
                        dropLng = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.long?:0.0
                    }

                    ivImage.load(deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                        crossfade(true)
                        placeholder(R.drawable.ic_user)
                        error(R.drawable.ic_user)
                    }
                    rbStore.isVisible = true
                    tvRating.isVisible = true
                    tvName.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeName
                    tvOngoingDeliveries.isVisible = false
                    tvStatus.isVisible = false
                    tvUpdateStatus.isVisible = false
                    btnPickedOrder.isVisible = true
                    clPickUp.isVisible = true
                }
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            btnPickedOrder.setOnClickListener {
                pickOrder()
            }

            viewPackageDetail.setOnClickListener {
                if (deliveryDetailData!=null){
                    viewPackageDetail()
                }
                else{
                    getOrderDetail()
                }

            }

            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivCall.setOnClickListener {
                if (deliveryData?.deliveryStatus=="7"){
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${deliveryData?.order?.deliveryDetails?.countryCode+deliveryData?.order?.deliveryDetails?.mobile}")
                    }
                    startActivity(intent)
                }
                else{
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${deliveryData?.productDetails?.sellerDetails?.storeInfo?.countryCode+deliveryData?.productDetails?.sellerDetails?.storeInfo?.storePhone}")
                    }
                    startActivity(intent)
                }

            }

            tvStatus.setOnClickListener {
                updateDeliveryStatusDialog()
            }

            tvUpdateStatus.setOnClickListener {
                if (selectedDeliveryStatus!=""){
                    if (selectedDeliveryStatus=="8"){
                        cancelOrderDialog()
                    }
                   else if (selectedDeliveryStatus=="3"){
                        if (deliveryData?.returnType=="1"){
                            returnOrderToStore(false)
                            updateDeliveryStatus()
                        }
                        else if (deliveryData?.pOrderdetailsId!="0"&&deliveryData?.reType!="0"&&deliveryData?.returnType!="1"){
                            if (itemCollected){
                                returnOrderToStore()
                            }
                            else{
                                updateDeliveryStatus()
                            }

                        }
                        else{
                            updateDeliveryStatus()
                        }
                    }
                    else {
                        updateDeliveryStatus()

                    }
                }
            }

            ivPickupNavigate.setOnClickListener {
                openGoogleMap(storeLat,storeLng)
            }

            ivDropNavigate.setOnClickListener {
                openGoogleMap(dropLat,dropLng)
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

        viewModel.getReasonResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    reasonList.clear()
                    reasonList.addAll(response.data)
                    reasonList.add(
                        GetReasonResponse.Data(
                            reasonEnglish = "Other"
                        )
                    )

                    reasonAdapter?.notifyDataSetChanged()
                }
                isObserver = false
            }
        }

        viewModel.pickOrderResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                deliveryDetailData?.deliveryStatus = response.data.deliveryStatus
                response?.let {
                        CommonMethod.showSuccessDialog(requireActivity(),"Order Picked successfully"){
                            setData()
                        }

                }
                isObserver = false
            }
        }

        viewModel.orderDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryDetailData = response.data
                    itemCollected = deliveryDetailData?.returntostore=="0"&&deliveryDetailData?.deliveryStatus=="3"
                    setData()
                }
                isObserver = false
            }
        }

        viewModel.updateDeliveryStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {
                    deliveryDetailData?.deliveryStatus = response.data.deliveryStatus
                    deliveryDetailData?.returntostore = response.data.returntostore
                    when(selectedDeliveryStatus){
                      "3" ->{
                          if (deliveryData?.returnType=="1"){

                          }
                         else if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"&&deliveryDetailData?.returnType!="1"){
                              itemCollected = deliveryDetailData?.returntostore=="0"&&deliveryDetailData?.deliveryStatus=="3"
                              productCollectedDialog()
                          }
                          else {
                              if (deliveryData?.order?.paymentMethod == "3" && deliveryData?.paymentStatus != "1") {
                                  collectAmountDialog()
                              } else {
                                  CommonMethod.showSuccessDialog(
                                      requireActivity(),
                                      "Order Delivered successfully"
                                  ) {
                                      findNavController().popBackStack()
                                  }
                              }
                          }

                      }

                        "8"->{
                            cancelDialog?.dismiss()
                            CommonMethod.showSuccessDialog(requireActivity(),"Order Status Updated as Not Delivered"){
                                findNavController().popBackStack()
                            }
                        }
                    }

                }
                isObserver = false
            }
        }

        viewModel.returnOrderResult.observe(viewLifecycleOwner) { response ->
            if (isReturnObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                     if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"&&deliveryDetailData?.returnType!="1"){
                         CommonMethod.showSuccessDialog(requireActivity(),"Exchange Complete Successfully "){
                             findNavController().popBackStack()
                         }
                     }
                    else{
                         CommonMethod.showSuccessDialog(requireActivity(),response.message){
                             findNavController().popBackStack()
                         }
                     }


                }
                isReturnObserver = false
            }
        }
    }

    private fun setDataAfterPickUp() {
        binding?.apply {
            tvOngoingDeliveries.isVisible = true
            tvStatus.isVisible = true
            tvUpdateStatus.isVisible = true
            btnPickedOrder.isVisible = false
            clPickUp.isVisible = false
        }
    }

    private fun viewPackageDetail() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = PackageDetailDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            ivOrder.load(deliveryDetailData?.variationDetails?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvPriceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.order?.totalAmount?:0.0) } KD"
            tvSize.text = "Size: ${deliveryDetailData?.variationDetails?.size?.name}"
            deliveryDetailData?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(
                Color.parseColor(it)) }
            tvQuantityValue.text = "${deliveryDetailData?.quantity}"
            tvStore.text = deliveryDetailData?.productDetails?.productNameEn


            tvPaymentStatus.text = if (deliveryDetailData?.paymentStatus=="1"){
                "Paid"
            }
            else{
                if (deliveryDetailData?.order?.paymentMethod=="3"){
                    "COD"
                }
                else {
                    "Pending"
                }
            }
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")

            tvCustomerName.text = deliveryDetailData?.order?.deliveryDetails?.name
            tvOrderIdValue.text = "#${deliveryDetailData?.id}"

            if(deliveryData?.returnType=="1"){
                tvPriceValue.isVisible = false
                tvPrice.isVisible = false
                tvPaymentStatus.isVisible = false
                tvDeliveryLocation.text = deliveryData?.productDetails?.sellerDetails?.storeInfo?.storeAddress


                if (deliveryDetailData?.returnImage!=null&&deliveryDetailData?.returnImage?.size!=0){
                    clUploadedImage.isVisible = true
                    tvReturn.isVisible = true
                    ivReturnImage.load(deliveryDetailData?.returnImage?.get(0)){
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder)
                        error(R.drawable.ic_placeholder)
                    }
                }
                else{
                    clUploadedImage.isVisible = false
                    tvReturn.isVisible = false
                }

            }
            else if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"){
                tvDeliveryLocation.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
                tvPriceValue.isVisible = false
                tvPrice.isVisible = false
                tvPaymentStatus.isVisible = false
                tvExchange.isVisible = true
                clExchangeSize.isVisible = true
                tvExchangeSizeValue.text = deliveryDetailData?.exchangeNewSizeData?.name

            }
            else if(deliveryDetailData?.returnType=="2"){
                tvDeliveryLocation.text = deliveryDetailData?.productDetails?.sellerDetails?.storeInfo?.storeAddress

                tvExchange.isVisible = true
                clExchangeSize.isVisible = true
                tvExchangeSizeValue.text = deliveryDetailData?.exchangeNewSizeData?.name
            }
            else{
                tvDeliveryLocation.text = deliveryDetailData?.order?.deliveryDetails?.address
            }

        }

        dialog.show()
    }

    private fun updateDeliveryStatusDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = UpdateDeliveryStatusDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            if(deliveryDetailData?.returnType=="1"||deliveryDetailData?.returnType=="2"){
                rbNotDelivered.isVisible = false
                rbDelivered.text = "Drop at Store"
            }
            else if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"){
                if (deliveryDetailData?.deliveryStatus=="3"){
                 rbDelivered.text = "Drop at Store"
                    rbPicked.isVisible = false
                }

                rbNotDelivered.isVisible = false
            }
            else{
                rbDelivered.text = "Delivered"
                rbNotDelivered.isVisible = true
            }

            ivBack.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
               when (checkedId) {
                    R.id.rbPicked ->{
                        binding?.tvStatus?.text = "Picked"
                        selectedDeliveryStatus = "7"
                        binding?.tvUpdateStatus?.setBackgroundResource(R.drawable.btn_gradient)
                        binding?.tvUpdateStatus?.setTextColor(requireActivity().getColor(R.color.white))
                        dialog.dismiss()
                    }
                    R.id.rbDelivered ->{
                        if(deliveryDetailData?.returnType=="1"||deliveryDetailData?.returnType=="2"){
                            binding?.tvStatus?.text = "Drop at Store"
                        }
                        if (deliveryDetailData?.pOrderdetailsId!="0"&&deliveryDetailData?.reType!="0"){
                            if (deliveryDetailData?.deliveryStatus=="3"){
                                binding?.tvStatus?.text = "Drop at Store"
                            }
                            else{
                                binding?.tvStatus?.text = "Delivered"
                            }
                        }
                        else{
                            binding?.tvStatus?.text = "Delivered"
                        }
                        selectedDeliveryStatus = "3"
                        binding?.tvUpdateStatus?.setBackgroundResource(R.drawable.btn_gradient)
                        binding?.tvUpdateStatus?.setTextColor(requireActivity().getColor(R.color.white))
                        dialog.dismiss()
                    }
                    R.id.rbNotDelivered -> {
                        binding?.tvStatus?.text = "Not Delivered"
                        selectedDeliveryStatus = "8"
                        binding?.tvUpdateStatus?.setBackgroundResource(R.drawable.btn_gradient)
                        binding?.tvUpdateStatus?.setTextColor(requireActivity().getColor(R.color.white))
                        dialog.dismiss()
                    }
                    else -> ""
                }

            }

        }

        dialog.show()
    }

    private fun pickOrder() {
        val map = HashMap<String,Any>()
        map["id"] = deliveryData?.id.toString()
        viewModel.pickOrder(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }


    private fun updateDeliveryStatus(reason:String?=null) {
        val map = HashMap<String,Any>()
        map["id"] = deliveryData?.id.toString()
        map["delivery_status"] = selectedDeliveryStatus////3-delivered 7-picked 8-not delivered
        if (selectedDeliveryStatus=="3"&&deliveryData?.order?.paymentMethod=="3"&&deliveryData?.paymentStatus!="1"){
            map["collect_amount"] = "1"
        }
        if (reason!=null){
            map["reason"] = reason
        }
        viewModel.updateDeliveryStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getOrderDetail() {
        val map = HashMap<String,Any>()
        map["id"] = deliveryData?.id.toString()
        viewModel.orderDetail(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun getReason(type:Int) {
        viewModel.getReason(type)////1-exchange,2-return,3-cancel
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun cancelOrderDialog() {
        var reason = ""
        cancelDialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = CancelDeliveryDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { cancelDialog?.setContentView(it) }

        val window: Window = cancelDialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        cancelDialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {
            reasonList.clear()
            reasonAdapter = ReasonAdapter(requireActivity(), reasonList,object :
                ReasonAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    if (position==reasonList.size-1){
                        etReason.isVisible = true
                        reason = ""
                    }
                    else{
                        etReason.isVisible = false
                        reason = reasonList[position].reasonEnglish.toString()
                    }

                }

            })
            rvReason.adapter = reasonAdapter

            getReason(4)

            tvToolbar.setOnClickListener {
                cancelDialog?.dismiss()
            }


            btnSubmit.setOnClickListener {
                if (etReason.isVisible){
                    if (etReason.text?.trim().toString().isEmpty()){
                        showToast("Please enter reason")
                    }
                    else{
                        reason = etReason.text?.trim().toString()
                        updateDeliveryStatus(reason)
                    }
                }
                else if(reason==""){
                    showToast("Please select reason")
                }
                else {
                    updateDeliveryStatus(reason)
                }
            }
        }

        cancelDialog?.show()
    }

    private fun openGoogleMap(lat: Double, lng: Double) {
        val gmmIntentUri =
            Uri.parse("google.navigation:q=$lat,$lng")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        try {
            startActivity(mapIntent)
        } catch (e: Exception) {
            val browserUri =
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
            startActivity(Intent(Intent.ACTION_VIEW, browserUri))
        }
    }


    private fun drawRoute(origin: LatLng, destination: LatLng) {

        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=AIzaSyA4L_dBeJDWuqtBT9ZOOUhjigbh6Lxi43s"

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                URL(url).readText()
            }

            val json = JSONObject(result)
            val routes = json.getJSONArray("routes")
            if (routes.length() > 0) {
                val overviewPolyline =
                    routes.getJSONObject(0)
                        .getJSONObject("overview_polyline")
                        .getString("points")

                val points = decodePolyline(overviewPolyline)

                googleMap.addPolyline(
                    PolylineOptions()
                        .addAll(points)
                        .color(Color.parseColor("#00C2D1"))
                        .width(5f)
                )

            }

            googleMap.addMarker(
                MarkerOptions()
                    .position(origin)
                    .title("Pickup Location")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_pickup_pin))
            )

            googleMap.addMarker(
                MarkerOptions()
                    .position(destination)
                    .title("Drop Location")
                    .icon(bitmapDescriptorFromVector(R.drawable.ic_drop_pin))
            )
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(origin, 12f), 1500, null)
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 12f))
        }
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }

    private fun tryDrawRoute() {
        if (!isMapReady || !isLocationReady) return
        val origin = LatLng(storeLat, storeLng)
        val destination = LatLng(dropLat, dropLng)
        drawRoute(origin, destination)
    }

    private fun bitmapDescriptorFromVector(resId: Int): BitmapDescriptor {
        val drawable = requireContext().getDrawable(resId)!!
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun collectAmountDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = PriceCollectDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            tvPrice.text = "${CommonMethod.formatPrice(deliveryData?.order?.totalAmount?:0.0)} KD"

            btnCollected.setOnClickListener {
                CommonMethod.showSuccessDialog(requireActivity(),"Order Delivered Successfully"){
                    findNavController().popBackStack()
                }

                dialog.dismiss()
            }

        }

        dialog.show()
    }

    private fun productCollectedDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = ExchangeReturnProductDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivOrder.load(deliveryDetailData?.variationDetails?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
            tvSize.text = "Size: ${deliveryDetailData?.variationDetails?.size?.name}"
            deliveryDetailData?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(
                Color.parseColor(it)) }
            tvQuantityValue.text = "${deliveryDetailData?.quantity}"
            tvStore.text = deliveryDetailData?.productDetails?.productNameEn
            btnCollected.setOnClickListener {
                CommonMethod.showSuccessDialog(
                    requireActivity(),
                    "Order Delivered successfully"
                ) {

                }

binding?.apply {
    tvDropAddress.text = deliveryData?.productDetails?.sellerDetails?.storeInfo?.storeAddress
    tvPickupAddress.text = deliveryData?.order?.deliveryDetails?.address
    if (deliveryData?.order?.deliveryDetails?.latitude != 0.0) {
        storeLat = deliveryData?.order?.deliveryDetails?.latitude ?: 0.0
    }
    if (deliveryData?.order?.deliveryDetails?.longitude != 0.0) {
        storeLng = deliveryData?.order?.deliveryDetails?.longitude ?: 0.0
    }

    if (deliveryData?.productDetails?.sellerDetails?.storeInfo?.let != 0.0) {
        dropLat = deliveryData?.productDetails?.sellerDetails?.storeInfo?.let ?: 0.0
    }
    if (deliveryData?.productDetails?.sellerDetails?.storeInfo?.long != 0.0) {
        dropLng = deliveryData?.productDetails?.sellerDetails?.storeInfo?.long ?: 0.0
    }
    binding?.tvUpdateStatus?.setBackgroundResource(R.drawable.ic_update_status_btn)
    binding?.tvUpdateStatus?.setTextColor(Color.parseColor("#808080"))
    selectedDeliveryStatus = ""
    itemCollected = true
}
                dialog.dismiss()
}
            }

        dialog.show()
    }

    private fun returnOrderToStore(isLoading:Boolean = true) {
        val map = HashMap<String,Any>()
        map["id"] = deliveryData?.id.toString()
        map["delivery_status"] = "1"
        viewModel.returnOrder(map)
        if (isLoading){
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isReturnObserver = true
    }

}