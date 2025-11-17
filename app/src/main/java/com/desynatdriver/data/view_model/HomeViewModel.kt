package com.desynatdriver.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatdriver.data.repo.MainRepository
import com.desynatdriver.ui.auth.response.LoginResponse
import com.desynatdriver.ui.response.CommonResponse
import com.desynatdriver.ui.response.GetProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    val logoutResult: LiveData<CommonResponse>
        get() = repository.logoutLiveData

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

}
