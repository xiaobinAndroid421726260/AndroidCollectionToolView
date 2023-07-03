package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityChartReportBinding
import com.dbz.view.view.ChartReportView

class ChartReportActivity : BaseActivity() {

    private val binding by lazy { ActivityChartReportBinding.inflate(layoutInflater) }
    private val mXValue1 = arrayListOf<ChartReportView.XValue>()
    private val mYValue1 = arrayListOf<ChartReportView.YValue>()
    private val mXValue2 = arrayListOf<ChartReportView.XValue>()
    private val mYValue2 = arrayListOf<ChartReportView.YValue>()

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.blue_color)
        binding.toolbar.title = "Android自定义滚动折线图"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        initArrayList1()
        initArrayList2()

        binding.chartview1.setValue(mXValue1, mYValue1)
        binding.chartview1.setCurrentSelectPoint(mXValue1.size)
        binding.chartview1.setOnSelectedActionClick(object : ChartReportView.OnSelectedActionClick{
            override fun onActionClick(position: Int, num: Float, text: String?) {
                ToastUtils.showShort("position : $position   num : $num   text : $text")
            }
        })
        binding.chartview2.setValue(mXValue2, mYValue2)
        binding.chartview2.setCurrentSelectPoint(1)
        binding.chartview2.setOnSelectedActionClick(object : ChartReportView.OnSelectedActionClick{
            override fun onActionClick(position: Int, num: Float, text: String?) {
                ToastUtils.showShort("position : $position   num : $num   text : $text")
            }
        })
    }

    override fun initData() {

    }

    private fun initArrayList1() {
        mXValue1.clear()
        mYValue1.clear()
        mXValue1.apply {
            add(ChartReportView.XValue(10f, "10"))
            add(ChartReportView.XValue(55f, "55"))
            add(ChartReportView.XValue(5f, "5"))
            add(ChartReportView.XValue(60f, "60"))
            add(ChartReportView.XValue(46f, "46"))
            add(ChartReportView.XValue(100f, "100"))
            add(ChartReportView.XValue(23f, "23"))
            add(ChartReportView.XValue(50f, "50"))
            add(ChartReportView.XValue(0f, "0"))
            add(ChartReportView.XValue(65f, "65"))
            add(ChartReportView.XValue(55f, "55"))
            add(ChartReportView.XValue(10f, "10"))
            add(ChartReportView.XValue(79f, "79"))
            add(ChartReportView.XValue(70f, "70"))
            add(ChartReportView.XValue(100f, "100"))
            add(ChartReportView.XValue(88f, "88"))
            add(ChartReportView.XValue(99f, "99"))
            add(ChartReportView.XValue(40f, "40"))
            add(ChartReportView.XValue(60f, "60"))
            add(ChartReportView.XValue(20f, "20"))
            add(ChartReportView.XValue(90f, "90"))
        }
        mYValue1.apply {
            add(ChartReportView.YValue(0f, "0"))
            add(ChartReportView.YValue(2f, "20"))
            add(ChartReportView.YValue(4f, "40"))
            add(ChartReportView.YValue(6f, "60"))
            add(ChartReportView.YValue(8f, "80"))
            add(ChartReportView.YValue(10f, "100"))
        }
    }

    private fun initArrayList2() {
        mXValue2.clear()
        mYValue2.clear()
        mXValue2.apply {
            add(ChartReportView.XValue(450f, "450"))
            add(ChartReportView.XValue(0f, "0"))
            add(ChartReportView.XValue(888f, "888"))
            add(ChartReportView.XValue(650f, "650"))
            add(ChartReportView.XValue(1000f, "1000"))
            add(ChartReportView.XValue(310f, "310"))
        }
        mYValue2.apply {
            add(ChartReportView.YValue(0f, "0"))
            add(ChartReportView.YValue(1f, "100"))
            add(ChartReportView.YValue(2f, "200"))
            add(ChartReportView.YValue(3f, "300"))
            add(ChartReportView.YValue(4f, "400"))
            add(ChartReportView.YValue(5f, "500"))
            add(ChartReportView.YValue(6f, "600"))
            add(ChartReportView.YValue(7f, "700"))
            add(ChartReportView.YValue(8f, "800"))
            add(ChartReportView.YValue(9f, "900"))
            add(ChartReportView.YValue(10f, "1000"))
        }
    }
}