package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityCollapsingAvatarBinding

class CollapsingAvatarActivity : BaseActivity() {

    private val binding by lazy { ActivityCollapsingAvatarBinding.inflate(layoutInflater) }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.purple_500)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = "哈哈哈11啊哈哈哈哈哈哈"
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

//        //1. 展开时标题颜色
        binding.collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE)
        binding.collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedAppbar)
//        //2. 折叠时标题颜色
        binding.collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE)
        binding.collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedAppbar)

    }

    override fun initData() {

    }

}