package com.dbz.view.viewPager

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/6/12 11:11
 */
class CustomViewPagerTransformer : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.8f
        private const val MIN_ALPHA = 0.5f

        private const val MAX_SCALE = 1.0f
        private const val MAX_ALPHA = 1.0f
    }

    override fun transformPage(page: View, position: Float) {
        if (position < -1) {
            page.scaleY = MIN_SCALE
            page.alpha = MIN_ALPHA
        } else if (position == 0f) {
            page.scaleY = MAX_SCALE
            page.alpha = MAX_ALPHA
        } else if (position <= 1) {
            val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
            val alphaFactor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - abs(position))
            page.scaleY = scaleFactor
            page.alpha = alphaFactor
        } else {
            page.scaleY = MIN_SCALE
            page.alpha = MIN_ALPHA
        }
    }
}