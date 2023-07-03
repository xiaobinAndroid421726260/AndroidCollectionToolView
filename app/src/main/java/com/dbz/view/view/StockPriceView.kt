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
 * @Date 2021/4/15 14:20
 */
class StockPriceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * X轴画笔
     */
    private lateinit var mXPaint: Paint

    /**
     * X轴文本
     */
    private lateinit var mXTextPaint: Paint

    /**
     * Y轴文本
     */
    private lateinit var mYTextPaint: Paint

    /**
     * 背景虚线画笔
     */
    private lateinit var mDottedPaint: Paint

    /**
     * 折线画笔
     */
    private lateinit var mLinePaint: Paint

    /**
     * 点位画笔
     */
    private lateinit var mPointPaint: Paint

    /**
     * 点位直线画笔
     */
    private lateinit var mLinePointPaint: Paint

    /**
     * 背景颜色
     */
    private var mBackgroundColor = Color.WHITE

    /**
     * X、Y轴颜色
     */
    private var mXYColor = Color.parseColor("#D8D8D8")

    /**
     * X轴文本颜色
     */
    private var mXTextColor = Color.parseColor("#333333")

    /**
     * Y轴文本颜色
     */
    private var mYTextColor = Color.parseColor("#999999")

    /**
     * 折线阴影颜色
     */
    private var mLineShadowColor = Color.parseColor("#73FFE4E4")

    /**
     * 点位文本颜色
     */
    private var mPointTextSize = Color.parseColor("#333333")

    /**
     * 点位颜色
     */
    private var mPointColor = Color.parseColor("#FFFFFF")

    /**
     * 折线颜色
     */
    private var mLineColor = Color.parseColor("#FF0000")

    /**
     * 折线点位直线颜色
     */
    private var mLinePointColor = Color.parseColor("#FF8484")

    /**
     * 折线颜色
     */
    private var mPointTextColor = Color.parseColor("#333333")

    /**
     * X轴文本大小
     */
    private var mXTextSize = 13.dp2px().toFloat()

    /**
     * Y轴文本大小
     */
    private var mYTextSize = 12.dp2px().toFloat()

    /**
     * 折线宽度
     */
    private var mLineWidth = 2.dp2px().toFloat()

    /**
     * 两点之间的间隔
     */
    private var mInterval = 30.dp2px()

    /**
     * X轴文本距左边间距
     */
    private val mXTextLeftInterval = 15.dp2px()

    /**
     * X轴右边间距
     */
    private val mXRightInterval = 25.dp2px()

    /**
     * X轴距底边的距离
     */
    private val mXBottomInterval = 10.dp2px()

    /**
     * X、 Y轴原点距左边的距离
     */
    private var mYLeftInterval = 30.dp2px()

    /**
     * 当前选中的点 默认在最后一位
     */
    private var mCurrentSelectPoint = 1

    /**
     * X 轴 第一个坐标
     */
    private var mXFirstPoint = 0f

    /**
     * Y轴距离上边距
     */
    private val mYTopInterval = 40.dp2px()

    /**
     * 宽、高
     */
    private var mWidth = 0

    /**
     * 宽、高
     */
    private var mHeight = 0

    /**
     * 整体数据最大值
     */
    private var max = 1f
    private var onSelectedActionClick: OnSelectedActionClick? = null
    private var yRect: Rect? = null

    private var mXStartText = "2021.03.12"
    private var mXEndText = "2021.04.11"

    private var mXValue = arrayListOf<XValue>()
    private var mYValue = arrayListOf<YValue>()

    init {
        initView(context, attrs, defStyleAttr)
        initPaint()
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.StockPriceView, defStyleAttr, 0)
        mBackgroundColor =
            typedArray.getColor(R.styleable.StockPriceView_background_color_dbz, Color.WHITE)
        mLinePointColor =
            typedArray.getColor(R.styleable.StockPriceView_line_point_color_dbz, mLinePointColor)
        mLineColor = typedArray.getColor(R.styleable.StockPriceView_line_color_dbz, mLineColor)
        mPointColor = typedArray.getColor(R.styleable.StockPriceView_point_color_dbz, mPointColor)
        mPointTextColor =
            typedArray.getColor(R.styleable.StockPriceView_point_text_color_dbz, mPointTextColor)
        mXYColor = typedArray.getColor(R.styleable.StockPriceView_x_y_color_dbz, mXYColor)
        mXTextColor = typedArray.getColor(R.styleable.StockPriceView_x_text_color_dbz, mXTextColor)
        mYTextColor = typedArray.getColor(R.styleable.StockPriceView_y_text_color_dbz, mYTextColor)
        mLineShadowColor =
            typedArray.getColor(R.styleable.StockPriceView_line_shadow_color_dbz, mLineShadowColor)
        mPointTextSize = typedArray.getDimension(
            R.styleable.StockPriceView_point_text_size_dbz,
            mPointTextSize.toFloat()
        ).toInt()
        mLineWidth = typedArray.getDimension(R.styleable.StockPriceView_line_width_dbz, mLineWidth)
        mXTextSize = typedArray.getDimension(R.styleable.StockPriceView_x_text_size_dbz, mXTextSize)
        mYTextSize = typedArray.getDimension(R.styleable.StockPriceView_y_text_size_dbz, mYTextSize)
        typedArray.recycle()
    }

    private fun initPaint() {
        mXPaint = Paint().apply {
            isAntiAlias = true
            color = mXYColor
            strokeWidth = 1.dp2px().toFloat()
        }
        mXTextPaint = Paint().apply {
            isAntiAlias = true
            color = mXTextColor
            textSize = mXTextSize
        }
        mYTextPaint = Paint().apply {
            isAntiAlias = true
            color = mYTextColor
            textSize = mYTextSize
        }
        mDottedPaint = Paint().apply {
            isAntiAlias = true
            color = mXYColor
            style = Paint.Style.STROKE
            strokeWidth = 1.dp2px().toFloat()
        }
        // DashPathEffect () 数组 第一个是线的宽度 第二个数据是虚线间隔，
        mDottedPaint.pathEffect =
            DashPathEffect(floatArrayOf(4.dp2px().toFloat(), 2.dp2px().toFloat()), 0f)
        mLinePaint = Paint().apply {
            isAntiAlias = true
            color = mLineShadowColor
            strokeWidth = 1.dp2px().toFloat()
            style = Paint.Style.FILL
        }
        mPointPaint = Paint().apply {
            isAntiAlias = true
            color = mLineColor
            strokeWidth = mLineWidth
            style = Paint.Style.STROKE
        }
        mLinePointPaint = Paint().apply {
            isAntiAlias = true
            color = mLinePointColor
            strokeWidth = 1.dp2px().toFloat()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mWidth = width
            mHeight = height
        }
    }

    /**
     * 计算文本间距，和第一个点的位置  取数据最大值
     */
    private fun calculation() {
        val text = if (mYValue.size == 0) "00-00" else mYValue[mYValue.size - 1].value
        // 测量Y轴数据的文本宽度
        yRect = getTextBounds(text, mYTextPaint)
        // 计算X轴的左边距
        mYLeftInterval = yRect!!.width() + mXTextLeftInterval * 2
        // 每个点位的间距 mXValue.size() - 2 减去2 是因为两边的点各占一个  不用间距
        mInterval = (mWidth - mYLeftInterval - mXRightInterval) / (mXValue.size - 2)
        // 第一个X轴点的位置
        mXFirstPoint = mYLeftInterval.toFloat()
        // 遍历数据最大值 如果为0那么默认为1
        for (i in mXValue.indices) {
            max = max(max, mXValue[i].value.toFloat())
        }
        if (max == 0f) {
            max = 1f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calculation()
        canvas.drawColor(mBackgroundColor)
        // 绘制X轴时间文本
        drawBottomText(canvas)
        // 绘制X轴
        drawXLine(canvas)
        // 绘制Y轴文本
        drawYText(canvas)
        // 绘制背景虚线
        drawBackDottedLine(canvas)
        // 绘制阴影折线
        drawLine(canvas)
        // 绘制折线点和提示文本
        drawLinePoint(canvas)
    }

    /**
     * 绘制X轴时间文本
     */
    private fun drawBottomText(canvas: Canvas) {
        val endText: Rect = getTextBounds(mXEndText, mXTextPaint)
        canvas.drawText(
            mXStartText,
            mXTextLeftInterval.toFloat(),
            (mHeight - mXBottomInterval).toFloat(),
            mXTextPaint
        )
        canvas.drawText(
            mXEndText,
            (mWidth - mXTextLeftInterval - endText.width()).toFloat(),
            (mHeight - mXBottomInterval).toFloat(),
            mXTextPaint
        )
    }

    /**
     * 绘制X轴
     */
    private fun drawXLine(canvas: Canvas) {
        val startText: Rect = getTextBounds(mXStartText, mXTextPaint)
        // 距离底边的距离 = 底边距离 * 2 + X轴文本高度
        val xBottom =
            mHeight - paddingTop - paddingBottom - mXBottomInterval * 2 - startText.height()
        canvas.drawLine(
            mYLeftInterval.toFloat(),
            xBottom.toFloat(),
            (mWidth - mXRightInterval + 4.dp2px()).toFloat(),
            xBottom.toFloat(),
            mXPaint
        )
    }

    /**
     * 绘制Y轴文本
     */
    private fun drawYText(canvas: Canvas) {
        val startText: Rect = getTextBounds(mXStartText, mXTextPaint)
        for (i in mYValue.indices) {
            // 绘制区域 = 总高度 - 下边距 - 上边距
            // 下边距 = 文本高度 + 文本距离上下的间距
            // 绘制区域 / (绘制数据的数量 - 1) (减一是计算绘制之间的间距， 如果不减一， 那么在开始第一个绘制时会多出一段间距)
            val y: Float =
                (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - mYTopInterval - startText.height()).toFloat() / (mYValue.size - 1)
            // X轴 文本水平居中X轴    Y轴 从最大值到最小值，反着绘制， Y点从上到下 但要加 上边距
            // Y轴文本  距离左边边距, Y轴文本的宽度 / 2  居中绘制
            canvas.drawText(
                mYValue[mYValue.size - (i + 1)].value,
                mXTextLeftInterval.toFloat(),
                y * i + mYTopInterval,
                mYTextPaint
            )
        }
    }

    /**
     * 绘制背景虚线
     */
    private fun drawBackDottedLine(canvas: Canvas) {
        val startText: Rect = getTextBounds(mXStartText, mXTextPaint)
        for (i in mYValue.indices) {
            val y: Float =
                (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - mYTopInterval - startText.height()).toFloat() / (mYValue.size - 1)
            // x轴距离左边 = Y轴文本宽度 + Y轴文本左右边距
            canvas.drawLine(
                (mXTextLeftInterval * 2 + yRect!!.width()).toFloat(),
                y * i + mYTopInterval,
                (mWidth - mXRightInterval + 4.dp2px()).toFloat(),
                y * i + mYTopInterval,
                mDottedPaint
            )
        }
    }

    /**
     * 绘制阴影折线
     */
    private fun drawLine(canvas: Canvas) {
        if (mXValue.size <= 0) return
        val startText: Rect = getTextBounds(mXStartText, mXTextPaint)
        val path = Path()
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight =
            (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - startText.height() - mYTopInterval).toFloat()
        // 起点x、y轴开始绘制
        var x = mXFirstPoint
        var y: Float =
            mHeight - paddingBottom - mXBottomInterval * 2 - startText.height() - mXValue[0].value.toFloat() * totalHeight / max
        // 绘制x、y轴左下角起点
        path.moveTo(
            mYLeftInterval.toFloat(),
            (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - startText.height()).toFloat()
        )
        // 绘制第一个点的位置
        path.lineTo(x, y)
        // 因为绘制了第一个点，所以i起始是1
        for (i in 1 until mXValue.size) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            x = mXFirstPoint + mInterval * i
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            y =
                mHeight - paddingBottom - mXBottomInterval * 2 - startText.height() - mXValue[i].value.toFloat() * totalHeight / max
            path.lineTo(x, y)
        }
        // 如果不绘制点位到下方的路径， 那么 不会实现全部阴影效果， x 就是 绘制最后一个点的位置， y 就是底边的位置
        path.lineTo(x, (mHeight - mXBottomInterval * 2 - startText.height()).toFloat())
        canvas.drawPath(path, mLinePaint)

        // 绘制折线
        val linePath = Path()
        // 起点x、y轴开始绘制
        var x1: Float
        var y1: Float =
            mHeight - paddingBottom - mXBottomInterval * 2 - startText.height() - mXValue[0].value.toFloat() * totalHeight / max
        // 绘制第一个点的位置
        linePath.moveTo(mXFirstPoint, y1)
        // 因为绘制了第一个点，所以i起始是1
        for (i in 1 until mXValue.size) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            x1 = mXFirstPoint + mInterval * i
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            y1 =
                mHeight - paddingBottom - mXBottomInterval * 2 - startText.height() - mXValue[i].value.toFloat() * totalHeight / max
            linePath.lineTo(x1, y1)
        }
        // 绘制折线
        mPointPaint.color = mLineColor
        mPointPaint.style = Paint.Style.STROKE
        canvas.drawPath(linePath, mPointPaint)
    }

    /**
     * 绘制折线和提示文本
     */
    private fun drawLinePoint(canvas: Canvas) {
        val startText: Rect = getTextBounds(mXStartText, mXTextPaint)
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight =
            (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - startText.height() - mYTopInterval).toFloat()
        for (i in mXValue.indices) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            val x: Float = mInterval * i + mXFirstPoint
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            val y: Float =
                mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - startText.height() - mXValue[i].value.toFloat() * totalHeight / max
            if (mCurrentSelectPoint == i + 1) {
                // 绘制垂直直线、点、文本
                drawVerticalLinePointText(canvas, x, startText, mXValue[i].num)
                // 绘制选中的点
                drawCurrentSelectPoint(canvas, x, y)
                // 绘制选中提示点
                drawCurrentTextBox(canvas, x, y - 10.dp2px(), mXValue[i].value)
            }
        }
    }

    /**
     * 绘制垂直直线、点、文本
     */
    private fun drawVerticalLinePointText(canvas: Canvas, x: Float, startText: Rect, text: String) {
        val y = mHeight - paddingBottom - mXBottomInterval * 2 - startText.height()
        mLinePointPaint.style = Paint.Style.STROKE
        mLinePointPaint.color = mLinePointColor
        canvas.drawLine(x, y.toFloat(), x, mYTopInterval.toFloat(), mLinePointPaint)
        mLinePointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y.toFloat(), 4.dp2px().toFloat(), mLinePointPaint)
        mLinePointPaint.color = mPointColor
        canvas.drawCircle(x, y.toFloat(), 2.dp2px().toFloat(), mLinePointPaint)
        val rect: Rect = getTextBounds(text, mXTextPaint)
        canvas.drawText(text,
            x - rect.width().toFloat() / 2,
            y - 6.dp2px() - rect.height().toFloat() / 2,
            mXTextPaint
        )
    }

    /**
     * 绘制选中的点
     */
    private fun drawCurrentSelectPoint(canvas: Canvas, x: Float, y: Float) {
        mPointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, 4.dp2px().toFloat(), mPointPaint)
        mPointPaint.color = mPointColor
        mPointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, 2.dp2px().toFloat(), mPointPaint)
    }

    /**
     * 绘制选中提示框
     */
    private fun drawCurrentTextBox(canvas: Canvas, x: Float, y: Float, text: String) {
        val rect: Rect = getTextBounds(text, mXTextPaint)
        // y点计算  以下两种方法均可
        // x减去文本的宽度  y - 三角的高度 - 文本高度 / 2
        canvas.drawText(text,
            x - rect.width().toFloat() / 2,
            y - 6.dp2px() - rect.height().toFloat() / 2,
            mXTextPaint
        )
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.parent.requestDisallowInterceptTouchEvent(true) //当该view获得点击事件，就请求父控件不拦截事件
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_MOVE -> clickAction(event)
            MotionEvent.ACTION_UP -> {
                clickAction(event)
                this.parent.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_CANCEL -> this.parent.requestDisallowInterceptTouchEvent(false)
        }
        return true
    }

    /**
     * 点击X轴坐标或者折线节点
     *
     * @param event 事件
     */
    private fun clickAction(event: MotionEvent) {
        val startText = getTextBounds(mXStartText, mXTextPaint)
        val dp3: Int = 3.dp2px()
        val eventX = event.x
        val eventY = event.y
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val bottomInterval =
            (mHeight - paddingBottom - paddingTop - mXBottomInterval * 2 - startText.height() - mYTopInterval).toFloat()
        for (i in mXValue.indices) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            val x = mInterval * i + mXFirstPoint
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            if (eventX >= x - dp3 && eventX <= x + dp3 && eventY >= mYTopInterval && eventY <= bottomInterval) {
                mCurrentSelectPoint = i + 1
                invalidate()
                if (onSelectedActionClick != null) {
                    onSelectedActionClick!!.onActionClick(i, mXValue[i].num, mXValue[i].value)
                }
                return
            }
        }
    }

    /**
     * 设置开始日期数据
     */
    fun setXStartText(mXStartText: String?) {
        this.mXStartText = mXStartText!!
    }

    /**
     * 设置结束日期数据
     */
    fun setXEndText(mXEndText: String?) {
        this.mXEndText = mXEndText!!
    }

    /**
     * 设置数据
     */
    fun setValue(xValue: ArrayList<XValue>, yValue: ArrayList<YValue>) {
        mXValue = xValue
        mYValue = yValue
        // 设置选中的位置在最后一个
        mCurrentSelectPoint = mXValue.size
        invalidate()
    }

    /**
     * 设置当前选中的点
     */
    fun setCurrentSelectPoint(currentSelectPoint: Int) {
        mCurrentSelectPoint = currentSelectPoint
        invalidate()
    }

    fun setOnSelectedActionClick(onSelectedActionClick: OnSelectedActionClick?) {
        this.onSelectedActionClick = onSelectedActionClick
    }

    class XValue(val num: String, val value: String)

    class YValue(val num: Int, val value: String)

    interface OnSelectedActionClick {
        fun onActionClick(position: Int, num: String?, text: String?)
    }
}