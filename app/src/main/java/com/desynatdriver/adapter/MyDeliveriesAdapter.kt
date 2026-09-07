package com.desynatdriver.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatdriver.R
import com.desynatdriver.databinding.ItemMyDeliveriesBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants

class MyDeliveriesAdapter(var context:Context, var list: ArrayList<GetAllOrderResponse.Data.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<MyDeliveriesAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int,type:Int) //1-call
    }

    inner class ViewHolder(val binding: ItemMyDeliveriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyDeliveriesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var deliveryData = list[position]
        holder.binding.apply {
            tvDate.text = CommonMethod.customizeChangeDateFormat(deliveryData?.createdAt.toString(), outputFormat = "dd MMM yyyy, hh:mm a")
            ivImage.load(deliveryData?.productDetails?.sellerDetails?.storeInfo?.storeImage){
                crossfade(true)
                placeholder(R.drawable.ic_user)
                error(R.drawable.ic_user)
            }
            tvName.text = deliveryData?.productDetails?.sellerDetails?.storeInfo?.storeName
            tvOrderId.text = "#${deliveryData?.id}"
            tvDistance.text = "${CommonMethod.formatPrice(deliveryData?.distance?:0.0)} ${context.getString(R.string.km)}"
            tvAmount.text = "${CommonMethod.formatPrice(deliveryData?.order?.totalAmount?:0.0) } ${
                context.getString(
                    R.string.kd
                )
            }"

            when(deliveryData.deliveryStatus){
                "0"->{
                    if(deliveryData?.returnType=="1"){
                    tvStatus.text = context.getString(R.string.return_req)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                else if(deliveryData?.returnType=="2"){
                    tvStatus.text = context.getString(R.string.exchange_req)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                }
                "1"->{
                    if (deliveryData?.rEApprove=="1"&&deliveryData?.returnType=="1"){
                        tvStatus.text = context.getString(R.string.return_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                   else if (deliveryData?.pOrderdetailsId!="0"&&deliveryData?.reType!="0"){
                        tvStatus.text = context.getString(R.string.exchange_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                    else{
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                    }

                }
                "2.1"->{
                    if (deliveryData?.rEApprove=="1"&&deliveryData?.returnType=="1"){
                        tvStatus.text = context.getString(R.string.return_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                    else if (deliveryData?.pOrderdetailsId!="0"&&deliveryData?.reType!="0"){
                        tvStatus.text = context.getString(R.string.exchange_req)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                    else {
                        tvStatus.text = context.getString(R.string.confirmed)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                        tvStatus.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                    }
                }
                "2.2"->{
                    tvStatus.text = context.getString(R.string.exchange_req)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                "7"->{
                    tvStatus.text = context.getString(R.string.picked)
                    tvStatus.setTextColor(Color.parseColor("#2A74B8"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "3"->{
                    if (deliveryData?.pOrderdetailsId!="0"&&deliveryData?.returntostore=="0"){
                        tvStatus.text = context.getString(R.string.ongoing)
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                    else{
                        tvStatus.text = context.getString(R.string.delivered)
                        tvStatus.setTextColor(Color.parseColor("#17854C"))
                        tvStatus.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                    }
                }
                "8"->{
                    tvStatus.text = context.getString(R.string.not_delivered)
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }

                "5"->{
                    tvStatus.text = context.getString(R.string.return_req)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                "9"->{
                    tvStatus.text = context.getString(R.string.exchange_req)
                    tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                else ->{
                    tvStatus.text = context.getString(R.string.confirmed)
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
            }

        }
        holder.itemView.setOnClickListener{
            clickOnItem.onClickItem(position,2)
        }

    }
}
