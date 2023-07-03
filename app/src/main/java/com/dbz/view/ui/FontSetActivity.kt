package com.dbz.view.ui

import android.os.Bundle
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityFontSetBinding

class FontSetActivity : BaseActivity() {

    private val binding by lazy { ActivityFontSetBinding.inflate(layoutInflater) }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
    }

    override fun initData() {

    }

}