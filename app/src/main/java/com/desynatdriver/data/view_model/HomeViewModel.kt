package com.desynatdriver.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdriver.data.repo.MainRepository
import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.DeliveryDetailResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetAllOrderResponse
import com.desynatdriver.ui.home.fragment.deliveries.response.GetReasonResponse
import com.desynatdriver.ui.home.response.DutyOnOffResponse
import com.desynatdriver.ui.home.response.GetKuwaitZoneResponse
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

    val pickOrderResult: LiveData<CommonResponse>
        get() = repository.pickOrderLiveData

    fun pickOrder(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.pickOrder(map)
        }
    }

    val updateDeliveryStatusResult: LiveData<CommonResponse>
        get() = repository.updateDeliveryStatusLiveData

    fun updateDeliveryStatus(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateDeliveryStatus(map)
        }
    }


}
