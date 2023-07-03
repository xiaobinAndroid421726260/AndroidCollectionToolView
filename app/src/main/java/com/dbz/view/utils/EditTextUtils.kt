package com.dbz.view.utils

import android.text.TextUtils
import android.view.View
import android.widget.EditText
import java.util.*
import java.util.regex.Pattern

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/6/12 14:39
 */
object EditTextUtils {

    private const val ALL_LETTER = "^[A-Za-z]+$"
    private const val ALL_NUMBER = "^[0-9]*$"
    private const val REGEX_ILLEGAL_INPUT = "^[a-zA-Z0-9]+$"
    private const val REGEX_PHONE = "^1[0-9]{10}$"
    private const val REGEX_USERNAME_INPUT = "^[A-Za-z](?=(.*[a-zA-Z])+)(?=(.*[0-9])+)[0-9A-Za-z]{3,10}$"
    private const val TIP_1 = "密码长度为8-12位，字母+数字的组合"
    private const val TIP_2 = "密码必须包含大写字母和小写字母"

    fun checkInputEmpty(editTextArr: Array<EditText>, z: Boolean): Boolean {
        for (editText in editTextArr) {
            if (z) {
                if (isEmpty(editText)) {
                    return false
                }
            } else if (isEmpty(editText)) {
                return false
            }
        }
        return true
    }

    fun combine1EditAndButton(editText: EditText, view: View?) {
        val z = !isEmpty(editText)
        if (view != null && z != view.isClickable) {
            view.isEnabled = z
        }
    }

    fun combine3EditAndButton(
        editText: EditText,
        editText2: EditText,
        editText3: EditText,
        view: View,
        view2: View,
    ) {
        val z =
            if (view.visibility != View.VISIBLE) !(isEmpty(editText) || isEmpty(editText2)) else !(isEmpty(
                editText) || isEmpty(editText2) || isEmpty(editText3))
        if (z != view2.isClickable) {
            view2.isEnabled = z
        }
    }

    fun getEdtString(editText: EditText): String? {
        return editText.text.toString().trim { it <= ' ' }
    }

    fun isEmpty(editText: EditText): Boolean {
        return TextUtils.isEmpty(editText.text.toString())
    }

    fun isNameMatcher2(str: String): Boolean {
        return Pattern.matches("^[A-Za-z][A-Za-z][0-9A-Za-z]{3,8}$",
            str.lowercase(Locale.getDefault())) && !Pattern.matches("^[0-9]*$", str.lowercase(
            Locale.getDefault())) && !Pattern.matches("^[A-Za-z][A-Za-z]+$",
            str.lowercase(Locale.getDefault()))
    }

    fun isNameMatcher(str: String): Boolean {
        return Pattern.matches("^[A-Za-z]{2}[0-9A-Za-z]{2,7}$", str.lowercase(Locale.getDefault()))
    }

    fun isPhoneNumber(str: String?): Boolean {
        return Pattern.matches("^1[0-9]{10}$", str)
    }

    fun isPwdMatcher(str: String): Boolean {
        val length = str.length
        return if (length > 15 || length < 8) {
            false
        } else Pattern.compile("[a-z]+").matcher(str.lowercase(Locale.getDefault()))
            .find() && Pattern.compile("[0-9]+").matcher(str)
            .find() && Pattern.matches("^[a-zA-Z0-9]+$", str.lowercase(
            Locale.getDefault()))
    }

    fun isPwdMatcher2(str: String): Boolean {
        val length = str.length
        return if (length > 15 || length < 8) {
            false
        } else Pattern.matches("^[0-9A-Za-z]{1,15}$", str.lowercase(Locale.getDefault()))
    }

    fun isRegexMatcher(str: String): Boolean {
        return Pattern.matches("^[a-zA-Z0-9]+$", str.lowercase(Locale.getDefault()))
    }

    fun isUserNameOld(str: String): Boolean {
        if (!Pattern.matches("^[a-zA-Z0-9]+$", str.lowercase(Locale.getDefault()))) {
            if (!Pattern.matches("^[A-Za-z]+$", str.lowercase(Locale.getDefault()))) {
                return Pattern.matches("^[0-9]*$", str.lowercase(Locale.getDefault()))
            }
        }
        return false
    }
}