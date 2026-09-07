package com.desynatdriver.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatdriver.R
import com.desynatdriver.databinding.ItemNotificationBinding
import com.desynatdriver.ui.home.response.GetNotificationResponse
import com.desynatdriver.utils.CommonMethod
import com.desynatdriver.utils.Constants

class NotificationAdapter(var context:Context, var notificationList: ArrayList<GetNotificationResponse.Data.Data>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val notificationData = notificationList[position]
            tvTitle.text = if (Constants.language=="ar")notificationData?.titleArabic else notificationData?.titleEnglish
            tvDescription.text = if (Constants.language=="ar")notificationData?.descriptionArabic else notificationData?.descriptionEnglish
            tvDate.text = CommonMethod.customizeChangeDateFormat(notificationData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")
            if (notificationData.isRead==1){
                ivBell.setImageResource(R.drawable.icon_read_bell)
                clMain.setBackgroundResource(R.drawable.notification_read_background)
            }
            else{
                ivBell.setImageResource(R.drawable.icon_unread_bell)
                clMain.setBackgroundResource(R.drawable.notification_unread_background)
            }

            root.setOnClickListener {
                if (notificationData.isRead!=1){
                    clickOnItem.onClickItem(position)
                }

            }
        }
        }

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
