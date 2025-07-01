package com.example.farmmobileapp.feature.notifications.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.notifications.data.model.NotificationDisplay
import com.example.farmmobileapp.feature.notifications.data.model.NotificationDto
import com.example.farmmobileapp.feature.notifications.domain.repository.NotificationsRepository
// Import UserRepository to fetch usernames if you want to display them
import com.example.farmmobileapp.feature.users.domain.repository.UserRepository
import com.example.farmmobileapp.util.Resource
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notificationDisplays: List<NotificationDisplay> = emptyList(), // Or List<NotificationDisplay>
    val error: String? = null,
    val isRealtimeConnected: Boolean = false,
    val realtimeError: String? = null
)

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository,
    private val authTokenRepository: TokenRepository, // For SignalR auth
    private val userRepository: UserRepository // For fetching usernames (optional)
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var hubConnection: HubConnection? = null
    private var connectionJob: Job? = null

    private val notificationHubUrl = "${BuildConfig.SIGNALR_HUB_BASE_URL}/hubs/notifications"


    init {
        loadNotifications()
        setupAndStartSignalR()
    }

    fun loadNotifications(forceRefresh: Boolean = false) {
        if (_uiState.value.isLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = notificationsRepository.getAllNotifications()) {
                is Resource.Success -> {
                    val sortedNotifications =
                        result.data?.sortedByDescending { it.timestamp } ?: emptyList()
                    _uiState.update {
                        it.copy(isLoading = false, notificationDisplays = sortedNotifications.map { NotificationDisplay.fromNotification(it) })
                    }
                    // TODO: If using NotificationDisplay, map here and fetch usernames
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }

                is Resource.Loading -> { /* Handled by isLoading=true */
                }
            }
        }
    }

    fun markNotificationAsRead(notification: NotificationDisplay) {
        // Optimistically update UI first
        val originalNotifications = _uiState.value.notificationDisplays
        val updatedNotifications = originalNotifications.map {
            if (it.id == notification.id) it.copy(isRead = true) else it
        }
        _uiState.update { it.copy(notificationDisplays = updatedNotifications) }

        viewModelScope.launch {
            when (val result = notificationsRepository.markAsRead(notification.id)) {
                is Resource.Success -> {
                    // Already updated optimistically, maybe log success
                    Log.d("NotificationsVM", "Notification ${notification.id} marked as read.")
                }

                is Resource.Error -> {
                    // Revert UI on error & show error message
                    Log.e("NotificationsVM", "Failed to mark notification as read: ${result.message}")
                    _uiState.update {
                        it.copy(
                            notificationDisplays = originalNotifications,
                            error = result.message ?: "Failed to mark as read"
                        )
                    }
                }

                is Resource.Loading -> {}
            }
        }
    }


    private fun setupAndStartSignalR() {
        if (hubConnection != null && hubConnection?.connectionState != HubConnectionState.DISCONNECTED) return
        Log.d("SignalR_Notifications", "Setting up SignalR connection...")
        connectionJob?.cancel()

        connectionJob = viewModelScope.launch {
            try {
                val token = authTokenRepository.getAccessToken()
                if (token == null) {
                    _uiState.update { it.copy(realtimeError = "Auth token needed for real-time.") }
                    return@launch
                }

                hubConnection = withContext(Dispatchers.IO) {
                    HubConnectionBuilder.create(notificationHubUrl)
                        .withTransport(com.microsoft.signalr.TransportEnum.WEBSOCKETS) // Explicitly WebSockets
                        // Backend uses groups based on farmId in token, so skipNegotiate might be problematic
                        // if negotiation is used to pass context for group joining.
                        // For JWT auth to Hub directly, often skipNegotiate=false is safer unless explicit.
                        // .shouldSkipNegotiate(true) // Test with and without this
                        .withAccessTokenProvider(
                            Single.fromCallable {
                                runBlocking {
                                    authTokenRepository.getAccessToken()
                                        ?: throw IllegalStateException("Access token is null")
                                }
                            }
                        )
                        .build()
                }

                // --- Handle "NewTaskNotification" ---
                // Backend sends `createdNotification` which is a NotificationDto
                hubConnection?.on(
                    "NewTaskNotification",
                    { payload: NotificationDto? -> // Expect a Notification object
                        Log.i("SignalR_Notifications", "Received NewTaskNotification: $payload")
                        if (payload != null) {
                            loadNotifications()
                        }
                    },
                    NotificationDto::class.java // Type info for deserialization
                )

                hubConnection?.onClosed { exception ->
                    Log.e("SignalR_Notifications", "Connection closed.", exception)
                    _uiState.update {
                        it.copy(
                            isRealtimeConnected = false,
                            realtimeError = "Connection lost."
                        )
                    }
                    // Basic retry
                    viewModelScope.launch { delay(5000); startSignalRConnection() }
                }

                startSignalRConnection()

            } catch (e: Exception) {
                _uiState.update { it.copy(realtimeError = "SignalR setup error: ${e.message}") }
                Log.e("SignalR_Notifications", "Setup error", e)
            }
        }
    }

    private fun startSignalRConnection() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { hubConnection?.start()?.blockingAwait() }
                if (hubConnection?.connectionState == HubConnectionState.CONNECTED) {
                    Log.i("SignalR_Notifications", "Connection successful!")
                    _uiState.update { it.copy(isRealtimeConnected = true, realtimeError = null) }
                } else {
                    _uiState.update {
                        it.copy(
                            isRealtimeConnected = false,
                            realtimeError = "Failed to connect."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRealtimeConnected = false,
                        realtimeError = "Connection start error: ${e.message}"
                    )
                }
                Log.e("SignalR_Notifications", "Start error", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectionJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            hubConnection?.stop()?.blockingAwait()
            hubConnection = null
        }
    }
}