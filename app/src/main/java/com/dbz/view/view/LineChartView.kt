package com.dbz.view.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import kotlin.math.max

/**
 * description:
 *
 * @author Db_z
 * date 2020/3/18 14:49
 * @version V1.0
 */
class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 折线画笔
     */
    private lateinit var mLinePaint: Paint

    /**
     * Y轴提示框和Y轴点文本画笔
     */
    private lateinit var mDotTextPaint: Paint

    /**
     * X轴文本画笔
     */
    private lateinit var mXTextPaint: Paint

    /**
     * XY轴画笔
     */
    private lateinit var mXYPaint: Paint

    /**
     * 折线点位画笔
     */
    private lateinit var mDotPaint: Paint

    /**
     * 点位文本大小
     */
    private var mDotTextSize: Float = 13.dp2px().toFloat()

    /**
     * X轴文本大小
     */
    private var mXTextSize: Float = 13.dp2px().toFloat()

    /**
     * X轴文本颜色
     */
    private var mXTextColor = Color.BLACK

    /**
     * 背景颜色
     */
    private var mBackColor = Color.WHITE

    /**
     * 点位文本颜色
     */
    private var mDotTextColor = Color.WHITE

    /**
     * XY轴颜色
     */
    private var mXYLineColor = Color.parseColor("#cccccc")

    /**
     * 折线颜色
     */
    private var mLineColor = Color.RED

    /**
     * X轴线宽度
     */
    private var mXLineWidth: Float = 1.dp2px().toFloat()

    /**
     * 选中点的大小
     */
    private val mSelectDotSize: Int = 5.dp2px()

    /**
     * 未选中点的大小
     */
    private val mUnSelectDotSize: Int = 3.dp2px()

    /**
     * 当前选中的点
     */
    private var mCurrentSelectDot = 1

    /**
     * 线距点的距离
     */
    private var mDotSpacing: Float = 10.dp2px().toFloat()

    /**
     * X轴文本上下的间距
     */
    private val mTopBottomSpacing: Int = 5.dp2px()

    /**
     * Y 轴距离上边距
     */
    private val mYTopInterval: Int = 40.dp2px()

    /**
     * X轴直线原点
     */
    private val mXDotOrigin: Float = 15.dp2px().toFloat()
    private var mXTextXLineSpacing = 0

    /**
     * X轴文本高度
     */
    private var mXTextHeight = 0
    private var mWidth = 0
    private var mHeight = 0

    /**
     * 整体数据最大值
     */
    private var max = 0f
    private var mXValue: MutableList<XValue> = mutableListOf()
    private var mLineValue: MutableList<LineValue> = mutableListOf()

    init {
        init(context, attrs)
        initView()
    }

    fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineChartView)
        mBackColor = typedArray.getColor(R.styleable.LineChartView_dbz_back_color, Color.WHITE)
        mDotTextColor =
            typedArray.getColor(R.styleable.LineChartView_dbz_dot_text_color, Color.WHITE)
        mXTextColor = typedArray.getColor(R.styleable.LineChartView_dbz_x_text_color, Color.BLACK)
        mLineColor = typedArray.getColor(R.styleable.LineChartView_dbz_line_chart_color, Color.RED)
        mXYLineColor = typedArray.getColor(R.styleable.LineChartView_dbz_x_line_color, mXYLineColor)
        mXLineWidth =
            typedArray.getDimension(R.styleable.LineChartView_dbz_x_line_width, mXLineWidth)
        mXTextSize = typedArray.getDimension(R.styleable.LineChartView_dbz_x_text_size, mXTextSize)
        mDotTextSize =
            typedArray.getDimension(R.styleable.LineChartView_dbz_dot_text_size, mDotTextSize)
        typedArray.recycle()
    }

    private fun initView() {
        mXYPaint = Paint().apply {
            isAntiAlias = true
            color = mXYLineColor
        }
        mLinePaint = Paint().apply {
            isAntiAlias = true
            strokeWidth = mXLineWidth
            color = mLineColor
        }
        mDotPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.WHITE
        }
        mXTextPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mXTextColor
            textSize = mXTextSize
        }
        mDotTextPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mDotTextColor
            textSize = mDotTextSize
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            reSetCalculation()
        }
    }

    /**
     * 重新计算布局距离
     */
    private fun reSetCalculation() {
        mWidth = width
        mHeight = height
        if (mXValue.size == 0 || mLineValue.size == 0) return
        // 把屏幕整体分成 数据的个数份 多少数据就多少份 每份的间距
        mDotSpacing = ((mWidth - (mWidth - paddingStart - paddingEnd) / mLineValue.size) / mLineValue.size).toFloat()
        // 获取X轴数据文本最后一个的高度
        val rect: Rect = getTextBounds(mXValue[mXValue.size - 1].value + "-", mXTextPaint)
        // 文本立着显示，宽度即是高度
        mXTextHeight = rect.width()
        // 文本距离X轴直线的高度
        mXTextXLineSpacing = mHeight - mXTextHeight - mTopBottomSpacing * 2
        // 取数据中的最大值
        for (i in mLineValue.indices) {
            max = max(max, mLineValue[i].num)
        }
        if (max == 0f) max = 1f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackColor)
        drawXLine(canvas)
        drawXTextValue(canvas)
        drawDotLine(canvas)
        drawDot(canvas)
    }

    /**
     * 绘制X轴直线
     */
    private fun drawXLine(canvas: Canvas) {
        canvas.drawLine(mXDotOrigin, mXTextXLineSpacing.toFloat(),
            (mWidth - paddingEnd - mXDotOrigin), mXTextXLineSpacing.toFloat(), mXYPaint)
    }

    /**
     * 绘制垂直点位的线 和 X轴文本
     */
    private fun drawXTextValue(canvas: Canvas) {
        for (i in mXValue.indices) {
            val x = (mDotSpacing * i + mDotSpacing)
            if (x >= mDotSpacing) {
                // 如果是选中的 绘制垂直线
                if (i == mCurrentSelectDot - 1) {
                    canvas.drawLine(x, mXTextXLineSpacing.toFloat(), x, mXValue[i].num, mXYPaint)
                }
                //绘制X轴文本
                val rect: Rect = getTextBounds(mXValue[i].value, mXTextPaint)
                mXTextHeight = rect.width()
                val xV = x - rect.width().toFloat() / 2 + mTopBottomSpacing
                val yV: Float = (mHeight - mXTextHeight + mXLineWidth)
                // 画布旋转80度
                canvas.rotate(-80f, xV, yV)
                canvas.drawText(mXValue[i].value,
                    xV - (mXTextHeight - mXTextHeight.toFloat() / 3),
                    yV + (mXTextHeight - mXTextHeight.toFloat() / 2),
                    mXTextPaint
                )
                canvas.rotate(80f, xV, yV)
            }
        }
    }

    /**
     * 绘制点折线
     */
    private fun drawDotLine(canvas: Canvas) {
        if (mLineValue.size == 0) return
        mLinePaint.style = Paint.Style.STROKE
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight = (mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - paddingTop - mYTopInterval).toFloat()
        val path = Path()
        var x = mDotSpacing
        // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
        var y = mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - mLineValue[0].num * totalHeight / max
        path.moveTo(x, y)
        for (i in mLineValue.indices) {
            x = (mDotSpacing * i + mDotSpacing)
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            y = mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - mLineValue[i].num * totalHeight / max
            path.lineTo(x, y)
        }
        canvas.drawPath(path, mLinePaint)
    }

    /**
     * 绘制点位置
     */
    private fun drawDot(canvas: Canvas) {
        var x: Float
        var y: Float
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight = (mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - paddingTop - mYTopInterval).toFloat()
        for (i in mLineValue.indices) {
            x = (mDotSpacing * i + mDotSpacing)
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            y = mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - mLineValue[i].num * totalHeight / max
            drawCircle(canvas, x, y, mUnSelectDotSize)
            if (i == mCurrentSelectDot - 1) {
                drawCircle(canvas, x, y, mSelectDotSize)
                drawTextBox(canvas, x, y - 8.dp2px(), mLineValue[i].value)
            }
        }
    }

    /**
     * 绘制点
     */
    private fun drawCircle(canvas: Canvas, x: Float, y: Float, dp: Int) {
        mDotPaint.style = Paint.Style.FILL
        mDotPaint.color = Color.WHITE
        canvas.drawCircle(x, y, dp.toFloat(), mDotPaint)
        mDotPaint.style = Paint.Style.STROKE
        mDotPaint.color = mLineColor
        mDotPaint.strokeWidth = mXLineWidth
        canvas.drawCircle(x, y, dp.toFloat(), mDotPaint)
    }

    /**
     * 绘制提示框
     */
    private fun drawTextBox(canvas: Canvas, x: Float, y: Float, text: String) {
        val rect: Rect = getTextBounds(text, mDotTextPaint)
        val dp5: Int = 5.dp2px()
        val dp6: Int = 6.dp2px()
        val dp20: Int = 20.dp2px()
        // 绘制路径三角
        val path = Path()
        path.moveTo(x, y)
        path.lineTo(x - dp6, y - dp6)
//        path.lineTo(x - dp20, y - dp6);
//        path.lineTo(x - dp20, y - dp6 - dp20);
//        path.lineTo(x + dp20, y - dp6 - dp20);
//        path.lineTo(x + dp20, y - dp6);
//        path.quadTo(x + dp18, y - dp4, x - dp18, y - dp4);
        path.lineTo(x + dp6, y - dp6)
        path.lineTo(x, y)
        path.close()
        mDotTextPaint.style = Paint.Style.FILL
        mDotTextPaint.color = Color.parseColor("#6f6f6f")
        canvas.drawPath(path, mDotTextPaint)
        // 绘制矩形， 周边圆角
        val rectF = RectF(x - dp20, y - dp5, x + dp20, y - dp20 - dp6)
        canvas.drawRoundRect(rectF, dp5.toFloat(), dp5.toFloat(), mDotTextPaint)
        mDotTextPaint.color = Color.WHITE
        mDotTextPaint.textSize = mDotTextSize
        // y点计算  以下两种方法均可
        // x减去文本的宽度  y - 提示框距离点的高度 - 三角的高度 - 提示框 / 2
//        canvas.drawText(text, x - (float) rect.width() / 2, y - dpToPx(10) - dp6 - dpToPx(5) - rectF.height() / 2, mDotTextPaint);
        // x减去文本的宽度  y - 三角的高度 - 文本高度 / 2
        canvas.drawText(
            text, 0, text.length,
            x - rect.width().toFloat() / 2,
            y - dp6 - rect.height().toFloat() / 2,
            mDotTextPaint
        )
    }

    /**
     * 点击点位绘制
     */
    private fun clickAction(event: MotionEvent) {
        val dp8 = 8.dp2px()
        val eventX = event.x
        val eventY = event.y
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight: Float = (mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - paddingTop - mYTopInterval).toFloat()
        for (i in mLineValue.indices) {
            val x = (mDotSpacing * i + mDotSpacing)
            var y: Float = mHeight - paddingBottom - (mHeight - mXTextXLineSpacing) - mLineValue[i].num * totalHeight / max
            if (eventX >= x - dp8 && eventX <= x + dp8 && eventY >= y - dp8 && eventY <= y + dp8 && mCurrentSelectDot != i + 1) {
                mCurrentSelectDot = i + 1
                invalidate()
                return
            }
            val rect: Rect = getTextBounds(mXValue[i].value, mXTextPaint)
            y = (mXTextXLineSpacing - mXLineWidth + rect.height())
            if (eventX >= x - rect.height().toFloat() / 2 - dp8 &&
                eventX <= x + rect.height() + dp8.toFloat() / 2 &&
                eventY >= y - dp8 &&
                eventY <= y + rect.width() + dp8 &&
                mCurrentSelectDot != i + 1
            ) {
                mCurrentSelectDot = i + 1
                invalidate()
                return
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                clickAction(event)
                this.parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_CANCEL -> this.parent.requestDisallowInterceptTouchEvent(false)
        }
        return true
    }

    fun setXYLineValue(mXValue: MutableList<XValue>, mLineValue: MutableList<LineValue>) {
        this.mXValue = mXValue
        this.mLineValue = mLineValue
        reSetCalculation()
        invalidate()
    }

    /**
     * 获取丈量文本的矩形
     *
     * @param text  文本
     * @param paint 画笔
     */
    private fun getTextBounds(text: String, paint: Paint): Rect {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect
    }

    class XValue(val num: Float, val value: String)

    class LineValue(val num: Float, val value: String)
}