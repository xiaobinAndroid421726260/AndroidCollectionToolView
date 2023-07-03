package com.dbz.view

import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.dbz.view.adapter.MainAdapter
import com.dbz.view.base.BaseActivity
import com.dbz.view.bean.MainBean
import com.dbz.view.databinding.ActivityMainBinding
import com.dbz.view.ext.setLinearLayoutManager
import com.dbz.view.ui.*

class MainActivity : BaseActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        binding.toolbar.title = "自定义View合集"
        binding.toolbar.setTitleTextColor(Color.WHITE)
    }

    override fun initData() {
        val list = arrayListOf<MainBean>().apply {
            add(MainBean("画廊效果viewPager  ||  RecyclerView"))
            add(MainBean("自定义进度条"))
            add(MainBean("自定义折线图"))
            add(MainBean("自定义滚动折线图"))
            add(MainBean("自定义滚动折线阴影"))
            add(MainBean("自定义环形统计图"))
            add(MainBean("滚动折叠头像至导航栏"))
            add(MainBean("仿Telegram的媒体"))
        }
        val mAdapter = MainAdapter(itemClickListener = {
            when(it){
                0 -> ActivityUtils.startActivity(RecyclerViewPagerActivity::class.java)
                1 -> ActivityUtils.startActivity(ProgressBarActivity::class.java)
                2 -> ActivityUtils.startActivity(CustomChartActivity::class.java)
                3 -> ActivityUtils.startActivity(ChartReportActivity::class.java)
                4 -> ActivityUtils.startActivity(StockPriceActivity::class.java)
                5 -> ActivityUtils.startActivity(RingChartActivity::class.java)
                6 -> ActivityUtils.startActivity(CollapsingAvatarActivity::class.java)
                7 -> ActivityUtils.startActivity(ChatActivity::class.java)
            }
        })
        binding.recyclerView.setLinearLayoutManager(mAdapter)
        mAdapter.setList(list)
    }
}