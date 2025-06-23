package com.zhang.log.report

import com.google.gson.Gson
import com.zhang.log.RMLog
import com.zhang.log.report.bean.LogContentData
import com.zhang.log.report.bean.LogReportData
import com.zhang.log.report.bean.LogReportRequest
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.CoroutineContext

object RMLogReporter {
    private const val TAG = "HYLogReporter"
    private const val MONITOR_ID = "hunyuan_app_android"
    private const val MONITOR_KEY = "b15461c8"
    private const val MONITOR_LOG_URL = "https://api.aida.qq.com/api/aigc/v1/datareport/zhiyanmonitor/saveLog"
    private const val MONITOR_LOG_TPOIC = "sdk-9eca2f21b9c3aacf"
    private const val MAX_LOG_DATA_BATCH_SIZE = 1 // 1：相当于实时上报

    private var logDataList = mutableListOf<LogReportData>()

    val gson by lazy { Gson() }

    private var userId: String = ""
    private var applicationId: String? = null
    private var appVersion: String? = null

    fun updateParam(userId: String, applicationId: String, appVersion: String) {
        RMLogReporter.userId = userId
        RMLogReporter.applicationId = applicationId
        RMLogReporter.appVersion = appVersion
    }

    private val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob() +
            CoroutineExceptionHandler { _, e ->
                RMLog.w(TAG, "reportLog error: $e", e)
            }

    fun reportLog(level: String, content: String, traceId: String?) {
        CoroutineScope(coroutineContext).launch {
            val current = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
            val contentData = LogContentData(current, content, bundle_id = applicationId, app_version = appVersion,
                trace_id = traceId, user_id = userId
            )
            val logReportData = LogReportData(level, gson.toJson(contentData))
            logDataList.add(logReportData)
            if (logDataList.size >= MAX_LOG_DATA_BATCH_SIZE) {
                RMLog.d(TAG, "execute reportLog size: ${logDataList.size}")
                reportLog(gson.toJson(LogReportRequest(MONITOR_LOG_TPOIC, logDataList)))
                logDataList.clear()
            }
        }
    }

    private fun reportLog(body: String) {
        CoroutineScope(coroutineContext).launch {
            val ts = System.currentTimeMillis()
            val sign = "$MONITOR_ID$MONITOR_KEY$ts".md5()
            val headers = mutableMapOf(
                "appId" to MONITOR_ID,
                "ts" to ts.toString(),
                "sign" to sign
            )
            RMLog.d(TAG, "reportLog: $headers, body string: $body")
            val ret = executeBean(url = MONITOR_LOG_URL, headers = headers, body = body)
            RMLog.d(TAG, "reportLog result: ${ret.isSuccessful}, ret: ${ret.code}, ${ret.message}")
        }
    }

    private suspend fun executeBean(
        url: String,
        headers: MutableMap<String, String>?,
        body: String?,
    ): Response {
        val okHeaders = okhttp3.Headers.Builder()
        headers?.forEach {
            okHeaders.add(it.key, it.value)
        }
        val requestBody: okhttp3.RequestBody? =
            body.orEmpty().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url.toHttpUrl())
            .headers(okHeaders.build())
            .method("POST", requestBody)
            .build()
        return OkhttpManager.owner.getOkHttpClient().newCall(request).execute()
    }

    private fun String.md5(): String {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.update(toByteArray())
            val arrayOfByte = digest.digest()
            val buffer = StringBuffer()
            for (i in arrayOfByte.indices) {
                val j = 0xFF and arrayOfByte[i].toInt()
                if (j < 16) {
                    buffer.append("0")
                }
                buffer.append(Integer.toHexString(j))
            }
            return buffer.toString()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return ""
    }
}
