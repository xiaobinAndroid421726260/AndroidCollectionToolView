package com.dbz.view.base

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.R
import com.dbz.view.popup.LoadingView
import com.gyf.immersionbar.ImmersionBar
import com.jakewharton.rxbinding4.view.clicks
import com.lnshbcs.hibuy.ext.e
import com.permissionx.guolindev.PermissionX
import io.reactivex.rxjava3.disposables.CompositeDisposable
import me.jessyan.autosize.internal.CustomAdapt
import java.util.concurrent.TimeUnit

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/7 15:38
 */
abstract class BaseFragment : Fragment(), CustomAdapt {

    protected val mHandler = Handler(Looper.getMainLooper())
    private var mRootView: FrameLayout? = null
    private var mContentView: View? = null
    private var isFirstLoad = true
    private var isAsyncView = false
    protected lateinit var mContext: Context
    protected var savedInstanceState: Bundle? = null
    private var mDisposable: CompositeDisposable? = null

    protected fun <VM : BaseViewModel> getViewModel(owner: ViewModelStoreOwner = this, clazz: Class<VM>) =
        ViewModelProvider(owner, defaultViewModelProviderFactory)[clazz]

    /**
     * 返回视图
     */
    protected abstract fun getContentView(): View?
    protected abstract fun initView(bundle: Bundle?)
    protected abstract fun initData()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setWindowConfigure()
        if (isAsyncLoadView()) {
            initRootView()
            onCreateAsyncView(inflater, container)
        } else {
            mContentView = getContentView()
        }
        return mRootView ?: mContentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(arguments)
    }

    /**
     * 请求权限
     */
    fun requestPermissions(vararg permissions: String, block: () -> Unit) {
        requestPermissions(listOf(*permissions), block)
    }

    private fun requestPermissions(permissions: List<String>, block: () -> Unit) {
        PermissionX.init(this)
            .permissions(permissions)
//            .onExplainRequestReason { scope, deniedList ->
//                val message = "PermissionX需要您同意以下权限才能正常使用"
//                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
//            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    block()
                } else {
                    ToastUtils.showShort("您拒绝了如下权限：$deniedList")
                }
            }
    }

    open fun initImmersionBar(
        @ColorInt statusBarColor: Int = ContextCompat.getColor(
            mContext,
            R.color.colorPrimary
        ), isDarkFont: Boolean = false, fits: Boolean = true
    ) {
        activity?.let {
            ImmersionBar.with(it)
                .statusBarDarkFont(isDarkFont)
                .fitsSystemWindows(fits) //使用该属性,必须指定状态栏颜色
                .statusBarColorInt(statusBarColor)
                .init()
        }
    }

    /**
     * 为布局内所有控件设置添加防抖处理
     */
    protected fun addDebouncingViews(vararg views: View) {
        if (mDisposable == null) {
            mDisposable = CompositeDisposable()
        }
        for (view in views) {
            val disposable = view.clicks()
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe({ onClickView(view) }) { e("-----onClickView = $it") }
            mDisposable?.add(disposable)
        }
    }

    /**
     * 是否在可见的时候加载
     */
    private fun onResumeLoad() {
        alwaysNeedReload()
        if (!isAsyncLoadView()) {
            if (isFirstLoad) {
                initData()
                isFirstLoad = false
            } else {
                if (isNeedReload()) {
                    initData()
                }
            }
        } else {
            if (isAsyncView) {
                if (isNeedReload()) {
                    initData()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onResumeLoad()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearDisposable()
    }

    /**
     * 取消所有订阅，防止内存泄漏
     */
    private fun clearDisposable() {
        mDisposable?.clear()
    }

    fun showLoadView() {
        activity?.let { LoadingView.getInstance(it).showPopupWindow() }
    }

    fun dismissLoadView() {
        activity?.let { LoadingView.getInstance(it).delayDismiss() }
    }

    fun dismissLoadView(delay: Long) {
        activity?.let { LoadingView.getInstance(it).delayDismiss(delay) }
    }

    fun dismissDelayWith(delay: Long, runnable: Runnable?) {
        activity?.let { LoadingView.getInstance(it).delayDismissWith(delay, runnable) }
    }

    /**
     * 提供子类方法 在设置布局之前
     */
    protected open fun setWindowConfigure() {}

    /**
     * 总是需要重新加载
     */
    protected open fun alwaysNeedReload() {}

    protected open fun onClickView(view: View?) {}

    /**
     * 失败重试,重新加载事件
     */
    protected open fun onReloadClick() {}

    /**
     * 再次可见时，是否重新请求数据，默认为true
     */
    protected open fun isNeedReload() = true

    /**
     * fragment是否懒加载  默认 false
     *
     * @return
     */
    protected open fun isAsyncLoadView() = false

    private fun initRootView() {
        mRootView = FrameLayout(requireActivity().applicationContext)
        mRootView?.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    /**
     * 设置视图
     */
    protected fun setContentView(view: View?) {
        if (isAsyncLoadView()) {
            mRootView?.removeAllViews()
            mRootView?.addView(view)
            isAsyncView = true
        }
    }

    /**
     * 懒加载
     */
    protected open fun onCreateAsyncView(inflater: LayoutInflater, container: ViewGroup?) {}

    override fun isBaseOnWidth() = false

    override fun getSizeInDp() = 667F
}