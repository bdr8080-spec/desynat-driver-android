package com.desynatdriver.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdriver.data.repo.MainRepository
import com.desynatdriver.ui.auth.response.FileUploadResponse
import com.desynatdriver.ui.auth.response.GetCountryResponse
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.home.response.GetKuwaitZoneResponse
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

}
