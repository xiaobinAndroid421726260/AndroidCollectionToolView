package com.dbz.view.view.panel

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import com.blankj.utilcode.util.ScreenUtils
import com.dbz.view.R

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/4/21 17:51
 */
class ChatPhotoAlertNestedScrollingParent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    // 标题栏
    private var mFlTitle: View? = null
    // 滚动的间距
    private var mViewSpace: View? = null
    // 滚动底部的view
    private var mScrollBottomView: View? = null

    // 滚动的背景布局头
    private var mClBottomLayoutTitle: ConstraintLayout? = null
    private var mTabBgStartColor = Color.WHITE
    private var mTabBgEndColor = Color.WHITE
    private var mTabBgColor = 0
    // 背景 圆角
    private var mTabBg: GradientDrawable = GradientDrawable()
    private var mTabBgRadius: FloatArray = FloatArray(8)
    private var mMaxTabBgRadius = 0f

    private val mHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var mSnapAnimator: ValueAnimator? = null
    private var mLastStartedType = 0
    private var mScrollRangeTop = 0
    private var mScrollRangeBottom = 0
    private var mCurrentScrollRange = 0
    private var mTitleChange = false
    private var isScrollTopShowTitle = false
    private var mArgbEvaluator: ArgbEvaluator = ArgbEvaluator()
    private var isDoPerformAnyCallbacks = true
    private var mOnOffsetScrollRangeListener: OnOffsetScrollRangeListener? = null

    init {
        mArgbEvaluator = ArgbEvaluator()
        mTabBgColor = mTabBgStartColor
        mMaxTabBgRadius = getDefaultTabBgRadius().toFloat()
        mTabBg.setColor(mTabBgStartColor)
        mTabBgRadius = FloatArray(8)
        mTabBgRadius[0] = mMaxTabBgRadius.also { mTabBgRadius[3] = it }
            .also { mTabBgRadius[2] = it }.also { mTabBgRadius[1] = it }
        mTabBgRadius[4] = 0.also { mTabBgRadius[7] = it.toFloat() }.also {
            mTabBgRadius[6] =
                it.toFloat()
        }.also { mTabBgRadius[5] = it.toFloat() }.toFloat()
        mTabBg.cornerRadii = mTabBgRadius
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        mFlTitle = findViewById(R.id.fl_title)
        mViewSpace = findViewById(R.id.view_space)
        mScrollBottomView = findViewById(R.id.ll_scroll_bottom_layout)
        mClBottomLayoutTitle = findViewById(R.id.cl_bottom_top_title)
        mClBottomLayoutTitle?.background = mTabBg
    }

    private fun getDefaultTabBgRadius(): Int {
        return getDimen(R.dimen.dp_16)
    }

    private fun getDimen(@DimenRes dimenId: Int): Int {
        return resources.getDimensionPixelSize(dimenId)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mViewSpaceHeight = mViewSpace!!.measuredHeight
        val mFlTitleHeight = mFlTitle!!.measuredHeight
        val mViewSpaceMarginTop = (mViewSpace!!.layoutParams as MarginLayoutParams).topMargin
        // 获取向上滑动的距离
        mScrollRangeTop = mViewSpaceHeight + mViewSpaceMarginTop + mFlTitleHeight
        val update = mCurrentScrollRange != 0 && mScrollRangeBottom == mCurrentScrollRange
        // 屏幕总高度 - 上方空间高度 - 状态栏高度
        val mViewSpaceBottom = ScreenUtils.getScreenHeight() - mViewSpaceHeight
        // 获取向下滑动的距离  布局距离下方的3分之1
        mScrollRangeBottom = -(mViewSpaceBottom - mViewSpaceBottom / 3)
        if (update) {
            mCurrentScrollRange = mScrollRangeBottom
        }
        val scrollWidthSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
        val scrollHeightSpec =
            MeasureSpec.makeMeasureSpec(measuredHeight - mFlTitleHeight, MeasureSpec.EXACTLY)
//        val scrollHeightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        mScrollBottomView!!.measure(scrollWidthSpec, scrollHeightSpec)
        if (mOnOffsetScrollRangeListener != null) {
            mOnOffsetScrollRangeListener!!.offsetScroll(0f,
                mCurrentScrollRange,
                mScrollRangeTop,
                mScrollRangeBottom,
                isScrollTopShowTitle)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val mViewSpaceHeight =
            ScreenUtils.getScreenHeight() - mScrollBottomView!!.measuredHeight + mViewSpace!!.measuredHeight
        mViewSpace!!.layout(left, mFlTitle!!.measuredHeight, right, mViewSpaceHeight)
        val t = mScrollRangeTop - mCurrentScrollRange + mFlTitle!!.measuredHeight
        mScrollBottomView!!.layout(left, t, right, t + mScrollBottomView!!.measuredHeight)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        val started = axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        if (started) {
            if (mSnapAnimator != null) {
                mSnapAnimator!!.cancel()
            }
        }
        mLastStartedType = type
        return started
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (mLastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH) {
            if (mSnapAnimator == null || !mSnapAnimator!!.isRunning) {
                snap()
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray,
    ) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
        if (dyUnconsumed < 0) {
            val bottom: Int
            if (type == ViewCompat.TYPE_TOUCH) {
                bottom = mScrollRangeBottom
            } else if (mCurrentScrollRange < 0) {
                bottom = mScrollRangeBottom
                if (dyUnconsumed > -10 && mCurrentScrollRange < mScrollRangeBottom * 0.25f) {
                    ViewCompat.stopNestedScroll(target, type)
                }
            } else {
                bottom = 0
            }
            consumed[1] = offsetScrollView(dyUnconsumed, mScrollRangeTop, bottom)
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
    ) {
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) {
            if (type == ViewCompat.TYPE_TOUCH || mCurrentScrollRange > 0) {
                consumed[1] = offsetScrollView(dy, mScrollRangeTop, mScrollRangeBottom)
            } else {
                offsetScrollView(dy, 0, mScrollRangeBottom)
                consumed[1] = dy
            }
        }
    }

    private fun offsetScrollView(dy: Int) {
        offsetScrollView(dy, mScrollRangeTop, mScrollRangeBottom, true)
    }

    private fun offsetScrollView(dy: Int, top: Int, bottom: Int): Int {
        return offsetScrollView(dy, top, bottom, false)
    }

    private fun offsetScrollView(dy: Int, top: Int, bottom: Int, anim: Boolean): Int {
        var tempDy = dy
        if (tempDy >= 0 && mCurrentScrollRange >= top) {
            return 0
        }
        if (tempDy <= 0 && mCurrentScrollRange <= bottom) {
            return 0
        }
        val result = tempDy
        if (mCurrentScrollRange <= 0 && !anim) {
            tempDy /= 1.5f.toInt()
        }
        mCurrentScrollRange += tempDy
        if (mCurrentScrollRange > top) {
            tempDy -= mCurrentScrollRange - top
            mCurrentScrollRange = top
        } else if (mCurrentScrollRange < bottom) {
            tempDy -= mCurrentScrollRange - bottom
            mCurrentScrollRange = bottom
        }
        changeTitle(mCurrentScrollRange.toFloat())
        ViewCompat.offsetTopAndBottom(mScrollBottomView!!, -tempDy)
        return result
    }

    private fun changeTitle(current: Float) {
        val fraction: Float = if (current < 0) {
            0f
        } else if (current >= mScrollRangeTop) {
            1f
        } else {
            current / mScrollRangeTop
        }
//        int titleColor = (int) mArgbEvaluator.evaluate(fraction, mTitleSearchBgStartColor, mTitleSearchBgEndColor);
//        mFlTitle.getBackground().mutate().setAlpha((int) (fraction * 0xFF));
        val radius = mMaxTabBgRadius * (1 - fraction)
        mTabBgRadius[3] = radius
        mTabBgRadius[2] = mTabBgRadius[3]
        mTabBgRadius[1] = mTabBgRadius[2]
        mTabBgRadius[0] = mTabBgRadius[1]
        mTabBg.cornerRadii = mTabBgRadius
        val tabColor = mArgbEvaluator.evaluate(fraction, mTabBgColor, mTabBgEndColor) as Int
        mTabBg.setColor(tabColor)
        mClBottomLayoutTitle!!.background = mTabBg
        dispatchTitleChange(fraction)
        if (mOnOffsetScrollRangeListener != null && isDoPerformAnyCallbacks) {
            mOnOffsetScrollRangeListener!!.offsetScroll(fraction,
                mCurrentScrollRange,
                mScrollRangeTop,
                mScrollRangeBottom,
                isScrollTopShowTitle)
            mOnOffsetScrollRangeListener!!.isScrollTopShowTitle(mCurrentScrollRange,
                mScrollRangeTop,
                mCurrentScrollRange == mScrollRangeTop)
        }
    }

    private fun dispatchTitleChange(fraction: Float) {
        val change = fraction > 0
        if (change != mTitleChange) {
            mTitleChange = change
        }
    }

    fun setExpand(expand: Boolean) {
        if (expand) {
            anim(mCurrentScrollRange, mScrollRangeBottom)
        } else {
            anim(mCurrentScrollRange, 0)
        }
    }

    private fun snap() {
        val start = mCurrentScrollRange
        if (start != mScrollRangeTop) {
            val end: Int = if (start < mScrollRangeBottom * 0.5f) {
                //                end = mScrollRangeBottom;
                0
            } else if (start > mScrollRangeTop * 0.5f) {
                mScrollRangeTop
            } else {
                0
            }
            anim(start, end)
        }
    }

    private fun anim(start: Int, end: Int) {
        if (start == end) return
        isScrollTopShowTitle = end == mScrollRangeTop
        if (mSnapAnimator == null) {
            mSnapAnimator = ValueAnimator.ofInt(start, end)
            mSnapAnimator?.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Int
                offsetScrollView(value - mCurrentScrollRange)
            }
        } else {
            mSnapAnimator!!.cancel()
            mSnapAnimator!!.setIntValues(start, end)
        }
        mSnapAnimator!!.duration = 250
        mSnapAnimator!!.start()
    }

    fun setDoPerformAnyCallbacks(doPerformAnyCallbacks: Boolean) {
        isDoPerformAnyCallbacks = doPerformAnyCallbacks
    }

    fun setOnOffsetScrollRangeListener(listener: OnOffsetScrollRangeListener) {
        mOnOffsetScrollRangeListener = listener
    }

    // 滚动的距离
    interface OnOffsetScrollRangeListener {
        fun offsetScroll(
            fraction: Float,
            offset: Int,
            scrollRangeTop: Int,
            scrollRangeBottom: Int,
            isScrollTopShowTitle: Boolean,
        )

        fun isScrollTopShowTitle(offset: Int, scrollRangeTop: Int, isScrollTopShowTitle: Boolean)
    }
}