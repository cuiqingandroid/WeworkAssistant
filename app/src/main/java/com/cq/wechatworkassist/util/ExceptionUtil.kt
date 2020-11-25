package com.cq.wechatworkassist.util

import kotlin.concurrent.thread

/**
 * BasicUtil contains the helper functions for general purpose.
 */
object ExceptionUtil {
    /**
     * trySilently will execute a callback and ignore any thrown exceptions.
     */
    fun <T: Any>trySilently(func: () -> T?){
        try { func() } catch (t: Throwable) {
            t.printStackTrace()
            t.stackTrace
        }
    }

    /**
     * tryVerbosely will execute a callback and record any thrown exceptions to the Xposed log.
     */
    fun <T: Any>tryVerbosely(func: () -> T?) {
        try { func() } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    /**
     * tryAsynchronously will execute a callback in another thread and record any thrown exceptions
     * to the Xposed log.
     *
     * Remember to handle UI operations in UI thread properly in the callback.
     */
    fun tryAsynchronously(func: () -> Unit): Thread {
        return thread(start = true) { func() }.apply {
            setUncaughtExceptionHandler { _, t ->
                t.printStackTrace()
            }
        }
    }

}