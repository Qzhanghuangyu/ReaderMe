package com.zhang.log

import android.util.Log
import com.tencent.tddiag.logger.TDLog
import com.tencent.tddiag.logger.TDLogInfo
import com.tencent.tddiag.protocol.LogLevel
import java.util.concurrent.ThreadLocalRandom

/**
 * 日志使用规范：
 * 1. 日志级别：
 * v：级别最低，用于打印详细的频繁的日志，般用于开发阶段
 * d：级别最低，用于打印详细的频繁的日志，般用于开发阶段
 * i：级别最低，用于打印详细的频繁的日志，般用于上线阶段
 * w：级别最低，用于打印详细的频繁的日志，般用于上线阶段
 * e：级别最低，用于打印详细的频繁的日志，般用于上线阶段
 *上线阶段可以看到的日志用i级别以上的日志
 */
object RMLog {
    private val logList = mutableListOf<TDLogInfo>()
    private var isInit = false
    private var showExtMsg = true // 是否带上类名，方法，行号
    private const val defaultTag = "HYAPP"
    private fun checkInit(initBack: () -> Unit, notInitBack: () -> Unit) {
        if (!isInit) {
            isInit = LogMgr.get.isInit()
            if (isInit) {
                i("HYLog init success")
                synchronized(logList) {
                    logList.forEach {
                        TDLog.log(it)
                    }
                    logList.clear()
                }
            }
        }
        if (isInit) {
            initBack()
        } else {
            notInitBack()
        }
    }

    private fun catchLog(level: Int, tag: String, msg: String, tr: Throwable? = null) {
        synchronized(logList) {
            logList.add(TDLogInfo().apply {
                this.level = level
                this.tag = tag
                this.message = msg
                this.throwable = tr
                this.tName = Thread.currentThread().name
                this.tid = Thread.currentThread().id
                this.timeMillis = System.currentTimeMillis()
            })
        }
    }

    @JvmStatic
    fun d(message: String) {
        d(message = message, tr = null)
    }

    @JvmStatic
    fun d(tr: Throwable) {
        d(message = "", tr = tr)
    }

    @JvmStatic
    fun d(tag: String, tr: Throwable) {
        d(tag = tag, message = "", tr = tr)
    }

    @JvmStatic
    fun d(tag: String, message: String) {
        d(tag, message, null)
    }

    @JvmStatic
    fun v(message: String) {
        v(message = message, tr = null)
    }

    @JvmStatic
    fun v(tr: Throwable) {
        v(message = "", tr = tr)
    }

    @JvmStatic
    fun v(tag: String, tr: Throwable) {
        v(tag = tag, message = "", tr = tr)
    }

    @JvmStatic
    fun v(tag: String, message: String) {
        v(tag, message, null)
    }

    @JvmStatic
    fun i(message: String) {
        i(message = message, tr = null)
    }

    @JvmStatic
    fun i(tr: Throwable) {
        i(message = "", tr = tr)
    }

    @JvmStatic
    fun i(tag: String, tr: Throwable) {
        i(tag = tag, message = "", tr = tr)
    }

    @JvmStatic
    fun i(tag: String, message: String) {
        i(tag, message, null)
    }

    @JvmStatic
    fun w(message: String) {
        w(message = message, tr = null)
    }

    @JvmStatic
    fun w(tr: Throwable) {
        w(message = "", tr = tr)
    }

    @JvmStatic
    fun w(tag: String, tr: Throwable) {
        w(tag = tag, message = "", tr = tr)
    }

    @JvmStatic
    fun w(tag: String, message: String) {
        w(tag, message, null)
    }

    @JvmStatic
    fun e(message: String) {
        e(message = message, tr = null)
    }

    @JvmStatic
    fun e(tr: Throwable) {
        e(message = "", tr = tr)
    }

    @JvmStatic
    fun e(tag: String, tr: Throwable) {
        e(tag = tag, message = "", tr = tr)
    }

    @JvmStatic
    fun e(tag: String, message: String) {
        e(tag, message, null)
    }

