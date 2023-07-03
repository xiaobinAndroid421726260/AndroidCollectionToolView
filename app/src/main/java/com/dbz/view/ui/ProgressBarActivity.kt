package com.dbz.view.ui

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityProgressBarBinding

class ProgressBarActivity : BaseActivity() {

    private val binding by lazy { ActivityProgressBarBinding.inflate(layoutInflater) }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.orange)
        binding.toolbar.title = "Android自定义进度条"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.dynamicProgress.reset()
            binding.dynamicProgress.setRevisionProgress(230, 888f)
            binding.dynamicProgress1.reset()
            binding.dynamicProgress1.setRevisionProgress(6300, 8520f)
            binding.dynamicProgress2.reset()
            binding.dynamicProgress2.setRevisionProgress(50, 100f)
        }, 1200)

        binding.progressBar.setProgressMax(100)
            .setProgressTextColor(Color.WHITE)
            .setShowProgressText(true)
            .setRadius(2)
            .setProgress(50)

        binding.progressBar2.setProgressMax(330)
            .setProgressColor(ContextCompat.getColor(this, R.color.orange))
            .setProgressTextColor(Color.WHITE)
            .setShowProgressText(true)
            .setShowRate(true)
            .setRadius(5)
            .setProgress(150)

        binding.progressBar3.setProgressMax(100)
            .setProgressBackColor(ContextCompat.getColor(this, R.color.color_bg))
            .setProgressColor(ContextCompat.getColor(this, R.color.green_color))
            .setProgressTextColor(Color.WHITE)
            .setShowProgressText(true)
            .setRadius(10)
            .setProgress(80)

        binding.progressBar4.setProgressMax(100)
            .setProgressBackColor(ContextCompat.getColor(this, R.color.color_bg))
            .setProgressColor(ContextCompat.getColor(this, R.color.yellow))
            .setShowProgressText(false)
            .setRadius(10)
            .setProgress(70)

        binding.progressBar5.setProgressMax(1000)
            .setProgressBackColor(ContextCompat.getColor(this, R.color.color_bg))
            .setProgressColor(ContextCompat.getColor(this, R.color.orange))
            .setShowProgressText(false)
            .setRadius(20)
            .setProgress(500)

        binding.progressBar6.setProgressMax(1000)
            .setProgressBackColor(ContextCompat.getColor(this, R.color.color_bg))
            .setProgressColor(ContextCompat.getColor(this, R.color.green_color))
            .setShowProgressText(true)
            .setProgressTextColor(ContextCompat.getColor(this, R.color.red))
            .setRadius(30)
            .setProgress(500)
    }

    override fun initData() {

    }
}