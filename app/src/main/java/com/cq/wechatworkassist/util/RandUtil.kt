package com.cq.wechatworkassist.util

import java.util.*

object RandUtil {
    private val r = Random()
    fun randomInt(bount : Int) : Int{
        return r.nextInt(bount)
    }
    fun randomLong(bount : Int) : Long{
        return r.nextInt(bount).toLong()
    }

    /**
     * 生成随机数，介于start-end之间
     */
    fun randomInt(start: Int, end : Int) : Long{
        var step = end - start
        if (step <= 0) {
            step = 1
        }
        return (start + r.nextInt(step)).toLong()
    }
    /**
     * 生成随机数，介于start-end之间
     */
    fun randomLong(start: Int, end : Int) : Long{
        var step = end - start
        if (step <= 0) {
            step = 1
        }
        return (start + r.nextInt(step)).toLong()
    }
}