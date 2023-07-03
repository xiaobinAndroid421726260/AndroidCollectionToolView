package com.dbz.view.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.dbz.view.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/3/23 15:29
 */
class CollapsingAvatarToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr), OnOffsetChangedListener {

    private var avatarView: View? = null
    private var titleView: TextView? = null

    private var collapsedPadding = 0f
    private var expandedPadding = 0f

    private var expandedImageSize = 0f
    private var collapsedImageSize = 0f

    private var collapsedTextSize = 0f
    private var expandedTextSize = 0f

    private var expandedTextColor = Color.WHITE
    private var collapsedTextColor = Color.BLACK

    private var valuesCalculatedAlready = false
    private var appBarLayout: AppBarLayout? = null
    private var toolbar: Toolbar? = null
    private var collapsedHeight = 0f
    private var expandedHeight = 0f
    private var maxOffset = 0f

    init {
        orientation = HORIZONTAL
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.CollapsingAvatarToolbar, 0, 0)
        try {
            collapsedPadding = a.getDimension(R.styleable.CollapsingAvatarToolbar_collapsedPadding, -1f)
            expandedPadding = a.getDimension(R.styleable.CollapsingAvatarToolbar_expandedPadding, -1f)
            collapsedImageSize = a.getDimension(R.styleable.CollapsingAvatarToolbar_collapsedImageSize, -1f)
            expandedImageSize = a.getDimension(R.styleable.CollapsingAvatarToolbar_expandedImageSize, -1f)
            collapsedTextSize = a.getDimension(R.styleable.CollapsingAvatarToolbar_collapsedTextSize, -1f)
            expandedTextSize = a.getDimension(R.styleable.CollapsingAvatarToolbar_expandedTextSize, -1f)
            expandedTextColor = a.getColor(R.styleable.CollapsingAvatarToolbar_expandedTextColor, Color.WHITE)
            collapsedTextColor = a.getColor(R.styleable.CollapsingAvatarToolbar_collapsedTextColor, Color.BLACK)
        } finally {
            a.recycle()
        }

        val resources = resources
        if (collapsedPadding < 0) {
            collapsedPadding = resources.getDimension(R.dimen.default_collapsed_padding)
        }
        if (expandedPadding < 0) {
            expandedPadding = resources.getDimension(R.dimen.default_expanded_padding)
        }
        if (collapsedImageSize < 0) {
            collapsedImageSize = resources.getDimension(R.dimen.default_collapsed_image_size)
        }
        if (expandedImageSize < 0) {
            expandedImageSize = resources.getDimension(R.dimen.default_expanded_image_size)
        }
        if (collapsedTextSize < 0) {
            collapsedTextSize = resources.getDimension(R.dimen.default_collapsed_text_size)
        }
        if (expandedTextSize < 0) {
            expandedTextSize = resources.getDimension(R.dimen.default_expanded_text_size)
        }
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (!valuesCalculatedAlready) {
            calculateValues()
            valuesCalculatedAlready = true
        }
        val expandedPercentage: Float = 1 - (-verticalOffset / maxOffset)
        updateViews(expandedPercentage, verticalOffset)
    }

    private fun findParentAppBarLayout(): AppBarLayout {
        val parent = this.parent
        return if (parent is AppBarLayout) {
            parent
        } else if (parent.parent is AppBarLayout) {
            parent.parent as AppBarLayout
        } else {
            throw IllegalStateException("Must be inside an AppBarLayout")
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        findViews()
        if (!isInEditMode) {
            appBarLayout?.addOnOffsetChangedListener(this)
        } else {
            setExpandedValuesForEditMode()
        }
    }

    private fun setExpandedValuesForEditMode() {
        calculateValues()
        updateViews(1f, 0)
    }

    private fun findViews() {
        appBarLayout = findParentAppBarLayout()
        toolbar = findSiblingToolbar()
        avatarView = findAvatar()
        titleView = findTitle()
    }

    private fun findAvatar(): View {
        var i = 0
        while (i < childCount) {
            val child = getChildAt(i)
            if (child is ImageView) {
                return child
            }
            i++
        }
        throw IllegalStateException("ImageView with id avatar not found")
    }

    private fun findTitle(): TextView {
        var i = 0
        var j = 0
        while (i < childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup){
                while (j < child.childCount){
                    val childView = child.getChildAt(j)
                    if (childView is TextView) {
                        return childView
                    }
                    j++
                }
            }
            if (child is TextView) {
                return child
            }
            i++
        }
        throw IllegalStateException("TextView with id title not found")
    }

    private fun findSiblingToolbar(): Toolbar {
        val parent = this.parent as ViewGroup
        var i = 0
        val c = parent.childCount
        while (i < c) {
            val child = parent.getChildAt(i)
            if (child is Toolbar) {
                return child
            }
            i++
        }
        throw IllegalStateException("No toolbar found as sibling")
    }

    private fun calculateValues() {
        collapsedHeight = toolbar!!.height.toFloat()
        expandedHeight = (appBarLayout!!.height - toolbar!!.height).toFloat()
        maxOffset = expandedHeight
    }

    private fun updateViews(expandedPercentage: Float, currentOffset: Int) {
        val inversePercentage = 1 - expandedPercentage
        val translation = -currentOffset + toolbar!!.height.toFloat() * expandedPercentage
        val currHeight = collapsedHeight + (expandedHeight - collapsedHeight) * expandedPercentage
        val currentPadding = expandedPadding + (collapsedPadding - expandedPadding) * inversePercentage
        val currentImageSize = collapsedImageSize + (expandedImageSize - collapsedImageSize) * expandedPercentage
        val currentTextSize = collapsedTextSize + (expandedTextSize - collapsedTextSize) * expandedPercentage
        setContainerOffset(translation)
        setContainerHeight(currHeight.toInt())
        setPadding(currentPadding.toInt())
        setAvatarSize(currentImageSize.toInt())
        setTextSize(currentTextSize)
        setTextColor(inversePercentage)
    }

    private fun setContainerOffset(translation: Float) {
        this.translationY = translation
    }

    private fun setContainerHeight(currHeight: Int) {
        this.layoutParams.height = currHeight
    }

    private fun setPadding(currentPadding: Int) {
        this.setPadding(currentPadding, 0, 0, 0)
    }

    private fun setTextSize(currentTextSize: Float) {
        titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentTextSize)
    }

    private fun setTextColor(ratio: Float) {
        if (expandedTextColor != collapsedTextColor){
            titleView?.setTextColor(blendColors(expandedTextColor, collapsedTextColor, ratio))
        }
    }

    private fun setAvatarSize(currentImageSize: Int) {
        avatarView?.layoutParams?.height = currentImageSize
        avatarView?.layoutParams?.width = currentImageSize
    }

    private fun blendColors(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio
        val r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio
        val g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio
        val b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}