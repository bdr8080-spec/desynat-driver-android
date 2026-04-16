package com.desynatdriver.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdriver.data.repo.MainRepository
import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.cms.response.GetAllRaisedTicketResponse
import com.desynatdriver.ui.home.cms.response.GetContactResponse
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress: LiveData<Boolean> get() = repository.progress
    val error: LiveData<String> get() = repository.error
    fun reset() {
        repository.error.value = null
        repository.progress.value = false
    }

    val dashBoardResult: LiveData<GetProfileResponse>
        get() = repository.dashBoardLiveData

    fun getDashBoard() {
        viewModelScope.launch {
            repository.getDashBoard()
        }
    }

    val profileResult: LiveData<GetProfileResponse>
        get() = repository.profileLiveData

    fun getProfile() {
        viewModelScope.launch {
            repository.getProfile()
        }
    }

    val uploadImageResult: LiveData<FileUploadResponse>
        get() = repository.responseUploadImage

    fun uploadImage(
        image: File?=null
    ) {
        viewModelScope.launch {
            repository.uploadImage(image)
        }
    }

    val logoutResult: LiveData<CommonResponse>
        get() = repository.logoutLiveData

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val kuwaitZoneResult: LiveData<GetKuwaitZoneResponse>
        get() = repository.kuwaitLiveData

    fun getKuwaitZone() {
        viewModelScope.launch {
            repository.getKuwaitZone()
        }
    }

    val editProfileResult: LiveData<GetProfileResponse>
        get() = repository.editProfileLiveData

    fun editProfile(request:UpdateProfileRequest) {
        viewModelScope.launch {
            repository.editProfile(request)
        }
    }

    val dutyOnOffResult: LiveData<DutyOnOffResponse>
        get() = repository.dutyOnOffLiveData

    fun dutyOnOff(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.dutyOnOff(map)
        }
    }


    val getAllOrderResult: LiveData<GetAllOrderResponse>
        get() = repository.allOrderLiveData

    fun getAllOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAllOrder(map)
        }
    }

    val getNewOrderResult: LiveData<GetAllOrderResponse>
        get() = repository.newOrderLiveData

    fun getNewOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getNewOrder(map)
        }
    }

    val getOngoingOrderResult: LiveData<GetAllOrderResponse>
        get() = repository.ongoingOrderLiveData

    fun getOngoingOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getOngoingOrder(map)
        }
    }

    val getCompletedOrderResult: LiveData<GetAllOrderResponse>
        get() = repository.completedOrderLiveData

    fun getCompletedOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getCompletedOrder(map)
        }
    }

    val orderDetailResult: LiveData<DeliveryDetailResponse>
        get() = repository.orderDetailLiveData

    fun orderDetail(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.orderDetail(map)
        }
    }

    val getReasonResult: LiveData<GetReasonResponse>
        get() = repository.getReasonLiveData

    fun getReason(type:Int) {
        viewModelScope.launch {
            repository.getReason(type)
        }
    }

    val pickOrderResult: LiveData<DeliveryDetailResponse>
        get() = repository.pickOrderLiveData

    fun pickOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.pickOrder(map)
        }
    }

    val updateDeliveryStatusResult: LiveData<DeliveryDetailResponse>
        get() = repository.updateDeliveryStatusLiveData

    fun updateDeliveryStatus(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(map)
        }
    }

    val returnOrderResult: LiveData<CommonResponse>
        get() = repository.returnOrderLiveData

    fun returnOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.returnOrder(map)
        }
    }

    val getNotificationResult: LiveData<GetNotificationResponse>
        get() = repository.getNotificationLiveData

    fun getNotification(map :HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getNotification(map)
        }
    }

    val readNotificationResult: LiveData<CommonResponse>
        get() = repository.readNotificationLiveData

    fun readNotification(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.readNotification(map)
        }
    }


    val readAllNotificationResult: LiveData<CommonResponse>
        get() = repository.readAllNotificationLiveData

    fun readAllNotification(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.readAllNotification(map)
        }
    }

    val notificationCountResult: LiveData<GetNotificationCountResponse>
        get() = repository.notificationCountLiveData

    fun notificationCount() {
        viewModelScope.launch {
            repository.notificationCount()
        }
    }

    val getTicketTypeResult: LiveData<GetTicketTypeResponse>
        get() = repository.getTicketTypeLiveData

    fun getTicketType() {
        viewModelScope.launch {
            repository.getTicketType()
        }
    }

    val getRaisedTicketResult: LiveData<GetAllRaisedTicketResponse>
        get() = repository.getRaisedTicketLiveData

    fun getRaisedTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getRaisedTicket(map)
        }
    }

    val raiseTicketResult: LiveData<CommonResponse>
        get() = repository.raiseTicketLiveData

    fun raiseTicket(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.raiseTicket(map)
        }
    }

    val contactUsResult: LiveData<GetContactResponse>
        get() = repository.contactUsLiveData

    fun contactUs(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.contactUs(map)
        }
    }

}
