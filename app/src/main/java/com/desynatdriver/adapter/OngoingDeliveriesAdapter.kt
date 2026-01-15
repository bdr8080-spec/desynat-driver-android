package com.desynatdriver.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdriver.databinding.ItemDeliveriesBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse

class OngoingDeliveriesAdapter(var context:Context, var list: ArrayList<GetAllOrderResponse.Data.Data>,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<OngoingDeliveriesAdapter.ViewHolder>() {

    interface ClickOnItem{
        fun onClickItem(position: Int,type:Int) //1-call
    }

    inner class ViewHolder(val binding: ItemDeliveriesBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDeliveriesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var deliveryData = list[position]
        holder.binding.apply {

            tvOrderIdValue.text = "#${deliveryData?.id}"
            tvName.text = deliveryData?.productDetails?.sellerDetails?.firstName+" "+deliveryData?.productDetails?.sellerDetails?.lastName
            tvStore.text = deliveryData?.productDetails?.sellerDetails?.storeInfo?.storeName

            when(deliveryData.deliveryStatus){
                "0"->{
                    if(deliveryData?.returnType=="1"){
                        tvStatus.text = "Return Req."
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                    else if(deliveryData?.returnType=="2"){
                        tvStatus.text = "Exchange Req."
                        tvStatus.setTextColor(Color.parseColor("#FF5F1B"))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                    }
                }
                "1"->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "7"->{
                    tvStatus.text = "Picked"
                    tvStatus.setTextColor(Color.parseColor("#2A74B8"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "3"->{
                    tvStatus.text = "Delivered"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "8"->{
                    tvStatus.text = "Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
                else ->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
            }

            ivCall.setOnClickListener {
                clickOnItem.onClickItem(position,1)
            }
        }
        holder.itemView.setOnClickListener{
            clickOnItem.onClickItem(position,2)
        }

    }
}
