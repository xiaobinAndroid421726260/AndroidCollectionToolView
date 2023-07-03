package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityStockPriceBinding
import com.dbz.view.ext.logE
import com.dbz.view.view.StockPriceView

class StockPriceActivity : BaseActivity() {

    private val binding by lazy { ActivityStockPriceBinding.inflate(layoutInflater) }
    private val mXValue1 = arrayListOf<StockPriceView.XValue>()
    private val mYValue1 = arrayListOf<StockPriceView.YValue>()

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.red)
        binding.toolbar.title = "Android自定义折线阴影"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        initArrayList()
        binding.stockPrice.setValue(mXValue1, mYValue1)
        binding.stockPrice.setCurrentSelectPoint(mXValue1.size)
        binding.stockPrice.setOnSelectedActionClick(object : StockPriceView.OnSelectedActionClick {
            override fun onActionClick(position: Int, num: String?, text: String?) {
                "---position : $position   num : $num   text : $text".logE()
                ToastUtils.showShort("position : $position   num : $num   text : $text")
            }
        })
    }

    override fun initData() {

    }

    private fun initArrayList() {
        mXValue1.clear()
        mYValue1.clear()
        mXValue1.apply {
            add(StockPriceView.XValue("03.01", "13.00"))
            add(StockPriceView.XValue("03.02", "13.00"))
            add(StockPriceView.XValue("03.03", "16.00"))
            add(StockPriceView.XValue("03.04", "23.00"))
            add(StockPriceView.XValue("03.05", "23.00"))
            add(StockPriceView.XValue("03.06", "20.00"))
            add(StockPriceView.XValue("03.07", "13.00"))
            add(StockPriceView.XValue("03.08", "16.00"))
            add(StockPriceView.XValue("03.09", "15.00"))
            add(StockPriceView.XValue("03.10", "19.00"))
            add(StockPriceView.XValue("03.11", "16.00"))
            add(StockPriceView.XValue("03.12", "12.00"))
            add(StockPriceView.XValue("03.13", "10.00"))
            add(StockPriceView.XValue("03.14", "20.00"))
            add(StockPriceView.XValue("03.15", "13.00"))
            add(StockPriceView.XValue("03.16", "25.00"))
            add(StockPriceView.XValue("03.17", "19.00"))
            add(StockPriceView.XValue("03.18", "22.00"))
            add(StockPriceView.XValue("03.19", "23.00"))
            add(StockPriceView.XValue("03.20", "23.00"))
            add(StockPriceView.XValue("03.21", "12.00"))
            add(StockPriceView.XValue("03.22", "16.00"))
            add(StockPriceView.XValue("03.23", "13.00"))
            add(StockPriceView.XValue("03.24", "10.00"))
            add(StockPriceView.XValue("03.25", "12.00"))
            add(StockPriceView.XValue("03.26", "20.00"))
            add(StockPriceView.XValue("03.27", "16.00"))
            add(StockPriceView.XValue("03.28", "12.00"))
            add(StockPriceView.XValue("03.29", "13.00"))
            add(StockPriceView.XValue("03.30", "15.00"))
        }
        mYValue1.apply {
            add(StockPriceView.YValue(0, "0.00"))
            add(StockPriceView.YValue(1, "5.00"))
            add(StockPriceView.YValue(2, "15.00"))
            add(StockPriceView.YValue(3, "20.00"))
            add(StockPriceView.YValue(4, "25.00"))
        }
    }
}