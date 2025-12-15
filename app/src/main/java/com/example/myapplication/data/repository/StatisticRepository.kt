package com.example.myapplication.data.repository


import com.example.myapplication.data.api.ApiService
import com.example.myapplication.data.model.Statistic

class StatisticRepository(private val apiService: ApiService) {

    suspend fun getStatistics(): Statistic {
        return apiService.getStatistics()
    }

    companion object {
        @Volatile
        private var instance: StatisticRepository? = null

        fun getInstance(apiService: ApiService): StatisticRepository =
            instance ?: synchronized(this) {
                instance ?: StatisticRepository(apiService).also { instance = it }
            }
    }
}