package com.dbz.view.common

import com.dbz.view.ext.md5

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/13 15:29
 */
object Constant {

    // 是否加密数据
    const val isEncodeParam = true // 是否加密数据

    // 单次请求是否不加密(注：只有这一单次请求不加密， 请在请求完成后关闭此状态)
    var singleEncodeParam = false

    // 加密类型
    const val TYPE = "type_key"
    const val TYPE_CIPHER = "type_cipher"
    const val TYPE_DEVICE_KEY = "type_device_key"
    const val TYPE_USER_KEY = "type_user_key"
    const val TYPE_LIVE_KEY = "type_live_key"
    const val TYPE_UPLOAD_CRASH_KEY = "type_upload_crash_key"

    object ConstantEncode {
        const val DEFAULT_PARAM = "DATA"
        val INIT_KEY: String = md5("TIANXIAPISECRET")
        const val LIVE_KEY = "liveDeviceKey123liveDeviceKey123"
        val UPLOAD_CRASH_KEY: String = md5("MILETUCRYPTSTRING")
    }

    object ConstantPublic {

    }

    object ConstantCode {

    }

    object ConstantUser {
        const val DEVICE_KEY = "device_key" // device_key
        const val KEY_USER_KEY = "key_user_key" // key_user_key
        const val KEY_UID = "key_uid" // key_uid
        const val KEY_SN = "key_sn" // key_sn
        const val KEY_SID = "key_sid" // key_sid
        const val KEY_IM_SIG = "im_sig" // im_sig
        const val KEY_SIM_SERIAL_NUMBER = "key_sim_serial_number" // key_sim_serial_number
        const val KEY_FIRST_OEPN_APP = "key_first_open_app" //打开APP第一次判断

    }
}