package com.example.farmmobileapp.feature.reports.domain.repository

import com.example.farmmobileapp.feature.reports.data.api.ReportsApi
import com.example.farmmobileapp.feature.reports.data.model.AddCommentRequest
import com.example.farmmobileapp.feature.reports.data.model.Comment
import com.example.farmmobileapp.feature.reports.data.model.CreateReportRequest
import com.example.farmmobileapp.feature.reports.data.model.Report
import javax.inject.Inject

class ReportsRepositoryImpl @Inject constructor(
    private val reportsApi: ReportsApi,
) : ReportsRepository {

    override suspend fun getAllReports(): List<Report> =
        reportsApi.getAllReports()

    override suspend fun getReportById(reportId: String): Report =
        reportsApi.getReportById(reportId)

    override suspend fun createReport(request: CreateReportRequest): String =
        reportsApi.createReport(request)

    override suspend fun getCommentsForReport(reportId: String): List<Comment> =
        reportsApi.getCommentsForReport(reportId)

    override suspend fun addCommentToReport(reportId: String, request: AddCommentRequest): String =
        reportsApi.addCommentToReport(reportId, request)
}