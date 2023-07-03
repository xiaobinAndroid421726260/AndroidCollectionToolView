@file:OptIn(ExperimentalContracts::class)
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package com.dbz.view.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import com.blankj.utilcode.util.*
import com.dbz.view.utils.EditTextUtils.isPwdMatcher
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.contracts.ExperimentalContracts

/**
 * description:   字符串相关扩展
 *
 * @author Db_z
 * @Date 2020/9/28 17:02
 */

/**
 * String.trim() 只能去除字符串首尾两端的空格，使用 trimAll() 可去除字符串中所有空格
 *
 * @return 去除所有空格后的字符串值
 */
@kotlin.internal.InlineOnly
inline fun String?.trimAll(): String? {
    if (this == null) return null
    if (this.isBlank()) return ""
    return this.replace(" ".toRegex(), "")
}

/**
 * 判断指定字符串是否能转换为 Int 格式
 *
 * @return 是否能转换，true 能转换，false 不能
 */
@kotlin.internal.InlineOnly
inline fun String?.canToInt(): Boolean {
    if (this.isNullOrBlank()) {
        return false
    }
    try {
        this.toInt()
    } catch (e: NumberFormatException) {
        return false
    }
    return true
}

/**
 * 限制字符串的长度，不考虑特殊字符
 *
 * @param maxCount 最大长度
 * @return 经过限制处理后的字符串值
 */
@kotlin.internal.InlineOnly
inline fun String?.limitLength(maxCount: Int): String? {
    if (this == null) return null
    if (this.isBlank()) return ""
    return if (this.length <= maxCount) this else substring(0, maxCount)
}

/**
 * 限制字符串的长度，考虑特殊字符
 *
 * @param maxCount 最大长度
 * @return 经过限制处理后的字符串值
 */
@kotlin.internal.InlineOnly
inline fun String?.getLimitString(maxCount: Int): String? {
    if (this == null) return null
    if (this.isBlank()) return ""
    var count = 0
    var tempStr: String
    for (i in 0 until length) {
        tempStr = this.substring(i, i + 1)
        if (tempStr.toByteArray().size == 3) {
            count += 2
        } else {
            count++
        }
        if (count > maxCount) {
            return this.substring(0, i)
        }
    }
    return this
}

/**
 * 判断指定字符串是否包含 emoji 表情
 *
 * @return 是否包含 emoji 表情，true 包含，false 不包含
 */
@kotlin.internal.InlineOnly
inline fun String?.isContainsEmoji(): Boolean {
    if (this.isNullOrBlank()) {
        return false
    }
    val len: Int = this.length
    for (i in 0 until len) {
        if (this[i].isEmojiCharacter()) {
            return true
        }
    }
    return false
}

/**
 * 过滤 emoji 表情
 *
 * @return 经过过滤处理后的字符串值
 */
@kotlin.internal.InlineOnly
inline fun String?.filterEmoji(): String? {
    if (this == null) return null
    if (this.isBlank()) return ""
    return this.replace("[^\\u0000-\\uFFFF]".toRegex(), "")
}

/**
 * 判断指定字符是否为 emoji 表情
 *
 * @return 是否为 emoji 表情，true 是，false 不是
 */
@kotlin.internal.InlineOnly
inline fun Char?.isEmojiCharacter(): Boolean {
    if (this == null) {
        return false
    }
    return !(this.code == 0x0 || this.code == 0x9 || this.code == 0xA || this.code == 0xD ||
            this.code in 0x20..0xD7FF ||
            this.code in 0xE000..0xFFFD ||
            this.code in 0x10000..0x10FFFF)
}

/**
 * inline报警告暂不处理，否则打印的地方始终是StringExt不好根据log找到相应的类
 * Author:yangcheng
 * Date:2020/8/11
 * Time:17:19
 */
inline fun String?.logE() {
    if (!this.isNullOrBlank()) {
        Log.e("YM-", "$this")
    }
}

inline fun String?.logW() {
    if (!this.isNullOrBlank()) {
        Log.w("YM-", "$this")
    }
}

inline fun String?.logI() {
    if (!this.isNullOrBlank()) {
        Log.i("YM-", "$this")
    }
}

inline fun String?.logD() {
    if (!this.isNullOrBlank()) {
        Log.d("YM-", "$this")
    }
}

inline fun String?.toast() {
    if (!this.isNullOrBlank() && AppUtils.isAppForeground() && this.lowercase() != "null") {
        ToastUtils.getDefaultMaker().setGravity(Gravity.CENTER, 0, 0).show(this)
    }
}

inline fun String?.toastBottom() {
    if (!this.isNullOrBlank() && AppUtils.isAppForeground() && this.lowercase() != "null") {
        ToastUtils.getDefaultMaker().setGravity(Gravity.BOTTOM, 0, 194.dp2px()).show(this)
    }
}

/**
 * 前4位后4位保留，中间打马赛克
 */
inline fun String?.maskBankNum(): String? {
    return if (!this.isNullOrBlank() && length > 8) {
        val startStr = substring(0, 4)
        val endStr = substring(length - 4, length)
        "$startStr****$endStr"
    } else {
        this
    }
}

///**
// * 校验越南手机号(默认为：越南->VN)
// * @see com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap
// */
//inline fun String?.matchYnPhone(countryCode: String = "VN"): Boolean {
//    if (this.isNullOrBlank()) return false
//    if (countryCode.toUpperCase() == "VN" || countryCode.toUpperCase() == "YN") {
//        return this.startsWith("0") && this.length == 10
//    }
//    return this.isPhoneNumber(if (countryCode.toUpperCase(Locale.getDefault()) == "YN") "VN" else countryCode)
//        ?: false
//}
//
///**
// * 判断手机号(国家编码，中国->CN)
// * @see com.google.i18n.phonenumbers.CountryCodeToRegionCodeMap
// */
//fun String?.isPhoneNumber(countryCode: String = "VN"): Boolean {
//    if (this.isNullOrBlank()) return false
//    val phoneUtil = PhoneNumberUtil.getInstance()
//    return try {
//        val numberProto: Phonenumber.PhoneNumber = phoneUtil.parse(this, countryCode.toUpperCase(Locale.getDefault()))
//        phoneUtil.isValidNumber(numberProto)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        false
//    }
//}

