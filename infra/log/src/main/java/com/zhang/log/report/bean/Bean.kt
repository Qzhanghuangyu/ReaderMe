package com.zhang.log.report.bean

data class LogReportRequest(
    val topic: String? = null,
    val data: List<LogReportData>? = null,
)

data class LogReportData(
    val type: String? = null,   // log level
    val content: String? = null,
)

// {\"type\":\"ios\",\"time\":\"2024-03-06 12:42:41\",\"agent\":\"INFO信息\"}
data class LogContentData(
    val time: String? = null,
    val content: String? = null,
    val type: String = "Android",
    val bundle_id: String? = null,
    val app_version: String? = null,
    val trace_id: String? = null,
    val user_id: String? = null,
)