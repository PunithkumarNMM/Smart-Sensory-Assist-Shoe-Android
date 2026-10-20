package com.smartshoe.app.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {

    private val _refreshKey = MutableStateFlow(System.currentTimeMillis())
    val refreshKey: StateFlow<Long> = _refreshKey

    private val _cameraOnline = MutableStateFlow(false)
    val cameraOnline: StateFlow<Boolean> = _cameraOnline

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {

            while (true) {

                _refreshKey.value = System.currentTimeMillis()

                delay(1000)

            }

        }
    }

    fun refreshNow() {

        _refreshKey.value = System.currentTimeMillis()

    }

    fun setCameraOnline(online: Boolean) {

        _cameraOnline.value = online

    }

}