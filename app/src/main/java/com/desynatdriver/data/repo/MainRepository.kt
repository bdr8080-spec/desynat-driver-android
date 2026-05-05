package com.desynatdriver.data.repo


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.desynatdriver.network.ApiServices
import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdriver.ui.home.cms.response.GetCmsResponse
import com.desynatdriver.ui.home.cms.response.GetContactResponse
import com.desynatdriver.ui.home.cms.response.GetFaqResponse
import com.desynatdriver.ui.home.cms.response.GetTicketTypeResponse
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
import com.desynatdriver.utils.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val api: ApiServices
) : BaseRepository(context) {


    val responseUploadImage = SingleLiveEvent<FileUploadResponse>()
    suspend fun uploadImage(
        imageFile: File? = null
    ) {
        handleApiCall(
            api = {
                api.uploadImage(
                    imageFile?.toMultipartPart("file")
                )
            },
            responseLiveData = responseUploadImage
        )
    }

    val loginLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun login(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.login(map)
            },
            responseLiveData = loginLiveData
        )
    }

    val otpVerifyLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun otpVerification(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.otpVerification(map)
            },
            responseLiveData = otpVerifyLiveData
        )
    }

    val resendOtpLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun resendOtp(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.resendOtp(map)
            },
            responseLiveData = resendOtpLiveData
        )
    }

    val forgotPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun forgotPassword(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.forgotPassword(map)
            },
            responseLiveData = forgotPasswordLiveData
        )
    }

    val createPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun createPassword(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.createPassword(map)
            },
            responseLiveData = createPasswordLiveData
        )
    }

    val dashBoardLiveData = SingleLiveEvent<GetProfileResponse>()
    suspend fun getDashBoard(
    ) {
        handleApiCall(
            api = {
                api.getDashBoard()
            },
            responseLiveData = dashBoardLiveData
        )
    }

    val profileLiveData = SingleLiveEvent<GetProfileResponse>()
    suspend fun getProfile(
    ) {
        handleApiCall(
            api = {
                api.getProfile()
            },
            responseLiveData = profileLiveData
        )
    }

    val logoutLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun logout(
    ) {
        handleApiCall(
            api = {
                api.logout()
            },
            responseLiveData = logoutLiveData
        )
    }

    val countryPlanLiveData = SingleLiveEvent<GetCountryResponse>()
    suspend fun getCountry(
    ) {
        handleApiCall(
            api = {
                api.getCountry()
            },
            responseLiveData = countryPlanLiveData
        )
    }

    val kuwaitLiveData = SingleLiveEvent<GetKuwaitZoneResponse>()
    suspend fun getKuwaitZone(
    ) {
        handleApiCall(
            api = {
                api.getKuwaitZone()
            },
            responseLiveData = kuwaitLiveData
        )
    }

    val editProfileLiveData = SingleLiveEvent<GetProfileResponse>()
    suspend fun editProfile(
        request: UpdateProfileRequest
    ) {
        handleApiCall(
            api = {
                api.editProfile(request)
            },
            responseLiveData = editProfileLiveData
        )
    }

    val dutyOnOffLiveData = SingleLiveEvent<DutyOnOffResponse>()
    suspend fun dutyOnOff(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.dutyOnOff(map)
            },
            responseLiveData = dutyOnOffLiveData
        )
    }

    val allOrderLiveData = SingleLiveEvent<GetAllOrderResponse>()
    suspend fun getAllOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getAllOrder(map)
            },
            responseLiveData = allOrderLiveData
        )
    }

    val newOrderLiveData = SingleLiveEvent<GetAllOrderResponse>()
    suspend fun getNewOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getNewOrder(map)
            },
            responseLiveData = newOrderLiveData
        )
    }

    val ongoingOrderLiveData = SingleLiveEvent<GetAllOrderResponse>()
    suspend fun getOngoingOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getOngoingOrder(map)
            },
            responseLiveData = ongoingOrderLiveData
        )
    }

    val completedOrderLiveData = SingleLiveEvent<GetAllOrderResponse>()
    suspend fun getCompletedOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getCompletedOrder(map)
            },
            responseLiveData = completedOrderLiveData
        )
    }

    val orderDetailLiveData = SingleLiveEvent<DeliveryDetailResponse>()
    suspend fun orderDetail(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.orderDetail(map)
            },
            responseLiveData = orderDetailLiveData
        )
    }

    val pickOrderLiveData = SingleLiveEvent<DeliveryDetailResponse>()
    suspend fun pickOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.pickOrder(map)
            },
            responseLiveData = pickOrderLiveData
        )
    }

    val updateDeliveryStatusLiveData = SingleLiveEvent<DeliveryDetailResponse>()
    suspend fun updateDeliveryStatus(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateDeliveryStatus(map)
            },
            responseLiveData = updateDeliveryStatusLiveData
        )
    }

    val returnOrderLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun returnOrder(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.returnOrder(map)
            },
            responseLiveData = returnOrderLiveData
        )
    }

    val getReasonLiveData = SingleLiveEvent<GetReasonResponse>()
    suspend fun getReason(
        type: Int
    ) {
        handleApiCall(
            api = {
                api.getReason(type)
            },
            responseLiveData = getReasonLiveData
        )
    }

    val getNotificationLiveData = SingleLiveEvent<GetNotificationResponse>()
    suspend fun getNotification(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getNotification(map)
            },
            responseLiveData = getNotificationLiveData
        )
    }

    val readNotificationLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun readNotification(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.readNotification(map)
            },
            responseLiveData = readNotificationLiveData
        )
    }


    val readAllNotificationLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun readAllNotification(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.readAllNotification(map)
            },
            responseLiveData = readAllNotificationLiveData
        )
    }


    val notificationCountLiveData = SingleLiveEvent<GetNotificationCountResponse>()
    suspend fun notificationCount(
    ) {
        handleApiCall(
            api = {
                api.notificationCount()
            },
            responseLiveData = notificationCountLiveData
        )
    }

    val getTicketTypeLiveData = SingleLiveEvent<GetTicketTypeResponse>()
    suspend fun getTicketType(
    ) {
        handleApiCall(
            api = {
                api.getTicketType()
            },
            responseLiveData = getTicketTypeLiveData
        )
    }

    var getRaisedTicketLiveData = SingleLiveEvent<GetAllRaisedTicketResponse>()
    suspend fun getRaisedTicket(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getRaisedTicket(map)
            },
            responseLiveData = getRaisedTicketLiveData
        )
    }

    var raiseTicketLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun raiseTicket(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.raiseTicket(map)
            },
            responseLiveData = raiseTicketLiveData
        )
    }

    var notificationOnOffLiveData = SingleLiveEvent<GetProfileResponse>()
    suspend fun notificationOnOff(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.notificationOnOff(map)
            },
            responseLiveData = notificationOnOffLiveData
        )
    }

    var appNotificationOnOffLiveData = SingleLiveEvent<GetProfileResponse>()
    suspend fun appNotificationOnOff(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.appNotificationOnOff(map)
            },
            responseLiveData = appNotificationOnOffLiveData
        )
    }

    var contactUsLiveData = SingleLiveEvent<GetContactResponse>()
    suspend fun contactUs(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.contactUs(map)
            },
            responseLiveData = contactUsLiveData
        )
    }

    var faqLiveData = SingleLiveEvent<GetFaqResponse>()
    suspend fun faq(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.faq(map)
            },
            responseLiveData = faqLiveData
        )
    }

    var termsAndConditionLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun termsAndCondition(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.termsAndCondition(map)
            },
            responseLiveData = termsAndConditionLiveData
        )
    }

    var privacyPolicyLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun privacyPolicy(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.privacyPolicy(map)
            },
            responseLiveData = privacyPolicyLiveData
        )
    }

    var aboutUsLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun aboutUs(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.aboutUs(map)
            },
            responseLiveData = aboutUsLiveData
        )
    }

    var returnPolicyLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun returnPolicy(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.returnPolicy(map)
            },
            responseLiveData = returnPolicyLiveData
        )
    }

    var exchangePolicyLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun exchangePolicy(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.exchangePolicy(map)
            },
            responseLiveData = exchangePolicyLiveData
        )
    }

    var cancellationPolicyLiveData = SingleLiveEvent<GetCmsResponse>()
    suspend fun cancellationPolicy(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.cancellationPolicy(map)
            },
            responseLiveData = cancellationPolicyLiveData
        )
    }


    var deleteAccountLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteAccount(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteAccount(map)
            },
            responseLiveData = deleteAccountLiveData
        )
    }


}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
                // Clear the value after it has been delivered
                // value = null
            }
        }
    }

    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    fun call() {
        value = null
    }

    fun clear() {
        value = null
        pending.set(false)
    }
}

