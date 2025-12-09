package com.desynatdriver.ui.home.response


import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("address_line1")
    val addressLine1: String,
    @SerializedName("address_line2")
    val addressLine2: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("delivery_boy_id")
    val deliveryBoyId: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name_arabic")
    val fullNameArabic: String,
    @SerializedName("full_name_english")
    val fullNameEnglish: String,
    @SerializedName("let")
    val let: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("zone_id")
    val zoneId: String
)