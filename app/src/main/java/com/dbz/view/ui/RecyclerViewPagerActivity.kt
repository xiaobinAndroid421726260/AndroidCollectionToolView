package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.dbz.view.R
import com.dbz.view.adapter.RecyclerViewAdapter
import com.dbz.view.adapter.ViewPagerAdapter
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityRecyclerViewPagerBinding
import com.dbz.view.ext.dp2px
import com.dbz.view.ext.setLinearLayoutManager
import com.dbz.view.viewPager.CustomViewPagerTransformer
import kotlin.math.abs
import kotlin.math.min

class RecyclerViewPagerActivity : BaseActivity() {

    private val binding by lazy { ActivityRecyclerViewPagerBinding.inflate(layoutInflater) }
    private val mRecyclerAdapter by lazy { RecyclerViewAdapter() }
    private val mPagerAdapter by lazy { ViewPagerAdapter() }
    private val data = arrayListOf<String>()

    companion object {
        private const val MIN_SCALE = 0.85f
        private const val MIN_ALPHA = 0.5f
        private const val MAX_SCALE = 1.0f
        private const val MAX_ALPHA = 1.0f
    }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.blue_color)
        binding.toolbar.title = "viewPager || RecyclerView"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun initData() {
        for (i in 0..5) {
            data.add("这是第" + (i + 1) + "个")
        }
        initPagerAdapter()
        initAdapter()
    }

    private fun initPagerAdapter() {
        // 设置页面之间的边距
        binding.viewPager.pageMargin = 12.dp2px()
        // 设置缩放 透明度
        binding.viewPager.setPageTransformer(false, CustomViewPagerTransformer())
        //添加数据之后在设置适配器这样setPageTransformer会生效，否则两边的item没有透明的效果
        binding.viewPager.adapter = mPagerAdapter
        mPagerAdapter.addData(data)
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {
            }

            override fun onPageSelected(position: Int) {
                // 滑动之后处理逻辑
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun initAdapter() {
        binding.recyclerView.setLinearLayoutManager(mRecyclerAdapter,
            orientation = RecyclerView.HORIZONTAL)
        // 让item居中显示
        val snapHelper = LinearSnapHelper()
        // 绑定到 mRecyclerView
        snapHelper.attachToRecyclerView(binding.recyclerView)
        mRecyclerAdapter.setList(data)
        // 需要在添加数据时 再调用一次 不然会在滑动时才会显示效果
        binding.recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val childCount = recyclerView.childCount
                    for (i in 0 until childCount) {
                        val child = recyclerView.getChildAt(i)
                        val left = child.left
                        val paddingStart = recyclerView.paddingStart
                        // 遍历recyclerView子项，以中间项左侧偏移量为基准进行缩放
                        val bl = min(1f, abs(left - paddingStart) * 1f / child.width)
                        val scale = MAX_SCALE - bl * (MAX_SCALE - MIN_SCALE)
                        val alpha = MAX_ALPHA - bl * (MAX_ALPHA - MIN_ALPHA)
                        child.scaleY = scale
                        child.alpha = alpha
                    }
                }
            })
    }
}