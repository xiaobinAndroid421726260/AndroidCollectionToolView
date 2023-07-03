package com.dbz.view.ext

import android.annotation.SuppressLint
import android.util.Base64
import java.lang.Exception
import java.lang.StringBuilder
import java.security.Key
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/22 21:46
 */
private const val AES_TYPE: String = "AES/ECB/PKCS5Padding"

@SuppressLint("GetInstance")
fun aesEncode(keyStr: String?, plainText: String): String {
    return try {
        val key: Key = SecretKeySpec(keyStr?.toByteArray(), "AES")
        val cipher = Cipher.getInstance(AES_TYPE)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val encrypt = cipher.doFinal(plainText.toByteArray())
        Base64.encodeToString(encrypt, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        "60888"
    }
}

fun md5(inStr: String): String {
    val md5: MessageDigest = try {
        MessageDigest.getInstance("MD5")
    } catch (e: Exception) {
        println(e.toString())
        e.printStackTrace()
        return ""
    }
    val charArray = inStr.toCharArray()
    val byteArray = ByteArray(charArray.size)
    for (i in charArray.indices) byteArray[i] = charArray[i].code.toByte()
    val md5Bytes = md5.digest(byteArray)
    val hexValue = StringBuilder()
    for (md5Byte in md5Bytes) {
        val `val` = md5Byte.toInt() and 0xff
        if (`val` < 16) hexValue.append("0")
        hexValue.append(Integer.toHexString(`val`))
    }
    return hexValue.toString()
}