package com.dbz.view.view.camera

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.StringUtils
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import com.dbz.view.ext.gone
import com.dbz.view.ext.invisible
import com.dbz.view.ext.visible
import com.dbz.view.view.camera.listener.ClickListener
import com.dbz.view.view.camera.listener.ReturnListener
import com.dbz.view.view.camera.listener.TypeListener
import com.ym.chat.widget.camera.listener.CaptureListener

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/5/9 8:31
 */
class CaptureLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var captureListener: CaptureListener? = null       //拍照按钮监听
    private var typeListener: TypeListener? = null             //拍照或录制后接结果按钮监听
    private var returnListener: ReturnListener? = null         //退出按钮监听
    private var leftClickListener: ClickListener? = null       //左边按钮监听
    private var rightClickListener: ClickListener? = null      //右边按钮监听

    private lateinit var btnCapture: CaptureButton             //拍照按钮
    private lateinit var btnConfirm: TypeButton                //确认按钮
    private lateinit var btnCancel: TypeButton                 //取消按钮
    private lateinit var btnReturn: ReturnButton               //返回按钮
    private var ivCustomLeft: ImageView? = null                //左边自定义按钮
    private var ivCustomRight: ImageView? = null               //右边自定义按钮
    private var txtTip: TextView? = null                       //提示文本

    private var textTip: String? = null
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var buttonSize = 0
    private var iconLeft = 0
    private var iconRight = 0

    init {
        val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        manager.defaultDisplay.getMetrics(outMetrics)
        layoutWidth =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                outMetrics.widthPixels
            } else {
                outMetrics.widthPixels / 2
            }
        buttonSize = (layoutWidth / 4.5f).toInt()
