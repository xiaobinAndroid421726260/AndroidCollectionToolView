package com.dbz.view.utils

import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
Describe：自定义Glide变换，实现图片4个圆角大小自定义
Author:ZT
Date:2021/10/1
 */
class RoundTransformation(
    val topLeftRadius: Float = 0f,
    val topRightRadius: Float = 0f,
    val bottomLeftRadius: Float = 0f,
    val bottomRightRadius: Float = 0f
): BitmapTransformation(){

    private val ID = javaClass.name

    private val ID_BYTES = ID.toByteArray(Key.CHARSET)

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return TransformationUtils.roundedCorners(pool,toTransform,topLeftRadius,topRightRadius,bottomRightRadius,bottomLeftRadius) //重置4个角bitmap
    }

    override fun equals(other: Any?): Boolean {
        return other is RoundTransformation
    }

    override fun hashCode(): Int {
        return Util.hashCode(ID.hashCode(), Util.hashCode(topLeftRadius))
    }
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

        val radiusData = ByteBuffer.allocate(4).putInt(topLeftRadius.toInt()).array()
        messageDigest.update(radiusData)
    }
}