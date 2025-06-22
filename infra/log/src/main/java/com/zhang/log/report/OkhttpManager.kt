package com.zhang.log.report

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkhttpManager {

    val logging = HttpLoggingInterceptor().apply {
        // 设置日志级别
        level = HttpLoggingInterceptor.Level.BODY
    }


    private var okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .apply {
            if (IS_LOGGING) {
                addInterceptor(logging)
            }
        }
        .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
        //.eventListenerFactory(BuglyListenerFactory.getInstance())
        .build()

    companion object {
        private const val TAG = "OkhttpManager"
        const val WRITE_TIME_OUT = 30L  //秒
        const val READ_TIME_OUT = 30L  //秒
        const val CONNECT_TIME_OUT = 10L  //秒
        const val IS_LOGGING = false//发布版本必须设置为false
        val owner: OkhttpManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkhttpManager()
        }

    }

    fun getOkHttpClient(): OkHttpClient {
        return okHttpClient
    }
}