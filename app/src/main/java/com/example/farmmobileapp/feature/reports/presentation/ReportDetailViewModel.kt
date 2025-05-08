package com.example.farmmobileapp.feature.reports.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.feature.reports.data.model.AddCommentRequest
import com.example.farmmobileapp.feature.reports.data.model.CommentWithUsername
import com.example.farmmobileapp.feature.reports.data.model.Report
import com.example.farmmobileapp.feature.reports.domain.repository.ReportsRepository
import com.example.farmmobileapp.feature.users.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportDetailUiState(
    val isReportLoading: Boolean = false,
    val areCommentsLoading: Boolean = false,
    val isAddingComment: Boolean = false,
    val report: Report? = null,
    val comments: List<CommentWithUsername> = emptyList(),
    val error: String? = null, // Can be more granular (reportError, commentsError)
    val commentAddedSuccessfully: Boolean = false // For triggering transient UI feedback
)

@HiltViewModel
class ReportDetailViewModel @Inject constructor(
    private val reportRepository: ReportsRepository,
    private val usersRepository: UserRepository,
    savedStateHandle: SavedStateHandle // To get navigation arguments
) : ViewModel() {

    var reportId: String =
        savedStateHandle["reportId"] ?: error("reportId not found in navigation args")
        set(value) {
            field = value
            loadReportDetails(forceRefresh = true) // Load report details when reportId changes
        }

    private val _uiState = MutableStateFlow(ReportDetailUiState())
    val uiState: StateFlow<ReportDetailUiState> = _uiState.asStateFlow()

    init {
        loadReportDetails()
        loadComments()
    }

    fun loadReportDetails(forceRefresh: Boolean = false) {
        if (_uiState.value.isReportLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.update { it.copy(isReportLoading = true, error = null) }
            try {
                val report = reportRepository.getReportById(reportId)
                _uiState.update {
                    it.copy(isReportLoading = false, report = report)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isReportLoading = false,
                        error = "Failed to load report details: ${e.message}"
                    )
                }
                // Log e
            }
        }
    }

    fun loadComments(forceRefresh: Boolean = false) {
        if (_uiState.value.areCommentsLoading && !forceRefresh) return

        viewModelScope.launch {
            _uiState.update { it.copy(areCommentsLoading = true, error = null) }
            try {
                val comments = reportRepository.getCommentsForReport(reportId)

                // Fetch usernames for each comment and combine with comments
                val commentsWithUsernames = comments.map { comment ->
                    val username = usersRepository.getUsernameById(comment.userId)
                    CommentWithUsername(comment, username)
                }

                _uiState.update {
                    it.copy(areCommentsLoading = false, comments = commentsWithUsernames)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        areCommentsLoading = false,
                        error = "Failed to load comments: ${e.message}"
                    )
                }
                // Log e
            }
        }
    }

    fun addComment(commentText: String, parentCommentId: String? = null) {
        if (commentText.isBlank()) {
            _uiState.update { it.copy(error = "Comment cannot be empty") }
            return
        }
        if (_uiState.value.isAddingComment) return // Prevent double submission

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAddingComment = true,
                    error = null,
                    commentAddedSuccessfully = false
                )
            }
            try {
                val request =
                    AddCommentRequest(commentText = commentText, parentCommentId = parentCommentId)
                val newCommentId = reportRepository.addCommentToReport(reportId, request)

                // Refresh comments list after successful addition
                val updatedComments =
                    reportRepository.getCommentsForReport(reportId) // Or optimistically add newComment
                // Fetch usernames for each comment and combine with comments
                val commentsWithUsernames = updatedComments.map { comment ->
                    val username = usersRepository.getUsernameById(comment.userId)
                    CommentWithUsername(comment, username)
                }

                _uiState.update {
                    it.copy(
                        isAddingComment = false,
                        comments = commentsWithUsernames, // Show updated list
                        commentAddedSuccessfully = true // Signal success
                    )
                }
                // Reset success flag after a short delay or consumption
                kotlinx.coroutines.delay(100) // Let UI react before resetting
                _uiState.update { it.copy(commentAddedSuccessfully = false) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAddingComment = false, error = "Failed to add comment: ${e.message}")
                }
//                 Log e
            }
        }
    }

    // Function to reset error state if needed by UI
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}