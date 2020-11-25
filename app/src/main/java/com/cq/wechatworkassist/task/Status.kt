package com.cq.wechatworkassist.task

/**
 * 发送成功
 */
const val STATUS_SUCCESS = 1

/**
 * 手机号查不到
 */
const val STATUS_UNKNOWN_PHONE = 2

/**
 * 已经是好友
 */
const val STATUS_ALREADY_FRIEND = 3

/**
 * 企业微信不加好友
 */
const val STATUS_WEWORK_FRIEND = 4

/**
 * 已经是企业微信好友
 */
const val STATUS_ALREADY_WEWORK_FRIEND = 5
/**
 * 操作频繁
 */
const val STATUS_OPERATION_LIMIT = 6