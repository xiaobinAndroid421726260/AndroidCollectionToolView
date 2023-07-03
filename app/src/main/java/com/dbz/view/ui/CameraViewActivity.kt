package com.dbz.view.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityCameraViewBinding
import com.dbz.view.view.camera.BUTTON_STATE_BOTH
import com.dbz.view.view.camera.listener.FlowCameraListener
import java.io.File

class CameraViewActivity : BaseActivity() {

    private val binding by lazy { ActivityCameraViewBinding.inflate(layoutInflater) }

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        window?.statusBarColor = Color.TRANSPARENT

        binding.flowCamera.setBindToLifecycle(this)
        binding.flowCamera.setCaptureMode(BUTTON_STATE_BOTH)
        binding.flowCamera.setRecordVideoMaxTime(30)
        binding.flowCamera.setFlowCameraListener(object : FlowCameraListener {
            override fun captureSuccess(file: File) {
                val intent = Intent().apply {
                    putExtra("mimeType", "Image")
                    putExtra("url", file.absolutePath)
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun recordSuccess(file: File) {
                val intent = Intent().apply {
                    putExtra("mimeType", "Video")
                    putExtra("url", file.absolutePath)
                }
                setResult(RESULT_OK, intent)
                finish()
            }

            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                ToastUtils.showShort(message)
            }
        })

        //左边按钮点击事件
        binding.flowCamera.setLeftClickListener {
            finish()
        }
    }

    override fun initData() {

    }
}