    @JvmStatic
    fun d(tag: String = defaultTag, message: String, tr: Throwable?) {
        checkInit({
            TDLog.d(tag, extMsg(message), tr)
        }, {
            catchLog(LogLevel.DEBUG, tag, extMsg(message), tr)
        })
    }

    @JvmStatic
    fun v(tag: String = defaultTag, message: String, tr: Throwable?) {
        checkInit({
            TDLog.v(tag, extMsg(message), tr)
        }, {
            catchLog(LogLevel.VERBOSE, tag, extMsg(message), tr)
        })
    }

    @JvmStatic
    fun i(tag: String = defaultTag, message: String, tr: Throwable?) {
        checkInit({
            TDLog.i(tag, extMsg(message), tr)
        }, {
            catchLog(LogLevel.INFO, tag, extMsg(message), tr)
        })
    }

    @JvmStatic
    fun w(tag: String = defaultTag, message: String, tr: Throwable?) {
        checkInit({
            TDLog.w(tag, extMsg(message), tr)
        }, {
            catchLog(LogLevel.WARN, tag, extMsg(message), tr)
        })
    }

    @JvmStatic
    fun e(tag: String = defaultTag, message: String, tr: Throwable?) {
        checkInit({
            TDLog.e(tag, extMsg(message), tr)
        }, {
            catchLog(LogLevel.ERROR, tag, extMsg(message), tr)
        })
    }

    private fun extMsg(message: String, index: Int = 8): String {
        if (showExtMsg) {
            val stackTrace = Throwable().stackTrace
            if (stackTrace.size <= index) {
                return message
            }
            var stackTraceElement = stackTrace[index]
            var className = stackTraceElement.className
            /*if (className.equals(LogUtil.javaClass.name)){ // 兼容 LogUtil
                if (stackTrace.size <= index+3){
                    return message
                }
                stackTraceElement = stackTrace[index+3]
                className = stackTraceElement.className
                if (className.equals(L.javaClass.name)) { // 兼容 L
                    if (stackTrace.size <= index + 4) {
                        return message
                    }
                    stackTraceElement = stackTrace[index + 4]
                    className = stackTraceElement.className
                }
            }*/
            var methodName = stackTraceElement.methodName
            var lineNumber = stackTraceElement.lineNumber
            return "$className.$methodName($lineNumber): $message"
        }
        return message
    }

    @JvmStatic
    fun forTag(tag: String) = Logger(tag)
}

@JvmInline
value class Logger(val tag: String) {

    /**
     * enable v/d level log
     */
    inline val enableVd: Boolean
        get() = LogMgr.get.isDebug()

    /**
     * only log on debug build
     */
    inline fun v(throwable: Throwable? = null, msg: () -> String) {
        if (enableVd) {
            // TODO: HYLog v 级别有bug，不出
            Log.v(tag, msg(), throwable)
            // HYLog.v(tag, msg(), throwable)
        }
    }

    /**
     * only log on debug build
     */
    inline fun d(throwable: Throwable? = null, msg: () -> String) {
        if (enableVd) {
            RMLog.d(tag, msg(), throwable)
        }
    }

    inline fun i(throwable: Throwable? = null, msg: () -> String) {
        RMLog.i(tag, msg(), throwable)
    }

    /**
     * 采样日志，按照 [sampleRate]% 的比例命中，未命中转调 [v]
     * @param sampleRate ranged in [0-100]
     */
    inline fun sampledI(sampleRate: Int, throwable: Throwable? = null, msg: () -> String) {
        if (ThreadLocalRandom.current().nextInt(100) < sampleRate) {
            i(throwable, msg)
        } else {
            v(throwable, msg)
        }
    }

    inline fun w(throwable: Throwable? = null, msg: () -> String) {
        RMLog.w(tag, msg(), throwable)
    }

    inline fun e(throwable: Throwable? = null, msg: () -> String) {
        RMLog.e(tag, msg(), throwable)
    }

    inline fun ifVerbose(msg: () -> String): String {
        return if (enableVd) msg() else ""
    }

    fun appendTag(postFix: String) = Logger(tag + postFix)
}