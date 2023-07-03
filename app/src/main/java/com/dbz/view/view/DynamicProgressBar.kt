package com.dbz.view.view

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.blankj.utilcode.util.ScreenUtils
import com.dbz.view.R
import com.dbz.view.ext.dp2px

/**
 * description:
 *
 * @author Db_z
 * @Date 2022/3/15 9:37
 */
class DynamicProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DURATION = 5L // 间隔时间
        private const val SPEED_WIDTH = 2 // 运行宽度变化减多少值
        private const val UPDATE_UI = 0x1 // 更新UI
    }

    private var layoutParams: RelativeLayout.LayoutParams? = null
    private var llProgressbar: LinearLayout? = null
    private var textView: AppCompatTextView? = null
    private var viewDivider: AppCompatImageView? = null
    private var totalWidth = 0
    private var startX = 0
    private var current = 0f

    init {
        initView()
    }

    private fun initView(){
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_dynamic_progress, this, false)
        textView = view.findViewById(R.id.tv_num)
        llProgressbar = view.findViewById(R.id.ll_progressbar)
        viewDivider = view.findViewById(R.id.view_divider)
        val imageView = view.findViewById<AppCompatImageView>(R.id.iv_revision)
        imageView.setBackgroundResource(R.drawable.progress_revision)
        val animationDrawable = imageView.background as AnimationDrawable
        imageView.post { animationDrawable.start() }
        // 总宽度 = 屏幕宽度 - 距离两边的宽度
        totalWidth = ScreenUtils.getScreenWidth() - 100.dp2px()
        layoutParams = viewDivider?.layoutParams as RelativeLayout.LayoutParams
        addView(view)
    }

    /**
     * 设置进度
     *
     * @param startX   平移开始值
     * @param endPoint 平移结束值
     */
    private fun setProgress(startX: Float, endPoint: Float) {
        val totalWidth: Int = ScreenUtils.getScreenWidth() - 100.dp2px()
        val endX = totalWidth - endPoint
        val animation = TranslateAnimation(startX, endX, 0f, 0f)
        animation.duration = DURATION
        animation.fillAfter = true
        llProgressbar?.startAnimation(animation)
    }

    /**
     * 设置数据
     * @param progress 当前
     * @param totalProgress 总宽度
     */
    fun setRevisionProgress(progress: Int, totalProgress: Float) {
        if (totalProgress == 0f) return
        current = progress * totalWidth / totalProgress
        current = totalWidth - current
        textView?.text = progress.toString()
        val mProgressThread = ProgressThread()
        mProgressThread.start()
    }

    /**
     * 重置进度
     */
    fun reset() {
        startX = 0 // 重置X轴起始位置
        totalWidth = ScreenUtils.getScreenWidth() - 100.dp2px()
        // 重新设置遮挡位置的宽度
        layoutParams?.width = totalWidth
        viewDivider?.layoutParams = layoutParams
        // 移动到起始位置 0
        setProgress(0f, totalWidth.toFloat())
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == UPDATE_UI) {
                val bundle = msg.data
                val endPoint = bundle.getFloat("width")
                val startX = bundle.getFloat("startX")
                layoutParams?.width = endPoint.toInt()
                viewDivider?.layoutParams = layoutParams
                setProgress(startX, endPoint)
            }
        }
    }

    private inner class ProgressThread : Thread() {
        private var isStop = true
        override fun run() {
            super.run()
            while (isStop) {
                totalWidth -= SPEED_WIDTH
                var width: Float = totalWidth.toFloat()
                if (width <= current) {
                    width = current
                    isStop = false
                }
                startX += SPEED_WIDTH
                val bundle = Bundle()
                bundle.putFloat("width", width)
                bundle.putFloat("startX", startX.toFloat())
                val message: Message = mHandler.obtainMessage(UPDATE_UI)
                message.data = bundle
                mHandler.sendMessage(message)
                try {
                    sleep(DURATION)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}