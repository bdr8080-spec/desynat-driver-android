package com.desynatdriver.network

import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.response.GetKuwaitZoneResponse
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

}