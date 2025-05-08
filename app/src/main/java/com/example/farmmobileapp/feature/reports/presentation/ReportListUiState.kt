package com.example.farmmobileapp.feature.reports.presentation

import com.example.farmmobileapp.feature.reports.data.model.ReportWithField

data class ReportListUiState(
    val isLoading: Boolean = false,
    val reportsWithFields: List<ReportWithField> = emptyList(),
    val error: String? = null
)