package com.ym.chat.widget.camera.listener


/**
 * author hbzhou
 * date 2019/12/13 11:13
 */
interface CaptureListener {

    fun takePictures()

    fun recordShort(time: Long)

    fun recordStart()

    fun recordEnd(time: Long)

    fun recordZoom(zoom: Float)

    fun recordError()
}