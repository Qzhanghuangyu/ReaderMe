package com.quick.network.error

import java.lang.RuntimeException

class  BusinessException(val code: Int, message: String?) : RuntimeException(message)