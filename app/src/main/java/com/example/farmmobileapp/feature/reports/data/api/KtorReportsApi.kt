package com.example.farmmobileapp.feature.reports.data.api

import com.example.farmmobileapp.feature.reports.data.model.AddCommentRequest
import com.example.farmmobileapp.feature.reports.data.model.Comment
import com.example.farmmobileapp.feature.reports.data.model.CreateReportRequest
import com.example.farmmobileapp.feature.reports.data.model.Report
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class KtorReportsApi @Inject constructor(
    private val httpClient: HttpClient // Inject the configured HttpClient
) : ReportsApi {
    // Base URL should be configured in your HttpClient module
    private val reportsBasePath = "/api/reports" // Adjust to your actual base path

    // @Throws(Exception::class) // Or use a Result<T> wrapper
    override suspend fun getAllReports(): List<Report> {
        return httpClient.get("$reportsBasePath/my").body()
    }

    override suspend fun getReportById(reportId: String): Report {
        return httpClient.get("$reportsBasePath/$reportId").body()
    }

    override suspend fun createReport(request: CreateReportRequest): String {
        return httpClient.post(reportsBasePath) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getCommentsForReport(reportId: String): List<Comment> {
        return httpClient.get("$reportsBasePath/$reportId/comments").body()
    }

    override suspend fun addCommentToReport(reportId: String, request: AddCommentRequest): String {
        return httpClient.post("$reportsBasePath/$reportId/comments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}