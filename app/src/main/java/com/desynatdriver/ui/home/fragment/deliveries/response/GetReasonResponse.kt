package com.desynatdriver.ui.home.fragment.deliveries.response


import com.google.gson.annotations.SerializedName

data class GetReasonResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("reason_arabic")
        val reasonArabic: String,
        @SerializedName("reason_english")
        val reasonEnglish: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}