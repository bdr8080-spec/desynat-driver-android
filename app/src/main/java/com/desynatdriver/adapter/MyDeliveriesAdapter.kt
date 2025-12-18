package com.desynatdriver.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdriver.databinding.ItemMyDeliveriesBinding

class MyDeliveriesAdapter(var context:Context, var list: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<MyDeliveriesAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            var status = ""
            when(status){
                "1"->{
                    tvStatus.text = "Confirmed"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "2"->{
                    tvStatus.text = "Picked"
                    tvStatus.setTextColor(Color.parseColor("#2A74B8"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "3"->{
                    tvStatus.text = "Delivered"
                    tvStatus.setTextColor(Color.parseColor("#17854C"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBFFF5"))
                }
                "4"->{
                    tvStatus.text = "Not Delivered"
                    tvStatus.setTextColor(Color.parseColor("#EB2525"))
                    tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFEAEA"))
                }
            }

        }
        holder.itemView.setOnClickListener{

        }

    }
}
