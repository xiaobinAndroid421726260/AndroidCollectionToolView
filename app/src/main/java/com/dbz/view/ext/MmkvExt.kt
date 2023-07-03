package com.dbz.view.ext

import androidx.appcompat.app.AppCompatDelegate
import com.dbz.view.R
import com.dbz.view.common.Constant.ConstantUser.DEVICE_KEY
import com.dbz.view.common.Constant.ConstantUser.KEY_SID
import com.dbz.view.common.Constant.ConstantUser.KEY_SN
import com.dbz.view.common.Constant.ConstantUser.KEY_USER_KEY
import com.tencent.mmkv.MMKV

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/8 16:43
 */
private const val THEME = "theme"
private const val KEY_NIGHT_MODE = "key_night_mode"
private const val SAVE_NIGHT_MODE = "save_night_mode"
private const val FONT_SIZE = "font_size"

fun setAppTheme(theme: Int) {
    MMKV.defaultMMKV().putInt(THEME, theme)
}

fun getAppTheme() = MMKV.defaultMMKV().getInt(THEME, R.style.AppTheme)

fun setNightMode(theme: Int) {
    MMKV.defaultMMKV().putInt(KEY_NIGHT_MODE, theme)
}

fun getNightMode() = MMKV.defaultMMKV().getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO)

fun saveLastNightMode(theme: Int) {
    MMKV.defaultMMKV().putInt(SAVE_NIGHT_MODE, theme)
}

fun saveFontSize(fontSize: Int){
    MMKV.defaultMMKV().putInt(FONT_SIZE, fontSize)
}

fun getFontSize() = MMKV.defaultMMKV().getInt(FONT_SIZE, 1)

fun getDeviceKey(): String? = MMKV.defaultMMKV().decodeString(DEVICE_KEY)

fun getUserKey(): String? = MMKV.defaultMMKV().decodeString(KEY_USER_KEY)

fun getSN(): String = MMKV.defaultMMKV().decodeString(KEY_SN, "").toString()

fun getSID(): String = MMKV.defaultMMKV().decodeString(KEY_SID, "").toString()
