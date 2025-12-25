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
import android.widget.Toast
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
    private var selectedDeliveryStatus: String = ""
    var deliveryData: GetAllOrderResponse.Data.Data?=null
    var deliveryDetailData: DeliveryDetailResponse.Data?=null
    var storeLat = 30.7333
    var storeLng = 76.7794
    var dropLat = 28.6139
    var dropLng = 77.2090
    private var cancelDialog: Dialog?=null
    private var reasonList = ArrayList<GetReasonResponse.Data>()
    private var reasonAdapter: ReasonAdapter? = null

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
            setData()
        }
        binding?.apply {
        }
    }

    private fun setData(){

       /* if (deliveryData?.deliveryDetails?.latitude!=0.0){
            dropLat = deliveryData?.deliveryDetails?.latitude?:0.0
        }
        if (deliveryData?.deliveryDetails?.longitude!=0.0){
            dropLng = deliveryData?.deliveryDetails?.longitude?:0.0
        }*/
        if (storeLat != 0.0 && dropLat != 0.0) {
            isLocationReady = true
            tryDrawRoute()
        }
        binding?.apply {
            ivImage.load(deliveryData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvName.text = deliveryData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storeName
            tvPickupAddress.text = deliveryData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storeAddress
            tvDropAddress.text = deliveryData?.deliveryDetails?.address

           when(deliveryData?.deliveryStatus){
               "7"->{
                   setDataAfterPickUp()
               }
               else->{
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
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${deliveryData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.countryCode+deliveryData?.orderDetails?.get(0)?.productDetails?.sellerDetails?.storeInfo?.storePhone}")
                }
                startActivity(intent)
            }

            tvStatus.setOnClickListener {
                updateDeliveryStatusDialog()
            }

            tvUpdateStatus.setOnClickListener {
                if (selectedDeliveryStatus!=""){
                    if (selectedDeliveryStatus=="8"){
                        cancelOrderDialog()
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
                response?.let {
                    CommonMethod.showSuccessDialog(requireActivity(),"Order Picked successfully"){
                        setDataAfterPickUp()
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
                    viewPackageDetail()
                }
                isObserver = false
            }
        }

        viewModel.updateDeliveryStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                response?.let {

                    when(selectedDeliveryStatus){
                      "3" ->{
                          CommonMethod.showSuccessDialog(requireActivity(),"Order Delivered successfully"){
                              if (deliveryData?.paymentMethod=="3"){
                                  collectAmountDialog()
                              }
                              else{
                                  findNavController().popBackStack()
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

            ivOrder.load(deliveryDetailData?.orderDetails?.get(0)?.variationDetails?.variantImage?.get(0)){
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

            tvPaymentStatus.text = if (deliveryDetailData?.paymentStatus=="1"){
                "Paid"
            }
            else{
                "Pending"
            }
            tvOrderDateTime.text = CommonMethod.customizeChangeDateFormat(deliveryDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy 'at' hh:mm a")

            tvCustomerName.text = deliveryDetailData?.deliveryDetails?.name
            tvOrderIdValue.text = "#${deliveryDetailData?.id}"
            tvDeliveryLocation.text = deliveryDetailData?.deliveryDetails?.address
            tvPriceValue.text = "${CommonMethod.formatPrice(deliveryDetailData?.totalAmount?:0.0) } KD"
            tvSize.text = "Size: ${deliveryDetailData?.orderDetails?.get(0)?.variationDetails?.size?.name}"
            deliveryDetailData?.orderDetails?.get(0)?.variationDetails?.color?.hexCode?.let { ivColor.setColorFilter(
                Color.parseColor(it)) }
            tvQuantityValue.text = "Size: ${deliveryDetailData?.orderDetails?.get(0)?.quantity}"
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
                        binding?.tvStatus?.text = "Delivered"
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
                        reason = reasonList[position].reasonEnglish.toString()
                    }
                    else{
                        etReason.isVisible = false
                        reason = ""
                    }

                }

            })
            rvReason.adapter = reasonAdapter

            getReason(3)

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
        val uri = Uri.parse("google.navigation:q=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Google Maps not installed", Toast.LENGTH_SHORT).show()
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

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 7f))
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

            tvPrice.text = "${CommonMethod.formatPrice(deliveryData?.totalAmount?:0.0)} KD"

            btnCollected.setOnClickListener {
                findNavController().popBackStack()
                dialog.dismiss()
            }

        }

        dialog.show()
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



}