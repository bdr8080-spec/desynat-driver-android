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
    const val LOGIN = "/api/login"
    const val OTP_VERIFY = "/api/verify_otp"
    const val RESEND_OTP = "/api/resend_otp"
    const val FORGOT_PASSWORD = "/api/forgot_password"
    const val CREATE_PASSWORD = "/api/create_password"
    const val DASHBOARD = "/api/user/user-dashboard"
    const val LOGOUT = "/api/user/logout"

}