package com.zhang.log

import android.content.Context
import com.tencent.tddiag.logger.TDLog
import com.tencent.tddiag.logger.TDLogConfig
import com.tencent.tddiag.protocol.LogLevel

class LogMgr {

    companion object{
        const val MAX_SIZE = 10*1024*1024L
        const val ALIVE_DAY = 10
        const val ALIVE_SIZE = 200*1024*1024L
        val get: LogMgr by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { LogMgr() }
    }

    private var isInitialized = false
    private var logPath: String = ""
    private var isDebug = false

    fun init(context: Context,isDebug: Boolean, logPath: String) {
        this.logPath = logPath
        val logConfig = TDLogConfig.Builder(context)
                .setLogPath(logPath) // 日志输出目录，需提前获取写权限
                .setLogLevel(if (isDebug) LogLevel.VERBOSE else LogLevel.INFO) // 默认VERBOSE
                .setConsoleLog(isDebug) // 默认true
                .setMaxFileSize(MAX_SIZE) // 默认10M
                .setMaxAliveFileSize(ALIVE_SIZE) // 默认不限制，最小值200M
                .setMaxAliveDay(ALIVE_DAY) // 默认7天, 最小值1天
                //.setPubKey(pubKey) // 日志加密公钥（可选）
                .build()
        TDLog.initialize(context, logConfig)
        this.isDebug = isDebug
        isInitialized = true
    }
    fun isInit(): Boolean {
        return isInitialized
    }

    fun isDebug(): Boolean {
        return isDebug
    }

    fun flushLog() {
        TDLog.flushLog()
    }

    fun getLogPath(): String {
        return logPath
    }
    /**
     * 关闭日志，退出应用时调用
     */
    fun close() {
        TDLog.closeLog()
    }
}