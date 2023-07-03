package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityRingChartBinding

class RingChartActivity : BaseActivity() {

    private val binding by lazy { ActivityRingChartBinding.inflate(layoutInflater) }

    private val mCircleColor = arrayListOf<Int>()
    private val mCircleData = arrayListOf<Float>()

    private val mCircleColor2 = arrayListOf<Int>()
    private val mCircleData2 = arrayListOf<Float>()

    private val mCircleColor3 = arrayListOf<Int>()
    private val mCircleData3 = arrayListOf<Float>()


    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.orange)
        binding.toolbar.title = "Android自定义环形图"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        initArrayList1()
        initArrayList2()
        initArrayList3()
    }

    override fun initData() {

    }

    private fun initArrayList1() {
        mCircleColor.clear()
        mCircleData.clear()
        mCircleColor.add(ContextCompat.getColor(this, R.color.orange))
        mCircleColor.add(ContextCompat.getColor(this, R.color.blue_color))
        mCircleColor.add(ContextCompat.getColor(this, R.color.red))
        mCircleData.add(180f)
        mCircleData.add(60f)
        mCircleData.add(120f)
        binding.ringChartView.setValueData(mCircleData, mCircleColor,
            isRing = true,
            isShowRate = true,
            isShowValueData = false)
    }

    private fun initArrayList2() {
        mCircleColor2.clear()
        mCircleData2.clear()
        mCircleColor2.add(ContextCompat.getColor(this, R.color.orange))
        mCircleColor2.add(ContextCompat.getColor(this, R.color.blue_color))
        mCircleColor2.add(ContextCompat.getColor(this, R.color.red))
        mCircleColor2.add(ContextCompat.getColor(this, R.color.yellow))
        mCircleColor2.add(ContextCompat.getColor(this, R.color.green_color))
        mCircleData2.add(150f)
        mCircleData2.add(10f)
        mCircleData2.add(90f)
        mCircleData2.add(60f)
        mCircleData2.add(30f)
        binding.ringChartView2.setValueData(mCircleData2, mCircleColor2,
            isRing = true,
            isShowRate = true,
            isShowValueData = true)
    }

    private fun initArrayList3() {
        mCircleColor3.clear()
        mCircleData3.clear()
        mCircleColor3.add(ContextCompat.getColor(this, R.color.orange))
        mCircleColor3.add(ContextCompat.getColor(this, R.color.blue_color))
        mCircleColor3.add(ContextCompat.getColor(this, R.color.red))
        mCircleData3.add(120f)
        mCircleData3.add(120f)
        mCircleData3.add(120f)
        binding.ringChartView3.setValueData(mCircleData3, mCircleColor3, true)
        binding.ringChartView4.setValueData(mCircleData3, mCircleColor3,
            isRing = false,
            isShowRate = true,
            isShowValueData = true)
    }
}