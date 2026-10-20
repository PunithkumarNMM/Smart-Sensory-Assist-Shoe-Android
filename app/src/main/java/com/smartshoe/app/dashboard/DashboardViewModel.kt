package com.smartshoe.app.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartshoe.app.network.Esp32Data
import com.smartshoe.app.network.Esp32Retrofit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _espData = MutableStateFlow<Esp32Data?>(null)
    val espData: StateFlow<Esp32Data?> = _espData

    init {
        startReading()
    }

    private fun startReading() {

        viewModelScope.launch {

            while (true) {

                try {

                    android.util.Log.d("ESP32_TEST", "Request started")

                    val response = Esp32Retrofit.api.getData()

                    android.util.Log.d("ESP32_TEST", "HTTP Code = ${response.code()}")

                    if (response.isSuccessful) {

                        android.util.Log.d("ESP32_TEST", "Response = ${response.body()}")

                        _espData.value = response.body()

                    }

                } catch (e: Exception) {

                    android.util.Log.e("ESP32_TEST", "Exception", e)

                }

                // Wait 1 second before the next request
                delay(1000)
            }
        }
    }
}