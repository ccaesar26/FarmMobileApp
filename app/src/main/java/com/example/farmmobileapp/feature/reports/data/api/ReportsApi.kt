package com.example.farmmobileapp.feature.reports.data.api

import com.example.farmmobileapp.feature.reports.data.model.AddCommentRequest
import com.example.farmmobileapp.feature.reports.data.model.Comment
import com.example.farmmobileapp.feature.reports.data.model.CreateReportRequest
import com.example.farmmobileapp.feature.reports.data.model.Report

interface ReportsApi {
    suspend fun getAllReports(): List<Report> // Assuming an endpoint for all reports
    suspend fun getReportById(reportId: String): Report
    suspend fun createReport(request: CreateReportRequest): Report
    suspend fun getCommentsForReport(reportId: String): List<Comment>
    suspend fun addCommentToReport(reportId: String, request: AddCommentRequest): Comment
}