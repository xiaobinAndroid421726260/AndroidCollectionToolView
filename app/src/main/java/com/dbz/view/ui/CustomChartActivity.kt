package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityCustomChartBinding
import com.dbz.view.view.LineChartView

class CustomChartActivity : BaseActivity() {

    private val binding by lazy { ActivityCustomChartBinding.inflate(layoutInflater) }
    private val mXValue1 = arrayListOf<LineChartView.XValue>()
    private val mLineValue1 = arrayListOf<LineChartView.LineValue>()
    private val mXValue2 = arrayListOf<LineChartView.XValue>()
    private val mLineValue2 = arrayListOf<LineChartView.LineValue>()

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.green_color)
        binding.toolbar.title = "Android自定义折线图"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    override fun initData() {
        initArrayList1()
        initArrayList2()
        binding.lineChart1.setXYLineValue(mXValue1, mLineValue1)
        binding.lineChart2.setXYLineValue(mXValue2, mLineValue2)
    }

    private fun initArrayList1() {
        mXValue1.clear()
        mLineValue1.clear()
        mXValue1.apply {
            add(LineChartView.XValue(0f, "12-14"))
            add(LineChartView.XValue(1f, "12-15"))
            add(LineChartView.XValue(2f, "12-16"))
            add(LineChartView.XValue(3f, "12-17"))
            add(LineChartView.XValue(4f, "12-18"))
            add(LineChartView.XValue(5f, "12-19"))
            add(LineChartView.XValue(6f, "12-20"))
            add(LineChartView.XValue(7f, "12-21"))
            add(LineChartView.XValue(8f, "12-22"))
            add(LineChartView.XValue(9f, "12-23"))
        }
        mLineValue1.apply {
            add(LineChartView.LineValue(1100f, "1100"))
            add(LineChartView.LineValue(700f, "700"))
            add(LineChartView.LineValue(2000f, "2000"))
            add(LineChartView.LineValue(500f, "500"))
            add(LineChartView.LineValue(2600f, "2600"))
            add(LineChartView.LineValue(420f, "420"))
            add(LineChartView.LineValue(320f, "320"))
            add(LineChartView.LineValue(0f, "0"))
            add(LineChartView.LineValue(600f, "600"))
            add(LineChartView.LineValue(1500f, "1500"))
        }
    }

    private fun initArrayList2() {
        mXValue2.clear()
        mLineValue2.clear()
        mXValue2.apply {
            add(LineChartView.XValue(0f, "12-14"))
            add(LineChartView.XValue(1f, "12-15"))
            add(LineChartView.XValue(2f, "12-16"))
            add(LineChartView.XValue(3f, "12-17"))
            add(LineChartView.XValue(4f, "12-18"))
            add(LineChartView.XValue(5f, "12-19"))
            add(LineChartView.XValue(6f, "12-20"))
            add(LineChartView.XValue(7f, "12-21"))
            add(LineChartView.XValue(8f, "12-22"))
            add(LineChartView.XValue(9f, "12-23"))
            add(LineChartView.XValue(10f, "12-24"))
            add(LineChartView.XValue(11f, "12-25"))
            add(LineChartView.XValue(12f, "12-26"))
            add(LineChartView.XValue(13f, "12-27"))
            add(LineChartView.XValue(14f, "12-28"))
        }
        mLineValue2.apply {
            add(LineChartView.LineValue(1000f, "1000"))
            add(LineChartView.LineValue(700f, "700"))
            add(LineChartView.LineValue(1800f, "1800"))
            add(LineChartView.LineValue(500f, "500"))
            add(LineChartView.LineValue(2600f, "2600"))
            add(LineChartView.LineValue(420f, "420"))
            add(LineChartView.LineValue(620f, "620"))
            add(LineChartView.LineValue(0f, "0"))
            add(LineChartView.LineValue(600f, "600"))
            add(LineChartView.LineValue(1500f, "1500"))
            add(LineChartView.LineValue(865f, "865"))
            add(LineChartView.LineValue(500f, "500"))
            add(LineChartView.LineValue(123f, "123"))
            add(LineChartView.LineValue(1200f, "1200"))
            add(LineChartView.LineValue(0f, "0"))
        }
    }
}