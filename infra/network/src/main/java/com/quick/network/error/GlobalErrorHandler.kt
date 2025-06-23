package com.quick.network.error

import com.quick.network.apiresponse.NetworkResponseAdapterFactory
import com.quick.network.error.BusinessException

class GlobalErrorHandler : NetworkResponseAdapterFactory.FailureHandler{
    override fun onFailure(throwable: BusinessException) {
        when (throwable.code) {
            2096 -> {
            }
            3099 -> {
            }
        }
    }
}