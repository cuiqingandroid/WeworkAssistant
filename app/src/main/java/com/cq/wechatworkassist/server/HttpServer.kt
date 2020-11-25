package com.cq.wechatworkassist.server

import android.content.Context
import android.util.Log
import com.cq.wechatworkassist.task.Task
import com.cq.wechatworkassist.task.TaskInterface
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject


class HttpServer(port: Int, private val context: Context) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        return processRequest(session.parms, session)
    }

    companion object {
        private val TAG = "wework_assist"
        @JvmStatic
        fun startServer(port: Int, context: Context) {
            Log.d(TAG,"start http server port:$port")
            HttpServer(port, context).start()
        }
    }

    fun processRequest(params: Map<String, String>,session: IHTTPSession) : Response {
        if (session.method == Method.POST) {
            val postParams = HashMap<String, String>()
            session.parseBody(postParams)
        }
        when (params["method"]) {
            "addPhoneFrient" -> {
                val phone = params["phone"]
                val msg = params["content"]
                if (phone.isNullOrBlank() || msg.isNullOrEmpty()) {
                    return makeErrResponse("手机号和内容不能为空")
                }
                val success  = (context as TaskInterface).startTask(
                    Task(phone, msg)
                )
//                val intent = Intent("add_task")
//                intent.putExtra("tasks","$phone,$msg")
//                context.sendBroadcast(intent)
                return if (success) {
                    makeSuccessResponse("")
                } else {
                    makeErrResponse("任务执行中")
                }
            }
            "stop" -> {
                val success  = (context as TaskInterface).stop()
                return if (success) {
                    makeSuccessResponse("")
                } else {
                    makeErrResponse("任务执行中")
                }
            }
        }

        val methodNotFound = JSONObject()
        methodNotFound.put("errcode", 10000)
        methodNotFound.put("errmsg", "method未实现")
        return newFixedLengthResponse(methodNotFound.toString())

    }

    private fun makeErrResponse(errcode: Int, errmsg : String?) : Response {
        val jsonObject = JSONObject()
        jsonObject.put("errmsg", errmsg)
        jsonObject.put("errcode", errcode)
        return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",jsonObject.toString())
    }

    private fun makeErrResponse(errmsg : String?) : Response {
        val jsonObject = JSONObject()
        jsonObject.put("errmsg", errmsg)
        jsonObject.put("errcode", -1)
        return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",jsonObject.toString())
    }

    private fun makeTimeoutResponse() : Response {
        val jsonObject = JSONObject()
        jsonObject.put("errmsg", "timeout")
        jsonObject.put("errcode", -1)
        return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",jsonObject.toString())
    }

    private fun makeSuccessResponse(any: Any?) : Response {
        val jsonObject = JSONObject()
        jsonObject.put("errmsg", "")
        jsonObject.put("errcode", 0)
        jsonObject.put("data", any)
        return newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8",jsonObject.toString())
    }
}