/**
 * 校验越南号码号段
 * 081 082 083 084 085
 * 032 033 034 035 036 037 038 039
 * 070 076 077 078 079
 * 056 058 059
 */
inline fun String?.isYnPhone(): Boolean {
    val telRegex =
        "^((08[1-5])|(03[2-9])|(07[0,6,7,8,9])|(05[6,8,9]))[0-9]{7}$"
    val p: Pattern = Pattern.compile(telRegex)
    val m: Matcher = p.matcher(this)
    return m.find()
}


/**
 * 去掉小数点后面多余的0
 */
inline fun String?.deleteLastZero(): String {
    var result = this!!
    if (this!!.contains(".")) {
        val p: Pattern = Pattern.compile("0+?$")
        val m: Matcher = p.matcher(this)
        if (m.find()) {
            result = m.replaceAll("")!!
        }
    }
    return result
}


/**
 *检查邮箱号
 */
inline fun String?.matchEmail(): Boolean {
    if (TextUtils.isEmpty(this)) {
        return false
    }
    return this!!.contains("@")
}

/**
 * 忘记密码用
 */
inline fun String?.matchPwd(): Boolean {
    if (this.isNullOrBlank()) return false
    return isPwdMatcher(this)
}

//inline fun String?.snack() {
//    if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
//        SnackBarUtils.show(this)
//    }
//}
//
//inline fun String?.snackFinish() {
//    if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
//        ActivityUtils.getActivityList().takeIf { it.isNotEmpty() }?.let {
//            if (it.size == 1) {
//                snackView(it[0].window.decorView)
//            } else {
//                snackView(it[it.size - 2].window.decorView)
//            }
//        }
//    }
//}
//
//inline fun String?.snackDismiss() {
//    if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
//        ActivityUtils.getTopActivity()?.let {
//            snackView(it.window.decorView)
//        }
//    }
//}
//
//@SuppressLint("WrongConstant")
//inline fun String?.snackView(view : View?) {
//    if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
//        view?.let { KokSnackbar.make(it,this,KokSnackbar.LENGTH_SHORT).show() }
//    }
//}

inline fun String?.isNetImageUrl(): Boolean {
    return if (this.isNullOrBlank()) {
        false
    } else if (!this.startsWith("http", true)) {
        false
    } else {
        Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)").matcher(this.toLowerCase(Locale.getDefault()))
            .matches()
    }
}

inline fun File?.isImage(): Boolean {
    return if (this == null) {
        false
    } else {
        Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)")
            .matcher(this.path.toLowerCase(Locale.getDefault())).matches()
    }
}

inline fun String?.isVideoUrl(): Boolean {
    return if (this.isNullOrBlank()) {
        false
    } else if (!this.toLowerCase(Locale.getDefault()).startsWith("http", true)) {
        false
    } else {
        Pattern.compile(".*?(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob)")
            .matcher(this.toLowerCase(Locale.getDefault())).matches()
    }
}

inline fun String?.hasEnglishOrNum(): Boolean {
    return if (this.isNullOrBlank()) {
        return false
    } else {
        Pattern.compile("[a-zA-Z0-9]").matcher(this).find()
    }
}

inline fun String?.isLiveUrl(): Boolean {
    return if (this.isNullOrBlank()) {
        false
    } else {
        this.toLowerCase(Locale.getDefault()).run {
            startsWith("rtmp") || startsWith("rtsp")
        }
    }
}

fun String?.maxLengthFixed(maxLength: Int): String? {
    return if (this.isNullOrBlank()) "" else if (maxLength >= this.length || maxLength <= 0) this else {
        "${this.substring(0, maxLength)}..."
    }
}

/**复制内容到剪切板*/
inline fun String?.copyToClipboard(): Boolean {
    return try {
        //获取剪贴板管理器
        val cm = Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", this ?: "")
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        true
    } catch (e: Exception) {
        false
    }
}

fun String?.getHost(): String {
    return if (this.isNullOrBlank()) "" else Uri.parse(this).host ?: this
}

//文件目录转file
fun String?.toFile(): File? {
    if (this != null) {
        return if (this.startsWith("http", true)) null else {
            val f = File(this)
            if (f.exists()) f else UriUtils.uri2File(Uri.parse(this))
        }
    }
    return null
}

fun String?.getHttpUrl(): String? {
    return this?.let { if (it.startsWith("http", true)) it else "http://$it" }
}

fun String?.openOutLink() {
    if (!this.isNullOrBlank()) {
        try {
            val newUrl = if (this.startsWith("http", true)) this else "http://$this"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))
            ActivityUtils.getTopActivity()?.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

////Coil获取缓存图片文件
//fun String?.getCoilCacheFile(): File? {
//    return this?.toFile() ?: this?.toHttpUrlOrNull()?.let { u ->
//        CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles()
//            ?.lastOrNull { it.name.endsWith(".1") && it.name.contains(Cache.key(u)) }
//    }
//}

//替换空格
fun String?.replaceNbsp(): String {
    return this?.replace("&nbsp;", " ") ?: ""
}

fun String?.ifToDouble(): Double {
    return try {
        this!!.toDouble()
    } catch (e: Exception) {
        0.0
    }
}