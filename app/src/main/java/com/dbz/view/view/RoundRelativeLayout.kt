package com.dbz.view.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.dbz.view.R

/**
 * description:
 *
 * @author Db_z
 * @Date 2020/9/27 17:02
 */
class RoundRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var mPaint: Paint
    private var mRectF: RectF
    private var mRadius = 0f
    private var isClipBackground = true

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundRelativeLayout)
        mRadius = ta.getDimension(R.styleable.RoundRelativeLayout_dbz_layout_radius, 0f)
        isClipBackground =
            ta.getBoolean(R.styleable.RoundRelativeLayout_dbz_clip_back_ground, isClipBackground)
        ta.recycle()
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRectF = RectF()
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    fun setRadius(radius: Float) {
        mRadius = radius
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRectF[0f, 0f, w.toFloat()] = h.toFloat()
    }

    @SuppressLint("MissingSuperCall")
    override fun draw(canvas: Canvas) {
        // 28
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            draw28(canvas)
        } else {
            draw27(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dispatchDraw28(canvas)
        } else {
            dispatchDraw27(canvas)
        }
    }

    private fun draw27(canvas: Canvas) {
        if (isClipBackground) {
            canvas.saveLayer(mRectF, null)
            super.draw(canvas)
            canvas.drawPath(getPath(), mPaint)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    private fun draw28(canvas: Canvas) {
        if (isClipBackground) {
            canvas.save()
            canvas.clipPath(getPath())
            super.draw(canvas)
            canvas.restore()
        } else {
            super.draw(canvas)
        }
    }

    private fun dispatchDraw27(canvas: Canvas) {
        canvas.saveLayer(mRectF, null)
        super.dispatchDraw(canvas)
        canvas.drawPath(getPath(), mPaint)
        canvas.restore()
    }

    private fun dispatchDraw28(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(getPath())
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    private fun getPath(): Path {
        val path = Path()
        path.reset()
        path.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW)
        return path
    }
}