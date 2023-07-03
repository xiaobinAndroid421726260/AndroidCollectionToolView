package com.dbz.view.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import java.text.DecimalFormat
import kotlin.math.roundToInt

/**
 * description:
 *
 * @author Db_z
 * @Date 2020/10/18 9:45
 */
class RingChartVing @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * 内圆的画笔
     */
    private lateinit var mCenterPaint: Paint

    /**
     * 外圆的画笔
     */
    private lateinit var mCirclePaint: Paint

    /**
     * 外圆文本的画笔
     */
    private lateinit var mCircleTextPaint: Paint

    /**
     * 背景颜色
     */
    private var mBackgroundColor = Color.WHITE

    /**
     * 内圆的颜色
     */
    private var mCenterColor = 0

    /**
     * 外圆文本的大小
     */
    private var mCircleTextSize = 13.dp2px().toFloat()

    /**
     * 外圆大小
     */
    private var mCircleRadiusSize = 40.dp2px().toFloat()

    /**
     * 内圆大小
     */
    private var mCenterRadiusSize = 20.dp2px().toFloat()

    /**
     * 宽、高
     */
    private var mWidth = 0

    /**
     * 宽、高
     */
    private var mHeight = 0

    /**
     * X、Y轴中心点
     */
    private var mXCentralPoint = 0

    /**
     * X、Y轴中心点
     */
    private var mYCentralPoint = 0

    /**
     * 左边距
     */
    private var leftMargin = 0

    /**
     * 上边距
     */
    private var topMargin = 0

    /**
     * 开始绘制圆的角度
     */
    private var preAngle = -90f

    /**
     * 结束绘制圆的角度
     */
    private var endAngle = -90f

    private var preRate = 0f

    private var rate = 0.4f //点的外延距离  与  点所在圆半径的长度比率

    private var extendLineWidth = 20.dp2px().toFloat() //点外延后  折的横线的长度

    /**
     * 绘制外圆时延线文本的点
     */
    private val pointArcCenterMap = HashMap<Int, Point>()

    /**
     * 是否绘制内圆 默认绘制
     */
    private var isRing = true

    /**
     * 是否绘制外圆的文本占比 默认绘制
     */
    private var isShowRate = true

    /**
     * 文本占比 是否显示整体数据还是占据比例  默认显示 占比比例 整体数据
     */
    private var isShowValueData = false

    /**
     * 绘制圆时的颜色
     */
    private var mValueColor = arrayListOf<Int>()

    /**
     * 绘制圆时的数据
     */
    private var mValueData = arrayListOf<Float>()

    init {
        initView(context, attrs, defStyleAttr)
        initPaint()
    }

    @SuppressLint("Recycle")
    private fun initView(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RingChartVing, defStyleAttr, 0)
        mBackgroundColor = typedArray.getColor(
            R.styleable.RingChartVing_dbz_ring_background_color,
            mBackgroundColor
        )
        mCenterColor = typedArray.getColor(R.styleable.RingChartVing_dbz_radius_color, mCenterColor)
        mCenterRadiusSize =
            typedArray.getDimension(R.styleable.RingChartVing_dbz_radius_size, mCenterRadiusSize)
        mCircleRadiusSize = typedArray.getDimension(
            R.styleable.RingChartVing_dbz_circle_radius_size,
            mCircleRadiusSize
        )
        mCircleTextSize = typedArray.getDimension(
            R.styleable.RingChartVing_dbz_circle_radius_text_size,
            mCircleTextSize
        )
    }

    private fun initPaint() {
        mCenterPaint = Paint().apply {
            isAntiAlias = true
            color = mCenterColor
            style = Paint.Style.FILL
        }
        mCirclePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
        mCircleTextPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mWidth = width
            mHeight = height
            // 计算中心点
            mXCentralPoint = mWidth / 2
            mYCentralPoint = mHeight / 2
            // 距离左边的边距
            leftMargin = (mWidth / 2 - mCircleRadiusSize).toInt()
            // 距离上边的边距
            topMargin = (mHeight / 2 - mCircleRadiusSize).toInt()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundColor)
        drawCircleRadius(canvas)
        drawRadius(canvas)
    }

    private fun drawCircleRadius(canvas: Canvas) {
        val rectF = RectF(
            (mWidth - paddingStart - paddingEnd).toFloat() / 2 - mCircleRadiusSize,
            (mHeight - paddingTop - paddingBottom).toFloat() / 2 - mCircleRadiusSize,
            (mWidth - paddingStart - paddingEnd).toFloat() / 2 + mCircleRadiusSize,
            (mHeight - paddingTop - paddingBottom).toFloat() / 2 + mCircleRadiusSize
        )
        // 以下两个算法一致， 选择一个即可
        val rectFPoint = RectF(
            (mWidth - paddingStart - paddingEnd).toFloat() / 2 - mCircleRadiusSize + (mCircleRadiusSize - mCenterRadiusSize) / 2,
            (mHeight - paddingTop - paddingBottom).toFloat() / 2 - mCircleRadiusSize + (mCircleRadiusSize - mCenterRadiusSize) / 2,
            (mWidth - paddingStart - paddingEnd).toFloat() / 2 + mCircleRadiusSize - (mCircleRadiusSize - mCenterRadiusSize) / 2,
            (mHeight - paddingTop - paddingBottom).toFloat() / 2 + mCircleRadiusSize - (mCircleRadiusSize - mCenterRadiusSize) / 2
        )
//        rectFPoint = new RectF(leftMargin + (mCircleRadiusSize - mCenterRadiusSize) / 2,
//                topMargin + (mCircleRadiusSize - mCenterRadiusSize) / 2,
//                leftMargin + mCircleRadiusSize + mCenterRadiusSize + (mCircleRadiusSize - mCenterRadiusSize) / 2,
//                topMargin + mCircleRadiusSize + mCenterRadiusSize + (mCircleRadiusSize - mCenterRadiusSize) / 2);
        for (i in mValueColor.indices) {
            mCirclePaint.style = Paint.Style.FILL
            mCirclePaint.color = mValueColor[i]
            endAngle = getAngle(getPercent(mValueData, mValueData[i]))
            // 开始绘制环形， 矩形， 开始绘制的位置， 结束的位置
            canvas.drawArc(rectF, preAngle, endAngle, true, mCirclePaint)
            // 开始绘制外延折线、文本、比例
            if (isShowRate) {
                drawArcCenterPoint(canvas, i, rectFPoint)
            }
            // 下一个开始的位置， 要加上当前的位置
            preAngle += endAngle
        }
    }

    /**
     * 开始绘制外延折线、文本、比例
     */
    private fun drawArcCenterPoint(canvas: Canvas, position: Int, rectFPoint: RectF) {
        mCirclePaint.style = Paint.Style.STROKE
        mCirclePaint.strokeWidth = 1.dp2px().toFloat()
        mCirclePaint.color = Color.TRANSPARENT
        canvas.drawArc(rectFPoint, preAngle, endAngle / 2, true, mCirclePaint)
        dealPoint(rectFPoint, preAngle, endAngle / 2, position)
        val point = pointArcCenterMap[position]
        mCirclePaint.color = Color.WHITE
        if (point != null) {
            canvas.drawCircle(
                point.x.toFloat(),
                point.y.toFloat(),
                2.dp2px().toFloat(),
                mCirclePaint
            )
            if (preRate / 2 + mValueData[position] / 2 < 5) {
                extendLineWidth += 20.dp2px().toFloat()
                rate -= 0.05f
            } else {
                extendLineWidth = 20.dp2px().toFloat()
                rate = 0.4f
            }
            //外延画折线
            val lineXPoint = (point.x - (leftMargin + mCircleRadiusSize)) * (1 + rate)
            val lineYPoint = (point.y - (topMargin + mCircleRadiusSize)) * (1 + rate)
            val floats = FloatArray(8)
            floats[0] = point.x.toFloat()
            floats[1] = point.y.toFloat()
            floats[2] = leftMargin + mCircleRadiusSize + lineXPoint
            floats[3] = topMargin + mCircleRadiusSize + lineYPoint
            floats[4] = leftMargin + mCircleRadiusSize + lineXPoint
            floats[5] = topMargin + mCircleRadiusSize + lineYPoint
            if (point.x >= leftMargin + mCircleRadiusSize) {
                mCircleTextPaint.textAlign = Paint.Align.LEFT
                floats[6] = leftMargin + mCircleRadiusSize + lineXPoint + extendLineWidth
            } else {
                mCircleTextPaint.textAlign = Paint.Align.RIGHT
                floats[6] = leftMargin + mCircleRadiusSize + lineXPoint - extendLineWidth
            }
            floats[7] = topMargin + mCircleRadiusSize + lineYPoint
            mCircleTextPaint.color = mValueColor[position]
            mCircleTextPaint.style = Paint.Style.STROKE
            mCircleTextPaint.strokeWidth = 1.dp2px().toFloat()
            canvas.drawLines(floats, mCircleTextPaint)
            mCircleTextPaint.style = Paint.Style.FILL
            mCircleTextPaint.textSize = mCircleTextSize
            // 是否显示占比数据的原始数据还是占比比例
            val value = if (isShowValueData) {
                mValueData[position].toString()
            } else {
                getPercentString(mValueData, mValueData[position])
            }
            canvas.drawText(
                "$value%",
                floats[6], floats[7] + mCircleTextSize / 3, mCircleTextPaint
            )
            preRate = mValueData[position]
        }
    }

    private fun dealPoint(rectF: RectF, startAngle: Float, endAngle: Float, position: Int) {
        val path = Path()
        //通过Path类画一个90度（180—270）的内切圆弧路径
        path.addArc(rectF, startAngle, endAngle)
        val measure = PathMeasure(path, false)
        val floats = floatArrayOf(0f, 0f)
        //利用PathMeasure分别测量出各个点的坐标值floats
        val divisor = 1
        measure.getPosTan(measure.length / divisor, floats, null)
        val x = floats[0]
        val y = floats[1]
        val point = Point(x.roundToInt(), y.roundToInt())
        pointArcCenterMap[position] = point
    }

    /**
     * 绘制内半径
     */
    private fun drawRadius(canvas: Canvas) {
        if (isRing) {
            canvas.drawCircle(
                mXCentralPoint.toFloat(), mYCentralPoint.toFloat(),
                mCenterRadiusSize, mCenterPaint
            )
        }
    }

    /**
     * 设置绘制数据、绘制数据的颜色
     */
    fun setValueData(valueData: ArrayList<Float>, valueColor: ArrayList<Int>) {
        setValueData(valueData, valueColor, true)
    }

    /**
     * 设置绘制数据、绘制数据的颜色
     *
     * @param valueData  数据
     * @param valueColor 颜色
     * @param isRing     是否绘制内半径
     * @param isShowRate 是否显示占数据比例
     */
    fun setValueData(
        valueData: ArrayList<Float>,
        valueColor: ArrayList<Int>,
        isRing: Boolean = false,
        isShowRate: Boolean = false
    ) {
        setValueData(valueData, valueColor, isRing, isShowRate, false)
    }

    /**
     * 设置绘制数据、绘制数据的颜色
     *
     * @param valueData       数据
     * @param valueColor      颜色
     * @param isRing          是否绘制内半径
     * @param isShowRate      是否显示占数据比例
     * @param isShowValueData 是否显示占数据比例原始数据， 只有isShowRate = true时 有效
     */
    fun setValueData(
        valueData: ArrayList<Float>,
        valueColor: ArrayList<Int>,
        isRing: Boolean,
        isShowRate: Boolean,
        isShowValueData: Boolean
    ) {
        mValueData = valueData
        mValueColor = valueColor
        this.isRing = isRing
        this.isShowRate = isShowRate
        this.isShowValueData = isShowValueData
        invalidate()
    }

    /**
     * @param percent 当前数据
     * @return 当前数据占据百分比
     */
    private fun getPercent(data: List<Float>, percent: Float): Float {
        if (data.isEmpty()) return 0f
        val total = getTotal(data)
        return percent * 100 / total
    }

    /**
     * @param percent 当前数据
     * @return 当前数据占据百分比
     */
    private fun getPercentString(data: List<Float>, percent: Float): String {
        if (data.isEmpty()) return ""
        val total = getTotal(data)
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format((percent * 100 / total).toDouble())
    }

    /**
     * 获取所有值总和
     */
    private fun getTotal(data: List<Float>): Float {
        var total = 0f
        for (i in data.indices) {
            total += data[i]
        }
        return total
    }

    /**
     * @param percent 百分比
     */
    private fun getAngle(percent: Float): Float {
        return 360f * percent / 100f
    }
}