package com.dbz.view.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import java.text.DecimalFormat

/**
 * description:
 *
 * @author Db_z
 * @Date 2020/3/18 17:25
 * @version V1.0
 */
class CustomHorizontalProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 进度条的圆角弧度
     */
    private var mRadius: Float = 5f.dp2px().toFloat()

    /**
     * 进度条的背景色
     */
    private var mProgressBackColor = Color.BLUE

    /**
     * 进度条颜色
     */
    private var mProgressColor = Color.GREEN

    /**
     * 进度条的文本色
     */
    private var mProgressTextColor = Color.WHITE

    /**
     * 是否显示文本 (默认不显示)
     */
    private var isShowProgressText = false

    /**
     * 是否显示文本百分比 (默认显示)
     */
    private var isShowRate = true

    /**
     * 进度条的最大进度
     */
    private var mProgressMax = 100

    /**
     * 进度条的当前进度
     */
    private var mProgress = 50

    /**
     * 画笔
     */
    private lateinit var mPaint: Paint

    init {
        initView(context, attrs, defStyleAttr)
        initPaint()
    }

    private fun initView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        /*获取自定义参数的颜色值*/
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomHorizontalProgressBar,
            defStyleAttr,
            0
        )
        val count = array.indexCount
        for (i in 0 until count) {
            when (val attr = array.getIndex(i)) {
                R.styleable.CustomHorizontalProgressBar_dbz_radius -> mRadius =
                    array.getDimension(attr, mRadius)
                R.styleable.CustomHorizontalProgressBar_dbz_progress_bar_back_color -> mProgressBackColor =
                    array.getColor(attr, Color.parseColor("#E6E6E6"))
                R.styleable.CustomHorizontalProgressBar_dbz_progress_color -> mProgressColor =
                    array.getColor(attr, mProgressColor)
                R.styleable.CustomHorizontalProgressBar_dbz_progress_bar_text_color -> mProgressTextColor =
                    array.getColor(attr, mProgressTextColor)
                R.styleable.CustomHorizontalProgressBar_dbz_progress_max -> mProgressMax =
                    array.getInteger(attr, mProgressMax)
                R.styleable.CustomHorizontalProgressBar_dbz_progress -> mProgress =
                    array.getInteger(attr, mProgress)
                R.styleable.CustomHorizontalProgressBar_dbz_is_show_progress_text -> isShowProgressText =
                    array.getBoolean(attr, true)
            }
        }
        array.recycle()
    }

    private fun initPaint() {
        mPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRoundRect(canvas)
        drawProgress(canvas)
        drawText(canvas)
    }

    /**
     * 画进度条的背景颜色
     */
    private fun drawRoundRect(canvas: Canvas) {
        mPaint.color = mProgressBackColor
        val rectF = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)
    }

    /**
     * 画进度条进度
     */
    private fun drawProgress(canvas: Canvas) {
        mPaint.color = mProgressColor
        val rectF = RectF(0f, 0f, measuredWidth.toFloat() * mProgress / mProgressMax,
            measuredHeight.toFloat()
        )
        canvas.drawRoundRect(rectF, mRadius, mRadius, mPaint)
    }

    /**
     * 画进度条文本
     */
    private fun drawText(canvas: Canvas) {
        if (isShowProgressText) {
            mPaint.color = mProgressTextColor
            mPaint.textSize = this.measuredHeight / 1.2f
            var text = "$mProgress%"
            val x = this.measuredWidth.toFloat() * mProgress / mProgressMax - mPaint.measureText(text) - 10
            val y = this.measuredHeight.toFloat() / 2f - mPaint.fontMetrics.ascent / 2f - mPaint.fontMetrics.descent / 2f
            // 显示百分比
            if (isShowRate) {
                text = getPercent(mProgress, mProgressMax) + "%"
            }
            if (mProgress > 10) { //解决百分比显示不全问题
                canvas.drawText(text, x, y, mPaint)
            } else {
                canvas.drawText(text, (text.length + 10).toFloat(), y, mPaint)
            }
        }
    }

    /**
     * 返回当前百分比
     */
    private fun getPercent(progress: Int, max: Int): String {
        val decimalFormat = DecimalFormat("#")
        return decimalFormat.format((progress * 100 / max).toLong())
    }

    /**
     * 设置当前进度
     */
    fun setProgress(progress: Int) {
        mProgress = progress
        if (mProgress > mProgressMax) {
            mProgress = mProgressMax
        }
        if (mProgress < 0) {
            mProgress = 0
        }
        invalidate()
    }

    /**
     * 设置圆角
     */
    fun setRadius(radius: Int): CustomHorizontalProgressBar {
        mRadius = radius.dp2px().toFloat()
        return this
    }

    /**
     * 是否显示文本 默认不显示
     */
    fun setShowProgressText(showProgressText: Boolean): CustomHorizontalProgressBar {
        isShowProgressText = showProgressText
        return this
    }

    /**
     * 是否显示文本百分比 默认显示
     */
    fun setShowRate(isShowRate: Boolean): CustomHorizontalProgressBar {
        this.isShowRate = isShowRate
        return this
    }

    /**
     * 设置字体颜色
     */
    fun setProgressTextColor(progressTextColor: Int): CustomHorizontalProgressBar {
        mProgressTextColor = progressTextColor
        return this
    }

    /**
     * 设置进度背景颜色
     */
    fun setProgressBackColor(progressBackColor: Int): CustomHorizontalProgressBar {
        mProgressBackColor = progressBackColor
        return this
    }

    /**
     * 设置进度颜色
     */
    fun setProgressColor(progressColor: Int): CustomHorizontalProgressBar {
        mProgressColor = progressColor
        return this
    }

    /**
     * 设置进度最大值
     */
    fun setProgressMax(progressMax: Int): CustomHorizontalProgressBar {
        mProgressMax = progressMax
        return this
    }
}