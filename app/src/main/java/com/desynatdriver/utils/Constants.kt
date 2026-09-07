package com.desynatdriver.utils

object Constants {

 // const val BASE_URL = "http://157.241.119.72" //old
 //const val BASE_URL = "http://65.2.12.223" //new
//    const val BASE_URL = "https://desynat.com"
    const val BASE_URL = "https://desynat.com.kw/"

    var language="ar"
    const val ENGLISH = "en"
    const val ARABIC = "ar"

    var isLogout=false
    var location=""
    var latitude=0.0
    var longitude=0.0
    var DEVICE_TYPE=2

    const val UPLOAD = "/api/uploadFile"
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


   const val GET_TICKET_TYPE = "/api/ticket_types"
   const val GET_RAISED_TICKET = "/api/deliveryboy/get_raised_tickets"
   const val RAISE_TICKET = "/api/deliveryboy/raise_ticket"
   const val NOTIFICATION_ON_OFF = "/api/deliveryboy/notification_on_off"
   const val APP_NOTIFICATION_ON_OFF = "/api/deliveryboy/app_notification_on_off"
   const val CONTACT_US = "/api/contact_us/"
   const val FAQ = "/api/faqs"
   const val TERMS_CONDITION = "/api/terms_conditions"
   const val ABOUT_US = "/api/about_us"
   const val PRIVACY_POLICY = "/api/terms_conditions"
   const val RETURN_POLICY = "/api/return_policy"
   const val EXCHANGE_POLICY = "/api/exchange_policy"
   const val CANCELLATION_POLICY = "/api/cancellation_policy"
   const val DELETE_ACCOUNT = "/api/deliveryboy/delete_account"


}