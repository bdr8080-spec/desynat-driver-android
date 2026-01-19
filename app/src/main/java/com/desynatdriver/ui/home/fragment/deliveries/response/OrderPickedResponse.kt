package com.desynatdriver.ui.home.fragment.deliveries.response


import com.google.gson.annotations.SerializedName

data class OrderPickedResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("admin_commission")
        val adminCommission: String,
        @SerializedName("amount")
        val amount: String,
        @SerializedName("assign_date")
        val assignDate: String,
        @SerializedName("assign_status")
        val assignStatus: String,
        @SerializedName("cancel_reason")
        val cancelReason: Any,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("delivery_boy_id")
        val deliveryBoyId: String,
        @SerializedName("delivery_status")
        val deliveryStatus: String,
        @SerializedName("exchange_new_size")
        val exchangeNewSize: Any,
        @SerializedName("exchange_old_size")
        val exchangeOldSize: Any,
        @SerializedName("exchange_reason")
        val exchangeReason: Any,
        @SerializedName("giftcard_amount")
        val giftcardAmount: Any,
        @SerializedName("giftcard_id")
        val giftcardId: Any,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_picked")
        val isPicked: String,
        @SerializedName("not_delivered_reason")
        val notDeliveredReason: Any,
        @SerializedName("order_id")
        val orderId: Int,
        @SerializedName("order_status")
        val orderStatus: Any,
        @SerializedName("p_orderdetails_id")
        val pOrderdetailsId: String,
        @SerializedName("payment_status")
        val paymentStatus: String,
        @SerializedName("product_id")
        val productId: Int,
        @SerializedName("promocode_id")
        val promocodeId: Any,
        @SerializedName("promocode_value")
        val promocodeValue: Any,
        @SerializedName("quantity")
        val quantity: Int,
        @SerializedName("r_e_approve")
        val rEApprove: String,
        @SerializedName("r_e_type")
        val rEType: String,
        @SerializedName("return_image")
        val returnImage: Any,
        @SerializedName("return_reason")
        val returnReason: Any,
        @SerializedName("return_type")
        val returnType: String,
        @SerializedName("returntostore")
        val returntostore: String,
        @SerializedName("seller_id")
        val sellerId: String,
        @SerializedName("shipping_fees")
        val shippingFees: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("tax")
        val tax: String,
        @SerializedName("unique_id")
        val uniqueId: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("variants_id")
        val variantsId: Int
    )
}