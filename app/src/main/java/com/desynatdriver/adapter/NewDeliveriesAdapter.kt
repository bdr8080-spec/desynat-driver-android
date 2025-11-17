package com.desynatdriver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdriver.databinding.ItemDeliveriesBinding

class NewDeliveriesAdapter(var context:Context, var list: ArrayList<String>,var clickOnItem:ClickOnItem) : RecyclerView.Adapter<NewDeliveriesAdapter.ViewHolder>() {

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

        holder.binding.apply {
            ivCall.setOnClickListener {
                clickOnItem.onClickItem(position,1)
            }
        }
        holder.itemView.setOnClickListener{

        }

    }
}
