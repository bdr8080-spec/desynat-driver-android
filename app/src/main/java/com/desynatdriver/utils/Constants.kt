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
    const val GET_KUWAIT_ZONE = "/api/deliveryboy/get_kuwait_zones"

    const val GET_PROFILE = "/api/deliveryboy/get_profile_details"
    const val UPDATE_PROFILE = "/api/deliveryboy/update_profile"
    const val DUTY_ON_OFF = "/api/deliveryboy/duty_on_off"

    const val GET_ALL_ORDER = "/api/deliveryboy/get_all_orders"
    const val GET_NEW_ORDER = "/api/deliveryboy/get_new_orders"
    const val GET_ONGOING_ORDER = "/api/deliveryboy/get_ongoing_orders"
    const val ORDER_DETAIL = "/api/deliveryboy/order_details"
    const val UPDATE_DELIVERY_STATUS = "/api/deliveryboy/update_delivery_status"
    const val PICK_ORDER = "/api/deliveryboy/pick_order"
    const val GET_COMPLETED_ORDER = "/api/deliveryboy/get_completed_orders"
    const val GET_REASON = "/api/deliveryboy/get_reasons"

}