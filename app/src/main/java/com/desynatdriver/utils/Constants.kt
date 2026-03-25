package com.desynatdriver.utils

object Constants {

 // const val BASE_URL = "http://157.241.119.72" //old
 //const val BASE_URL = "http://65.2.12.223" //new
    const val BASE_URL = "https://desynat.com"

    var language="en"
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var isLogout=false
    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=2

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
    const val RETURN_ORDER_TO_STORE = "/api/deliveryboy/return-order-to-store-in-exchange"
    const val NOTIFICATION = "/api/deliveryboy/get_notifications_list"
    const val NOTIFICATION_COUNT = "/api/deliveryboy/get_notifications_count"
    const val READ_NOTIFICATION = "/api/deliveryboy/mark_notification_as_read"
    const val READ_All_NOTIFICATION = "/api/deliveryboy/driver_mark_all_notification_as_read"

}