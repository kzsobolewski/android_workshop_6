package com.example.gdgandroidwebinar6.ui.main

import com.example.gdgandroidwebinar6.Consumable
import com.example.gdgandroidwebinar6.domain.Forecast

data class MainUiModel(
    val forecasts: List<Forecast> = emptyList(),
    val isLoading: Boolean = false,
    val error: Consumable<Unit> = Consumable(null)
)