//        layoutHeight = buttonSize + buttonSize / 5 * 2 + 100
        layoutHeight = outMetrics.heightPixels
        initView()
        //默认TypeButton为隐藏
        ivCustomRight?.gone()
        btnCancel.gone()
        btnConfirm.gone()
    }

    private fun initView() {
        setWillNotDraw(false)
        //拍照按钮
        btnCapture = CaptureButton(context, buttonSize)
        val btnCaptureParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        btnCaptureParam.gravity = Gravity.CENTER or Gravity.BOTTOM
        btnCaptureParam.bottomMargin = buttonSize / 2
        btnCapture.layoutParams = btnCaptureParam
        btnCapture.setCaptureListener(object : CaptureListener {
            override fun takePictures() {
                if (captureListener != null) {
                    captureListener!!.takePictures()
                }
                startAlphaAnimation()
            }

            override fun recordShort(time: Long) {
                if (captureListener != null) {
                    captureListener!!.recordShort(time)
                }
            }

            override fun recordStart() {
                if (captureListener != null) {
                    captureListener!!.recordStart()
                }
                startAlphaAnimation()
            }

            override fun recordEnd(time: Long) {
                if (captureListener != null) {
                    captureListener!!.recordEnd(time)
                }
            }

            override fun recordZoom(zoom: Float) {
                if (captureListener != null) {
                    captureListener!!.recordZoom(zoom)
                }
            }

            override fun recordError() {
                if (captureListener != null) {
                    captureListener!!.recordError()
                }
            }
        })

        //取消按钮
        btnCancel = TypeButton(context, TypeButton.TYPE_CANCEL, buttonSize)
        val btnCancelParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        btnCancelParam.gravity = Gravity.CENTER_VERTICAL or Gravity.BOTTOM
        btnCancelParam.setMargins(layoutWidth / 4 - buttonSize / 2, 0, 0, buttonSize / 2 + 10.dp2px())
        btnCancel.layoutParams = btnCancelParam
        btnCancel.setOnClickListener {
            if (typeListener != null) {
                typeListener!!.cancel()
            }
        }

        //确认按钮
        btnConfirm = TypeButton(context, TypeButton.TYPE_CONFIRM, buttonSize)
        val btnConfirmParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        btnConfirmParam.gravity = Gravity.CENTER_VERTICAL or Gravity.END or Gravity.BOTTOM
        btnConfirmParam.setMargins(0, 0, layoutWidth / 4 - buttonSize / 2, buttonSize / 2 + 10.dp2px())
        btnConfirm.layoutParams = btnConfirmParam
        btnConfirm.setOnClickListener {
            if (typeListener != null) {
                typeListener!!.confirm()
            }
        }

        //返回按钮
        btnReturn = ReturnButton(context, (buttonSize / 2.5f).toInt())
        val btnReturnParam = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        btnReturnParam.gravity = Gravity.CENTER_VERTICAL or Gravity.BOTTOM
        btnReturnParam.setMargins(layoutWidth / 6, 0, 0, buttonSize)
        btnReturn.layoutParams = btnReturnParam
        btnReturn.setOnClickListener {
            if (leftClickListener != null) {
                leftClickListener!!.onClick()
            }
        }
        //左边自定义按钮
        ivCustomLeft = ImageView(context)
        val ivCustomParamLeft = LayoutParams((buttonSize / 2.5f).toInt(), (buttonSize / 2.5f).toInt())
        ivCustomParamLeft.gravity = Gravity.CENTER_VERTICAL or Gravity.BOTTOM
        ivCustomParamLeft.setMargins(layoutWidth / 6, 0, 0, 0)
        ivCustomLeft?.layoutParams = ivCustomParamLeft
        ivCustomLeft?.setOnClickListener {
            if (leftClickListener != null) {
                leftClickListener!!.onClick()
            }
        }

        //右边自定义按钮
        ivCustomRight = ImageView(context)
        val ivCustomParamRight = LayoutParams((buttonSize / 2.5f).toInt(), (buttonSize / 2.5f).toInt())
        ivCustomParamRight.gravity = Gravity.CENTER_VERTICAL or Gravity.END or Gravity.BOTTOM
        ivCustomParamRight.setMargins(0, 0, layoutWidth / 6, 0)
        ivCustomRight?.layoutParams = ivCustomParamRight
        ivCustomRight?.setOnClickListener {
            if (rightClickListener != null) {
                rightClickListener!!.onClick()
            }
        }
        txtTip = TextView(context).apply {
            val txtParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            txtParam.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            txtParam.setMargins(0, 0, 0, buttonSize * 2 - (buttonSize / 4))
            switchTextTip(btnCapture.buttonState)
            setTextColor(-0x1)
            gravity = Gravity.CENTER
            layoutParams = txtParam
        }

        this.addView(btnCapture)
        this.addView(btnCancel)
        this.addView(btnConfirm)
        this.addView(btnReturn)
        this.addView(ivCustomLeft)
        this.addView(ivCustomRight)
        this.addView(txtTip)
    }

    @SuppressLint("ObjectAnimatorBinding")
    fun startTypeBtnAnimator() {
        //拍照录制结果后的动画
        if (iconLeft != 0) ivCustomLeft?.gone() else btnReturn.gone()
        if (iconRight != 0) ivCustomRight?.gone()
        btnCapture.gone()
        btnCancel.visible()
        btnConfirm.visible()
        btnCancel.isClickable = false
        btnConfirm.isClickable = false
        val animatorCancel =
            ObjectAnimator.ofFloat(btnCancel, "translationX", (layoutWidth / 4).toFloat(), 0f)
        val animatorConfirm =
            ObjectAnimator.ofFloat(btnConfirm, "translationX", (-layoutWidth / 4).toFloat(), 0f)
        val set = AnimatorSet()
        set.playTogether(animatorCancel, animatorConfirm)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                btnCancel.isClickable = true
                btnConfirm.isClickable = true
            }
        })
        set.duration = 500
        set.start()
    }

    /***************************  对外提供的API   */
    fun resetCaptureLayout() {
        btnCapture.resetState()
        btnCancel.gone()
        btnConfirm.gone()
        btnCapture.visible()
        switchTextTip(btnCapture.buttonState)
        txtTip?.visible()
        if (iconLeft != 0) ivCustomLeft?.visible() else btnReturn.visible()
        if (iconRight != 0) ivCustomRight?.visible()
    }

    fun startAlphaAnimation() {
        txtTip?.invisible()
    }

    fun getButtonSize() = buttonSize

    @SuppressLint("ObjectAnimatorBinding")
    fun setTextWithAnimation(tip: String?) {
        txtTip?.text = tip
        val animatorTxtTip = ObjectAnimator.ofFloat(txtTip, "alpha", 0f, 1f, 1f, 0f)
        animatorTxtTip.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                switchTextTip(btnCapture.buttonState)
                txtTip?.alpha = 1f
            }
        })
        animatorTxtTip.duration = 2500
        animatorTxtTip.start()
    }

    fun setDuration(duration: Int) {
        btnCapture.setDuration(duration)
    }

    fun setButtonFeatures(state: Int) {
        btnCapture.setButtonFeatures(state)
        switchTextTip(state)
    }

    private fun switchTextTip(state: Int) {
        when (state) {
            BUTTON_STATE_BOTH -> {
                val take = StringUtils.getString(R.string.click_take_photo)
                val camera = StringUtils.getString(R.string.long_press_camera)
                textTip = "$take, $camera"
                txtTip?.text = textTip
            }
            BUTTON_STATE_ONLY_CAPTURE -> {
                textTip = StringUtils.getString(R.string.click_take_photo)
                txtTip?.text = textTip
            }
            BUTTON_STATE_ONLY_RECORDER -> {
                textTip = StringUtils.getString(R.string.long_press_camera)
                txtTip?.text = textTip
            }
        }
    }

    fun setTip(tip: String) {
        textTip = tip
        txtTip?.text = textTip
    }

    fun setIconSrc(iconLeft: Int, iconRight: Int) {
        this.iconLeft = iconLeft
        this.iconRight = iconRight
        if (this.iconLeft != 0) {
            ivCustomLeft?.setImageResource(iconLeft)
            ivCustomLeft?.visible()
            btnReturn.gone()
        } else {
            ivCustomLeft?.gone()
            btnReturn.visible()
        }
        if (this.iconRight != 0) {
            ivCustomRight?.setImageResource(iconRight)
            ivCustomRight?.visible()
        } else {
            ivCustomRight?.gone()
        }
    }

    fun setLeftClickListener(leftClickListener: ClickListener?) {
        this.leftClickListener = leftClickListener
    }

    fun setRightClickListener(rightClickListener: ClickListener?) {
        this.rightClickListener = rightClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    fun setTypeListener(typeListener: TypeListener) {
        this.typeListener = typeListener
    }

    fun setCaptureListener(captureListener: CaptureListener) {
        this.captureListener = captureListener
    }

    fun setReturnListener(returnListener: ReturnListener) {
        this.returnListener = returnListener
    }
}