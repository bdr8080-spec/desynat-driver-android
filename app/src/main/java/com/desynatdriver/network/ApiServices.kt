package com.desynatdriver.network

import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.fragment.deliveries.DeliveryDetailFragment
import com.desynatdriver.ui.home.fragment.deliveries.response.DeliveryDetailResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetReasonResponse
import com.desynatdriver.ui.home.response.DutyOnOffResponse
import com.desynatdriver.ui.home.response.GetKuwaitZoneResponse
import com.desynatdriver.ui.home.response.GetNotificationCountResponse
import com.desynatdriver.ui.home.response.GetNotificationResponse
import com.desynatdriver.ui.home.response.UpdateProfileRequest
import com.desynatdriver.ui.response.CommonResponse
import com.desynatdriver.ui.response.GetProfileResponse
import com.desynatdriver.utils.Constants
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiServices {

    @Multipart
    @POST(Constants.UPLOAD)
    suspend fun uploadImage(
        @Part file: MultipartBody.Part? = null
    ): Response<FileUploadResponse>

    @POST(Constants.LOGIN)
    suspend fun login(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.OTP_VERIFY)
    suspend fun otpVerification(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.RESEND_OTP)
    suspend fun resendOtp(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.CREATE_PASSWORD)
    suspend fun createPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

 @GET(Constants.DASHBOARD)
    suspend fun getDashBoard(
    ): Response<GetProfileResponse>


 @GET(Constants.GET_PROFILE)
    suspend fun getProfile(
    ): Response<GetProfileResponse>

 @GET(Constants.LOGOUT)
    suspend fun logout(
    ): Response<CommonResponse>

    @GET(Constants.GET_COUNTRY)
    suspend fun getCountry(
    ): Response<GetCountryResponse>

    @GET(Constants.GET_KUWAIT_ZONE)
    suspend fun getKuwaitZone():Response<GetKuwaitZoneResponse>

    @POST(Constants.UPDATE_PROFILE)
    suspend fun editProfile(@Body request: UpdateProfileRequest):Response<GetProfileResponse>

    @POST(Constants.DUTY_ON_OFF)
    suspend fun dutyOnOff(
        @Body body: HashMap<String, Any>
    ): Response<DutyOnOffResponse>

    @GET(Constants.GET_ALL_ORDER)
    suspend fun getAllOrder(@QueryMap map: HashMap<String,Any>):Response<GetAllOrderResponse>

    @GET(Constants.GET_NEW_ORDER)
    suspend fun getNewOrder(@QueryMap map: HashMap<String,Any>):Response<GetAllOrderResponse>

    @GET(Constants.GET_ONGOING_ORDER)
    suspend fun getOngoingOrder(@QueryMap map: HashMap<String,Any>):Response<GetAllOrderResponse>

    @GET(Constants.GET_COMPLETED_ORDER)
    suspend fun getCompletedOrder(@QueryMap map: HashMap<String,Any>):Response<GetAllOrderResponse>

    @GET(Constants.ORDER_DETAIL)
    suspend fun orderDetail(@QueryMap map: HashMap<String, Any>):Response<DeliveryDetailResponse>

    @POST(Constants.PICK_ORDER)
    suspend fun pickOrder(@Body body: HashMap<String, Any>):Response<DeliveryDetailResponse>

    @POST(Constants.UPDATE_DELIVERY_STATUS)
    suspend fun updateDeliveryStatus(@Body body: HashMap<String, Any>):Response<DeliveryDetailResponse>

    @POST(Constants.RETURN_ORDER_TO_STORE)
    suspend fun returnOrder(@Body body: HashMap<String, Any>):Response<CommonResponse>
    @GET(Constants.GET_REASON)
    suspend fun getReason(@Query("type") type:Int):Response<GetReasonResponse>

    @GET(Constants.NOTIFICATION)
    suspend fun getNotification():Response<GetNotificationResponse>

    @GET(Constants.NOTIFICATION_COUNT)
    suspend fun notificationCount():Response<GetNotificationCountResponse>

    @POST(Constants.READ_NOTIFICATION)
    suspend fun readNotification(@Body body: HashMap<String, Any>):Response<CommonResponse>

    @POST(Constants.READ_All_NOTIFICATION)
    suspend fun readAllNotification(@Body body: HashMap<String, Any>):Response<CommonResponse>

}