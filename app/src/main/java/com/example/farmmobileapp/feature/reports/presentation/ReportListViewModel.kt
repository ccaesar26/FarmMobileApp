package com.example.farmmobileapp.feature.reports.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.feature.fields.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.reports.data.model.Report
import com.example.farmmobileapp.feature.reports.data.model.ReportCreatedEventPayload
import com.example.farmmobileapp.feature.reports.data.model.ReportWithField
import com.example.farmmobileapp.feature.reports.domain.repository.ReportsRepository
import com.example.farmmobileapp.util.Resource
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class ReportListViewModel @Inject constructor(
    private val reportRepository: ReportsRepository,
    private val fieldsRepository: FieldsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportListUiState())
    val uiState: StateFlow<ReportListUiState> = _uiState.asStateFlow()

    private lateinit var hubConnection: HubConnection

    init {
        setupSignalR()
        startSignalRConnection()
        loadInitialData()
    }

    private fun setupSignalR() {
        hubConnection = HubConnectionBuilder.create("${BuildConfig.SIGNALR_HUB_BASE_URL}/reportHub")
            .withTransport(com.microsoft.signalr.TransportEnum.WEBSOCKETS)
            .shouldSkipNegotiate(true)
            .build()
        hubConnection.on(
            "ReportCreated",
            { payload: ReportCreatedEventPayload? -> // Expect the payload object (nullable for safety)
                Log.i("SignalR", "Received ReportCreated event with payload: $payload")
                if (payload != null) {
                    // You can now access payload.reportId, payload.farmId etc. if needed
                    // For now, just refresh the list
                    viewModelScope.launch {
                        loadReportsAndFields(forceRefresh = true)
                        Log.d(
                            "SignalR",
                            "Refreshed reportsWithFields list due to ReportCreated event with payload."
                        )
                    }
                } else {
                    Log.w("SignalR", "Received ReportCreated event but payload was null.")
                    // Optionally still refresh if any ReportCreated message warrants it
                    viewModelScope.launch {
                        loadReportsAndFields(forceRefresh = true)
                        Log.d(
                            "SignalR",
                            "Refreshed reportsWithFields list due to null-payload ReportCreated event."
                        )
                    }
                }
            },
            ReportCreatedEventPayload::class.java // <<< Provide the Java class type for deserialization
        )
        hubConnection.on("ReportUpdated") {
            viewModelScope.launch {
                loadReportsAndFields(forceRefresh = true)
            }
        }
        hubConnection.on("ReportDeleted") {
            viewModelScope.launch {
                loadReportsAndFields(forceRefresh = true)
            }
        }
    }

    private fun startSignalRConnection() {
        viewModelScope.launch {
            try {
                hubConnection.start().blockingAwait()
            } catch (e: Exception) {
                // Handle connection error
            }
        }
    }

    private fun loadInitialData() {
        if (_uiState.value.reportsWithFields.isNotEmpty()) return
        loadReportsAndFields()
    }

    // Load reportsWithFields and then fetch individual field names
    fun loadReportsAndFields(forceRefresh: Boolean = false) {
        // Avoid reload only if NOT forcing refresh and already loading
        if (_uiState.value.isLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            var fetchedReports: List<Report> = emptyList() // Hold reportsWithFields temporarily

            try {
                // 1. Fetch all reportsWithFields
                fetchedReports = reportRepository.getAllReports()
                Log.d("ViewModel", "Fetched ${fetchedReports.size} reportsWithFields.")

                // 2. Fetch field names for each report concurrently (mitigates N+1 slightly)
                // Use supervisorScope to prevent one failed field fetch from cancelling others
                val reportsWithFields = supervisorScope {
                    fetchedReports.map { report ->
                        async(Dispatchers.IO) { // Fetch each field potentially in parallel
                            val fieldName =
                                when (val fieldResult = fieldsRepository.getField(report.fieldId)) {
                                    is Resource.Success -> fieldResult.data?.name
                                        ?: "Unknown Field" // Get name from Field object
                                    is Resource.Error -> {
                                        Log.w(
                                            "ViewModel",
                                            "Error fetching field ${report.fieldId}: ${fieldResult.message}"
                                        )
                                        "Error" // Or "Unknown Field" on error
                                    }
                                    // Handle Loading state if your Resource includes it, though maybe not applicable here
                                    // is Resource.Loading -> "Loading..."
                                    is Resource.Loading<*> -> {
                                        Log.d("ViewModel", "Loading field ${report.fieldId}...")
                                        "Loading..." // Placeholder while loading
                                    }
                                }
                            // Assume Report model has commentCount
                            ReportWithField(
                                report = report,
                                fieldName = fieldName,
                                commentCount = report.commentCount
                            )
                        }
                    }.awaitAll() // Wait for all async calls to complete
                }


                _uiState.update {
                    it.copy(
                        isLoading = false,
                        reportsWithFields = reportsWithFields,
                        error = null
                    )
                }
                Log.d("ViewModel", "Finished processing reportsWithFields with fields.")

            } catch (e: Exception) {
                // Handle errors during the initial getAllReports fetch
                Log.e("ViewModel", "Failed to load initial reportsWithFields: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load reportsWithFields: ${e.message}",
                        reportsWithFields = emptyList()
                    ) // Clear list on error
                }
            }
        }
    }
}

