package com.desynatdriver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdriver.R
import com.desynatdriver.databinding.ItemReasonBinding
import com.desynatdriver.ui.home.fragment.deliveries.response.GetReasonResponse

class ReasonAdapter(var context:Context, var reasonList: ArrayList<GetReasonResponse.Data>, var onClick: OnClick) : RecyclerView.Adapter<ReasonAdapter.ViewHolder>() {
    var isSelected = -1
    interface OnClick{
        fun onClickItem(position: Int)
    }
    inner class ViewHolder(val binding: ItemReasonBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReasonBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return reasonList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {

            tvReason.text = reasonList[position].reasonEnglish

            if (isSelected==position){
                ivImage.setImageResource(R.drawable.ic_radio_selected)
            }
            else{
                ivImage.setImageResource(R.drawable.ic_radio_unselected)
            }
        }

        holder.itemView.setOnClickListener {
            val previousSelected = isSelected
            isSelected = position

            if (previousSelected != -1) {
                notifyItemChanged(previousSelected)
            }
            notifyItemChanged(position)

            onClick.onClickItem(position)

        }

    }


}
