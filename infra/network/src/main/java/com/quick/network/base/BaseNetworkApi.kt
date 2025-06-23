package com.quick.network.base

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.quick.network.apiresponse.NetworkResponseAdapterFactory
import com.quick.network.apiresponse.MoshiResultTypeAdapterFactory
import com.quick.network.apiresponse.MoshiResultTypeAdapterFactory.Envelope
import com.quick.network.commoninterceptor.CommonRequestInterceptor
import com.quick.network.commoninterceptor.CommonResponseInterceptor
import com.quick.network.environment.EnvironmentActivity
import com.quick.network.environment.IEnvironment
import com.quick.network.error.GlobalErrorHandler
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

abstract class BaseNetworkApi : IEnvironment {
    var mRetrofit: Retrofit
    private var mBaseUrl: String = if(mIsFormal) test else formal
    private val globalErrorHandler = GlobalErrorHandler()

    companion object {
        private var iNetworkRequiredInfo: INetworkRequiredInfo? = null
        private var mIsFormal = true

        fun init(networkRequiredInfo: INetworkRequiredInfo) {
            iNetworkRequiredInfo = networkRequiredInfo
            mIsFormal = EnvironmentActivity.isOfficialEnvironment(networkRequiredInfo.applicationContext)
        }
    }

    private val moshi = Moshi.Builder()
        .add(MoshiResultTypeAdapterFactory(getEnvelopeHandler()))
        .addLast(KotlinJsonAdapterFactory())
        .build()

    open fun <T> getService(service: Class<T>?): T {
        return mRetrofit.create(service)
    }

    constructor() {
        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder.baseUrl(mBaseUrl)
        retrofitBuilder.client(getOkHttpClient())
        retrofitBuilder.addConverterFactory(MoshiConverterFactory.create(moshi))
        retrofitBuilder.addCallAdapterFactory(
            NetworkResponseAdapterFactory(globalErrorHandler)
        )
        retrofitBuilder.client(getOkHttpClient())
        mRetrofit = retrofitBuilder.build()
    }

    private fun log(msg: Any?) = println("[${Thread.currentThread().name}] $msg")

    private fun getOkHttpClient(): OkHttpClient? {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(CommonRequestInterceptor(iNetworkRequiredInfo))
        okHttpClientBuilder.addInterceptor(CommonResponseInterceptor())
        if(getInterceptor() != null) {
            okHttpClientBuilder.addInterceptor(getInterceptor()!!)
        }
        if (iNetworkRequiredInfo != null && iNetworkRequiredInfo!!.isDebug) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            okHttpClientBuilder.addInterceptor(httpLoggingInterceptor)
        } else {
            // throw exception
        }
        return okHttpClientBuilder.build()
    }

    protected abstract fun getInterceptor(): Interceptor?

    protected open fun getEnvelopeHandler(): Envelope? {
        return null
    }
}