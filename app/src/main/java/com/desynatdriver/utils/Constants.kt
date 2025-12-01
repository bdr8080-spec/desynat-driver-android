package com.desynatdriver.utils

object Constants {

    const val BASE_URL = "http://157.241.119.72"

    var language="en"
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var isLogout=false
    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=1

    const val UPLOAD = "/api/upload"
    const val LOGIN = "/api/deliveryboy/login"
    const val OTP_VERIFY = "/api/deliveryboy/verify_otp"
    const val RESEND_OTP = "/api/deliveryboy/resend_otp"
    const val FORGOT_PASSWORD = "/api/deliveryboy/forgot_password"
    const val CREATE_PASSWORD = "/api/deliveryboy/create_password"
    const val DASHBOARD = "/api/deliveryboy/delivery_boy_dashboard"
    const val LOGOUT = "/api/deliveryboy/logout"
    const val GET_COUNTRY = "/api/admin/get_countries"
    const val GET_PROFILE = "/api/deliveryboy/delivery_boy_profile"
    const val GET_KUWAIT_ZONE = "/api/deliveryboy/get_kuwait_zones"

}