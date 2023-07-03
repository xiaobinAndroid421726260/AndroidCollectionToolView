package com.dbz.view.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * description:
 *
 * @author Db_z
 * @Date 2020/3/18 14:49
 *  @version V1.0
 */
class ChartReportView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * X、Y轴画笔
     */
    private lateinit var mXYPaint: Paint

    /**
     * Y轴文本
     */
    private lateinit var mYTextPaint: Paint

    /**
     * 折线画笔
     */
    private lateinit var mLinePaint: Paint

    /**
     * 点位画笔
     */
    private lateinit var mPointPaint: Paint

    /**
     * 背景颜色
     */
    private var mBackgroundColor = Color.WHITE

    /**
     * X、Y轴文本颜色
     */
    private var mXYTextColor = Color.parseColor("#02bbb7")

    /**
     * X、Y轴文本大小
     */
    private var mXYTextSize = 13.dp2px().toFloat()

    /**
     * 点位颜色
     */
    private var mPointColor = Color.parseColor("#804AFFFF")

    /**
     * 点位文本颜色
     */
    private var mPointTextColor = Color.WHITE

    /**
     * 点位文本大小
     */
    private var mPointTextSize = 13.dp2px().toFloat()

    /**
     * 折线颜色
     */
    private var mLineColor = Color.parseColor("#4AFFFF")

    /**
     * 折线宽度
     */
    private var mLineWidth = 1.dp2px().toFloat()

    /**
     * X、Y轴的颜色
     */
    private var mXYColor = Color.parseColor("#02bbb7")

    /**
     * 两点之间的间隔
     */
    private var mInterval = 30.dp2px().toFloat()

    /**
     * Y轴文本距左边间距
     */
    private val mYTextLeftInterval = 5.dp2px().toFloat()

    /**
     * X、 Y轴原点距左边的距离
     */
    private var mYLeftInterval = 30.dp2px().toFloat()

    /**
     * X 轴 第一个坐标
     */
    private var mXFirstPoint = 0f
    private var maxXFirstPoint = 0f
    private var minXFirstPoint = 0f

    /**
     * X、Y轴距离底边的距离
     */
    private val mXBottomInterval = 15.dp2px().toFloat()

    /**
     * Y轴距离上边距
     */
    private val mYTopInterval: Int = 40.dp2px()

    /**
     * 当前选中的点 默认在最后一位
     */
    private var mCurrentSelectPoint = 1

    /**
     * 宽、高
     */
    private var mWidth = 0

    /**
     * 宽、高
     */
    private  var mHeight = 0

    /**
     * 整体数据最大值
     */
    private var max = 1f

    /**
     * 是否正在滑动
     */
    private var isScrolling = false

    /**
     * 动画控制
     */
    private var aniLock = false

    /**
     * 速度检测器
     */
    private var velocityTracker: VelocityTracker? = null
    private var onSelectedActionClick: OnSelectedActionClick? = null

    private var mXValue = mutableListOf<XValue>()
    private var mYValue = mutableListOf<YValue>()

    init {
        init(context, attrs, defStyleAttr)
        initPaint()
    }


    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ChartReportView, defStyleAttr, 0)
        mBackgroundColor = typedArray.getColor(R.styleable.ChartReportView_dbz_background_color, Color.WHITE)
        mLineColor = typedArray.getColor(R.styleable.ChartReportView_dbz_line_point_color, mLineColor)
        mPointColor = typedArray.getColor(R.styleable.ChartReportView_dbz_point_color, mPointColor)
        mPointTextColor = typedArray.getColor(R.styleable.ChartReportView_dbz_point_text_color, mPointTextColor)
        mXYColor = typedArray.getColor(R.styleable.ChartReportView_dbz_x_y_color, mXYColor)
        mXYTextColor = typedArray.getColor(R.styleable.ChartReportView_dbz_x_y_text_color, mXYTextColor)
        mPointTextSize = typedArray.getDimension(R.styleable.ChartReportView_dbz_point_text_size, mPointTextSize)
        mLineWidth = typedArray.getDimension(R.styleable.ChartReportView_dbz_line_width, mLineWidth)
        mXYTextSize = typedArray.getDimension(R.styleable.ChartReportView_dbz_x_y_text_size,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13f, resources.displayMetrics))
        mInterval = typedArray.getDimension(R.styleable.ChartReportView_dbz_point_interval, mInterval)
        typedArray.recycle()
    }

    private fun initPaint() {
        mXYPaint = Paint()
        mXYPaint.isAntiAlias = true
        mXYPaint.color = mXYColor
        mXYPaint.strokeWidth = mLineWidth
        mYTextPaint = Paint()
        mYTextPaint.isAntiAlias = true
        mYTextPaint.color = mXYTextColor
        mYTextPaint.textSize = mXYTextSize
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.color = mLineColor
        mLinePaint.strokeWidth = mLineWidth
        mLinePaint.style = Paint.Style.STROKE
        mPointPaint = Paint()
        mPointPaint.isAntiAlias = true
        mPointPaint.textSize = mPointTextSize
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            mWidth = width
            mHeight = height
            // 测量Y轴数据的文本宽度
            val rect: Rect = getTextBounds(mYValue[mYValue.size - 1].value, mYTextPaint)
            // 计算X轴的左边距
            mYLeftInterval = rect.width() + mYTextLeftInterval * 2
            // 第一个X轴点的位置
            mXFirstPoint = mYLeftInterval + mInterval
            // 遍历数据最大值 如果为0那么默认为1
            for (i in mXValue.indices) {
                max = max(max, mXValue[i].num)
            }
            if (max == 0f) {
                max = 1f
            }
            minXFirstPoint = mWidth - (mWidth - mYLeftInterval) * 0.1f - mInterval * (mXValue.size - 1)
            maxXFirstPoint = mXFirstPoint
        }
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mBackgroundColor)
        drawXYLine(canvas)
        drawYText(canvas)
        drawBrokenLineAndPoint(canvas)
        if (!isScrolling && !aniLock) {
            scrollAtStart()
        }
    }

    /**
     * 绘制X、Y轴
     */
    private fun drawXYLine(canvas: Canvas) {
        mXYPaint.color = mXYColor
        // 绘制X轴
        canvas.drawLine(mYLeftInterval, (mHeight - paddingBottom - mXBottomInterval), (mWidth - paddingRight).toFloat(),
            (mHeight - paddingBottom - mXBottomInterval), mXYPaint
        )
        // 绘制Y轴
        canvas.drawLine(
            mYLeftInterval, 0f, mYLeftInterval,
            (mHeight - paddingBottom - mXBottomInterval),
            mXYPaint
        )
        //绘制y轴箭头
        mXYPaint.style = Paint.Style.STROKE
        val path = Path()
        path.moveTo((mYLeftInterval - 5.dp2px()), mXBottomInterval)
        path.lineTo(mYLeftInterval, 0f)
        path.lineTo((mYLeftInterval + 5.dp2px()), mXBottomInterval)
        canvas.drawPath(path, mXYPaint)
    }

    /**
     * 绘制Y轴文本
     */
    private fun drawYText(canvas: Canvas) {
        for (i in mYValue.indices) {
            val rect: Rect = getTextBounds(mYValue[i].value, mYTextPaint)
            // 绘制区域 = 总高度 - 下边距 - 上边距
            // 绘制区域 / (绘制数据的数量 - 1) (减一是计算绘制之间的间距， 如果不减一， 那么在开始第一个绘制时会多出一段间距)
            val y =
                (mHeight - paddingBottom - mXBottomInterval - paddingTop - mYTopInterval) / (mYValue.size - 1)
            // X轴 文本水平居中X轴    Y轴 从最大值到最小值，反着绘制， Y点从上到下 但要加 上边距
            canvas.drawText(mYValue[mYValue.size - (i + 1)].value,
                rect.centerX().toFloat(), y * i + mYTopInterval, mYTextPaint
            )
        }
//        Rect rect = getTextBounds("100", mXYPaint);
//        // X轴  文本中间开始绘制 + 距离左边的距离      Y轴   从底部开始绘制， 要减去底边距离
//        canvas.drawText("0", (float) rect.width() / 2 + mYTextLeftInterval, mHeight - getPaddingBottom() - getPaddingTop() - mXBottomInterval, mXYPaint);
//        // X轴  文本中间开始绘制两位正好中间           Y轴  （总高度 - 顶部距离 - 底部距离） / 2 是整个的中心点，要在加上距离上边的边距才是绘制部分的中心点
//        canvas.drawText("50", (float) rect.width() / 2, (float) ((mHeight - mYTopInterval - mXBottomInterval - getPaddingBottom() - getPaddingTop()) / 2) + mYTopInterval, mXYPaint);
//        // X轴  文本中间开始绘制三位要减去左边的距离    Y轴   (要使文本在中间显示) 距离上边距是底边 + 文本的高度 / 2   (正常显示是 mYTopInterval 距离上边的边距)
//        canvas.drawText("100", (float) rect.width() / 2 - mYTextLeftInterval, mYTopInterval + (float) rect.height() / 2, mXYPaint);
    }

    /**
     * 绘制折线和折线交点处对应的点
     */
    private fun drawBrokenLineAndPoint(canvas: Canvas) {
        //重新开一个图层
        val layerId = canvas.saveLayer(0f, 0f, mWidth.toFloat(), mHeight.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        drawLine(canvas)
        drawLinePoint(canvas)
        // 将折线超出x轴坐标的部分截取掉
        mXYPaint.style = Paint.Style.FILL
        mXYPaint.color = mBackgroundColor
        mXYPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        val rectF = RectF(0f, 0f, mYLeftInterval, mHeight.toFloat())
        canvas.drawRect(rectF, mXYPaint)
        mXYPaint.xfermode = null
        //保存图层
        canvas.restoreToCount(layerId)
    }

    /**
     * 绘制折线
     */
    private fun drawLine(canvas: Canvas) {
        if (mXValue.isEmpty()) return
        val path = Path()
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight = mHeight - paddingBottom - mXBottomInterval - paddingTop - mYTopInterval
        // 起点x、y轴开始绘制
        var x = mXFirstPoint
        var y: Float =
            mHeight - paddingBottom - mXBottomInterval - mXValue[0].num * totalHeight / max
        // 绘制x、y轴左下角起点
        path.moveTo(mYLeftInterval, mHeight - paddingBottom - mXBottomInterval)
        // 绘制第一个点的位置
        path.lineTo(x, y)
        // 因为绘制了第一个点，所以i起始是1
        for (i in 1 until mXValue.size) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            x = mXFirstPoint + mInterval * i
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            y = mHeight - paddingBottom - mXBottomInterval - mXValue[i].num * totalHeight / max
            path.lineTo(x, y)
        }
        canvas.drawPath(path, mLinePaint)
    }

    /**
     * 绘制折线点和提示框
     */
    private fun drawLinePoint(canvas: Canvas) {
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight = mHeight - paddingBottom - mXBottomInterval - paddingTop - mYTopInterval
        for (i in mXValue.indices) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            val x = mInterval * i + mXFirstPoint
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            val y: Float = mHeight - paddingBottom - mXBottomInterval - mXValue[i].num * totalHeight / max
            // 绘制两次点中心两个颜色， 外层透明度50
            mPointPaint.color = mPointColor
            mPointPaint.style = Paint.Style.FILL
            canvas.drawCircle(x, y, 4.dp2px().toFloat(), mPointPaint)
            // 绘制两次点中心两个颜色， 外层透明度50
            mPointPaint.color = mLineColor
            mPointPaint.style = Paint.Style.FILL
            canvas.drawCircle(x, y, 2.dp2px().toFloat(), mPointPaint)
            if (mCurrentSelectPoint == i + 1) {
                // 绘制选中的点
                drawCurrentSelectPoint(canvas, i + 1, x, y)
                // 绘制选中提示点
                drawCurrentTextBox(canvas, i + 1, x, y - 10.dp2px().toFloat(), mXValue[i].value)
            }
        }
    }

    /**
     * 绘制当前选中的点
     */
    private fun drawCurrentSelectPoint(canvas: Canvas, i: Int, x: Float, y: Float) {
        mPointPaint.color = Color.parseColor("#d0f3f2")
        mPointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, 7.dp2px().toFloat(), mPointPaint)
        mPointPaint.color = mPointColor
        mPointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, 4.dp2px().toFloat(), mPointPaint)
        mPointPaint.color = mLineColor
        mPointPaint.style = Paint.Style.FILL
        canvas.drawCircle(x, y, 2.dp2px().toFloat(), mPointPaint)
    }

    /**
     * 绘制选中提示框
     */
    private fun drawCurrentTextBox(canvas: Canvas, i: Int, x: Float, y: Float, text: String) {
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
        mPointPaint.style = Paint.Style.FILL
        mPointPaint.color = mLineColor
        canvas.drawPath(path, mPointPaint)
        // 绘制矩形， 周边圆角
        val rectF = RectF(x - dp20, y - 5.dp2px(), x + dp20, y - dp6 - dp20)
        canvas.drawRoundRect(rectF, 4.dp2px().toFloat(), 4.dp2px().toFloat(), mPointPaint)
        mPointPaint.color = mPointTextColor
        mPointPaint.textSize = mPointTextSize
        val rect: Rect = getTextBounds(text, mPointPaint)
        // y点计算  以下两种方法均可
        // x减去文本的宽度  y - 提示框距离点的高度 - 三角的高度 - 提示框 / 2
//        canvas.drawText(text, x - (float) rect.width() / 2, y - dpToPx(10) - dp6 - dpToPx(5) - rectF.height() / 2, mPointPaint);
        // x减去文本的宽度  y - 三角的高度 - 文本高度 / 2
        canvas.drawText(text,
            x - rect.width().toFloat() / 2,
            y - dp6 - rect.height().toFloat() / 2,
            mPointPaint
        )
    }

    /**
     * 点击X轴坐标或者折线节点
     *
     * @param event 事件
     */
    private fun clickAction(event: MotionEvent) {
        val dp8 = 8.dp2px()
        val eventX = event.x
        val eventY = event.y
        // 绘制区域 = 总高度 - 下边距 - 上边距
        val totalHeight = mHeight - paddingBottom - mXBottomInterval - paddingTop - mYTopInterval
        for (i in mXValue.indices) {
            // x点绘制的位置， mInterval是两点的间距 * 点的数量 + x轴距离左边距
            val x = mInterval * i + mXFirstPoint
            // y轴上到下是数字变大 所以需要反着绘制  绘制区域 - 百分比高度(百分比高度 = 数值 * 总高度 / 数据最大值)， 相反过来就是从下到上的比例高度
            val y: Float =
                mHeight - paddingBottom - mXBottomInterval - mXValue[i].num * totalHeight / max
            // 判断点击的位置在点的旁边
            if (eventX >= x - dp8 && eventX <= x + dp8 && eventY >= y - dp8 && eventY <= y + dp8 && mCurrentSelectPoint != i + 1) {
                mCurrentSelectPoint = i + 1
                invalidate()
                if (onSelectedActionClick != null) {
                    onSelectedActionClick?.onActionClick(i, mXValue[i].num, mXValue[i].value)
                }
                return
            }
        }
    }

    /**
     * 当宽度不足以呈现全部数据时 滚动
     */
    private fun scrollAtStart() {
        // 整体数据的宽度 大于 绘制区域宽度
        if (mInterval * mXValue.size > mWidth - mYLeftInterval) {
            val scrollLength = maxXFirstPoint - minXFirstPoint
            val animator = ValueAnimator.ofFloat(0f, scrollLength)
            animator.duration = 500L //时间最大为1000毫秒，此处使用比例进行换算
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                mXFirstPoint = max(mXFirstPoint - value, minXFirstPoint)
                invalidate()
            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    isScrolling = true
                }

                override fun onAnimationEnd(animator: Animator) {
                    isScrolling = false
                    aniLock = true
                }

                override fun onAnimationCancel(animator: Animator) {
                    isScrolling = false
                    aniLock = true
                }

                override fun onAnimationRepeat(animator: Animator) {}
            })
            animator.start()
        }
    }

    /**
     * 手指抬起后的滑动处理
     */
    private fun scrollAfterActionUp() {
        val velocity: Float = getVelocity()
        var scrollLength = maxXFirstPoint - minXFirstPoint
        if (abs(velocity) < 10000) //10000是一个速度临界值，如果速度达到10000，最大可以滑动(maxXInit - minXInit)
            scrollLength = (maxXFirstPoint - minXFirstPoint) * abs(velocity) / 10000
        val animator = ValueAnimator.ofFloat(0f, scrollLength)
        animator.duration = (scrollLength / (maxXFirstPoint - minXFirstPoint) * 1000).toLong() //时间最大为1000毫秒，此处使用比例进行换算
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue as Float
            if (velocity < 0 && mXFirstPoint > minXFirstPoint) { //向左滑动
                mXFirstPoint = max(mXFirstPoint - value, minXFirstPoint)
            } else if (velocity > 0 && mXFirstPoint < maxXFirstPoint) { //向右滑动
                mXFirstPoint = min(mXFirstPoint + value, maxXFirstPoint)
            }
            invalidate()
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                isScrolling = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isScrolling = false
            }

            override fun onAnimationCancel(animator: Animator) {
                isScrolling = false
            }

            override fun onAnimationRepeat(animator: Animator) {}
        })
        animator.start()
    }

    private var startX = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isScrolling) return super.onTouchEvent(event)
        this.parent.requestDisallowInterceptTouchEvent(true) //当该view获得点击事件，就请求父控件不拦截事件
        obtainVelocityTracker(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startX = event.x
            MotionEvent.ACTION_MOVE -> if (mInterval * mXValue.size > mWidth - mYLeftInterval) { //当期的宽度不足以呈现全部数据
                val scrollX = event.x - startX
                startX = event.x
                mXFirstPoint = if (mXFirstPoint + scrollX < minXFirstPoint) {
                    minXFirstPoint
                } else {
                    min(mXFirstPoint + scrollX, maxXFirstPoint)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                clickAction(event)
//                scrollAfterActionUp()
                this.parent.requestDisallowInterceptTouchEvent(false)
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL -> {
                this.parent.requestDisallowInterceptTouchEvent(false)
                recycleVelocityTracker()
            }
        }
        return true
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

    /**
     * 获取速度跟踪器
     */
    private fun obtainVelocityTracker(event: MotionEvent) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(event)
    }

    /**
     * 获取速度
     *
     * @return
     */
    private fun getVelocity(): Float {
        if (velocityTracker != null) {
            velocityTracker?.computeCurrentVelocity(1000)
            return velocityTracker!!.xVelocity
        }
        return 0f
    }

    /**
     * 回收速度跟踪器
     */
    private fun recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
    }

    /**
     * 设置当前选中的点
     */
    fun setCurrentSelectPoint(currentSelectPoint: Int) {
        mCurrentSelectPoint = currentSelectPoint
        invalidate()
    }

    /**
     * 设置数据
     */
    fun setValue(xValue: MutableList<XValue>, yValue: MutableList<YValue>) {
        mXValue = xValue
        mYValue = yValue
        // 设置选中的位置在最后一个
        mCurrentSelectPoint = mXValue.size
        invalidate()
    }

    interface OnSelectedActionClick {
        fun onActionClick(position: Int, num: Float, text: String?)
    }

    fun setOnSelectedActionClick(onSelectedActionClick: OnSelectedActionClick) {
        this.onSelectedActionClick = onSelectedActionClick
    }

    class XValue(val num: Float, val value: String)

    class YValue(val num: Float, val value: String)
}