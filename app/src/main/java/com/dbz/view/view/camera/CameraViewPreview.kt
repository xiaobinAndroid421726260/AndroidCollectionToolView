package com.dbz.view.view.camera

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.dbz.view.databinding.FlowCameraViewPreviewBinding
import com.dbz.view.ext.logE
import kotlinx.coroutines.launch
import java.util.concurrent.Executors


/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/5/5 14:49
 */
class CameraViewPreview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mContext: Context = context
    private lateinit var binding: FlowCameraViewPreviewBinding
    private var lifecycleOwner: LifecycleOwner? = null

    private var imageCapture: ImageCapture? = null
    private val displayManager by lazy { mContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    init {
        initView()
    }

    private fun initView() {
        binding = FlowCameraViewPreviewBinding.inflate(LayoutInflater.from(mContext), this, false)
        fitsSystemWindows = true
        addView(binding.root)
    }

    fun setBindToLifecycle(lifecycleOwner: LifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                "----------event = $event".logE()
                when (event) {
                    Lifecycle.Event.ON_CREATE -> initCameraPreview()
                    Lifecycle.Event.ON_STOP,
                    Lifecycle.Event.ON_DESTROY,
                    -> closeCamera()
                    Lifecycle.Event.ON_RESUME -> initCameraPreview()
                    else -> {}
                }
            }
        })
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("RestrictedApi")
        override fun onDisplayChanged(displayId: Int) = binding.root.let {

        }
    }

    private fun initCameraPreview() {
        displayManager.registerDisplayListener(displayListener, null)
        lifecycleOwner?.lifecycleScope?.launch {
            bindCameraUseCases()
        }
    }

    private suspend fun bindCameraUseCases() {
        val cameraProvider = ProcessCameraProvider.getInstance(mContext).get()
//        val cameraProvider = ProcessCameraProvider.getInstance(mContext).await()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
            .apply { setSurfaceProvider(binding.previewView.surfaceProvider) }
        binding.previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()

        try {
            cameraProvider.unbindAll()
            lifecycleOwner?.let {
                cameraProvider.bindToLifecycle(
                    it,
                    cameraSelector,
                    imageCapture,
                    preview
                )
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }

    /**
     * 关闭摄像头  释放资源
     */
    private fun closeCamera() {
        displayManager.unregisterDisplayListener(displayListener)
        cameraExecutor.shutdown()
    }
}