package com.dbz.view.dialog

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.view.*
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.*
import com.dbz.view.R
import com.dbz.view.adapter.album.BasePictureAdapter
import com.dbz.view.adapter.album.BasePictureAdapter.Companion.NOTIFY_DATA_CHANGE
import com.dbz.view.databinding.DialogChatAttachPhotoAlertBinding
import com.dbz.view.ext.*
import com.dbz.view.ui.CameraViewActivity
import com.dbz.view.utils.GlideEngine
import com.dbz.view.view.panel.ChatPhotoAlertNestedScrollingParent
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.basic.IPictureSelectorEvent
import com.luck.picture.lib.basic.PictureContentResolver
import com.luck.picture.lib.basic.PictureMediaScannerConnection
import com.luck.picture.lib.config.*
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.dialog.AlbumListPopWindow
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.entity.LocalMediaFolder
import com.luck.picture.lib.interfaces.OnQueryDataResultListener
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener
import com.luck.picture.lib.loader.IBridgeMediaLoader
import com.luck.picture.lib.loader.LocalMediaLoader
import com.luck.picture.lib.loader.LocalMediaPageLoader
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.service.ForegroundService
import com.luck.picture.lib.thread.PictureThreadUtils
import com.luck.picture.lib.utils.*
import com.luck.picture.lib.widget.RecyclerPreloadView
import com.luck.picture.lib.widget.SlideSelectTouchListener
import com.luck.picture.lib.widget.SlideSelectionHandler
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTheming
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/4/6 15:22
 */
class ChatAttachPhotoAlert : DialogFragment(), OnRecyclerViewPreloadMoreListener,
    IPictureSelectorEvent {

    companion object {
        private const val ALERT_LAYOUT_TRANSLATION = 200L
        private const val ALERT_LAYOUT_TRANSLATION_DELAY = 30L
    }

    private lateinit var binding: DialogChatAttachPhotoAlertBinding
    private val mHandler by lazy { Handler(Looper.myLooper()!!) }

    private lateinit var mAdapter: BasePictureAdapter
//    private val mAdapterPreview = BaseBinderAdapterPro()
    private val mLayoutManager by lazy { GridLayoutManager(context, 3) }
    private lateinit var albumListPopWindow: AlbumListPopWindow
    private lateinit var config: PictureSelectionConfig
    private var mDragSelectTouchListener: SlideSelectTouchListener? = null

    private var emojiPopup: EmojiPopup? = null

    /**
     * Media Loader engine
     */
    private lateinit var mLoader: IBridgeMediaLoader

    /**
     * page
     */
    private var mPage = 1
    private var title = ""

    private var isFirstLoadData = true

    private var allFolderSize = 0

    //设置标志位，避免重复换行频繁设置背景导致的屏幕刷新闪屏
    private var is6dpRound = false

    // 滚动的距离
    private var onScrollOffset = 0

    // 可向上滑动的距离
    private var scrollTop = 0

    // 可向下滑动的距离
    private var scrollBottom = 0

    private lateinit var mActivity: FragmentActivity

    //<editor-fold desc="DialogFragment弹框代码">
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initView()
        clickListener()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentDialog)
        onCreateConfigLoader()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as FragmentActivity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = DialogChatAttachPhotoAlertBinding.inflate(layoutInflater, null, false)
        if (Build.VERSION.SDK_INT >= 30) {
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else {
            dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        dialog.window?.statusBarColor = Color.TRANSPARENT
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val params = dialog.window!!.attributes
        params.gravity = Gravity.BOTTOM
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        dialog.window?.attributes = params
        dialog.setContentView(binding.root)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismissWithDelayRun()
                true
            } else {
                false
            }
        }
        return dialog
    }

    //</editor-fold>

    // 初始化获取数据源的状态
    private fun onCreateConfigLoader() {
        PictureSelectionConfig.imageEngine = GlideEngine.createGlideEngine()
        config = PictureSelectionConfig.getInstance()
        config.chooseMode = SelectMimeType.ofAll()
        config.isDisplayCamera = true
        config.isPageSyncAsCount = true
        config.isPageStrategy = true
        config.isGif = true
        config.isBmp = true
        config.maxSelectNum = Int.MAX_VALUE
        mLoader = if (config.isPageStrategy) LocalMediaPageLoader() else LocalMediaLoader()
        mLoader.initConfig(mActivity, config)
    }

    /**
     * 处理向下滑动
     */
    private fun setOnTouchListenerScrollHandler(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_UP) {
            mHandler.postDelayed({
                // 当向下滑动距离 超过 向下总滑动的 0.1 时 关闭弹框
                if (-onScrollOffset > -(scrollBottom * 0.1)) {
                    dismissWithDelayRun()
                }
            }, 120)
        }
    }

    @SuppressLint("ClickableViewAccessibility", "NotifyDataSetChanged")
    private fun initView() {
        // 弹出动画之后 设置背景半透明
        mHandler.postDelayed({
            // 在显示之后 设置没有动画   否则跳转页面在返回是会有弹出动画
            dialog?.window?.setWindowAnimations(R.style.DialogNoAnimation)
            binding.nestedScrolling.setDoPerformAnyCallbacks(true)
            val animator = ValueAnimator.ofInt(0, 128)
            animator.duration = 200
            animator.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                dialog?.window!!.statusBarColor =
                    ColorUtils.setAlphaComponent(Color.parseColor("#80222229"), animatedValue)
                binding.nestedScrolling.setBackgroundColor(
                    ColorUtils.setAlphaComponent(Color.parseColor("#80222229"), animatedValue)
                )
            }
            animator.start()
        }, 400)
        binding.nestedScrolling.setOnOffsetScrollRangeListener(object :
            ChatPhotoAlertNestedScrollingParent.OnOffsetScrollRangeListener {
            override fun offsetScroll(
                fraction: Float,
                offset: Int,
                scrollRangeTop: Int,
                scrollRangeBottom: Int,
                isScrollTopShowTitle: Boolean,
            ) {
                this@ChatAttachPhotoAlert.onScrollOffset = offset
                this@ChatAttachPhotoAlert.scrollTop = scrollRangeTop
                this@ChatAttachPhotoAlert.scrollBottom = scrollRangeBottom
            }

            override fun isScrollTopShowTitle(
                offset: Int,
                scrollRangeTop: Int,
                isScrollTopShowTitle: Boolean,
            ) {
                if (offset == scrollTop) {
                    dialog?.window!!.statusBarColor = Color.parseColor("#0096f0")
                    binding.flTitle.visible()
                } else {
                    dialog?.window!!.statusBarColor = Color.parseColor("#80222229")
                    binding.flTitle.invisible()
                }
            }
        })
        initAlbumListPopWindow()
//        binding.recyclerViewPreview.setOnTouchListener { _, event ->
//            setOnTouchListenerScrollHandler(event)
//            return@setOnTouchListener false
//        }
        binding.recyclerView.setOnTouchListener { _, event ->
            setOnTouchListenerScrollHandler(event)
            return@setOnTouchListener false
        }
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setReachBottomRow(RecyclerPreloadView.BOTTOM_PRELOAD)
        binding.recyclerView.setOnRecyclerViewPreloadListener(this)
        binding.recyclerView.layoutManager = mLayoutManager
        binding.recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 6.dp2px(), true))
        mAdapter = BasePictureAdapter(this, openCameraClick = {
            openCameraPermissions()
        }, onItemClickListener = { _, position ->
            onStartPreview(position)
        }, onItemLongClick = { _, position ->
            if (mDragSelectTouchListener != null) {
                val vibrator = activity?.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(50)
                mDragSelectTouchListener?.startSlideSelection(if (mAdapter.isDisplayCamera()) position - 1 else position)
            }
        }, onSelectListener = { selectedView, ivPicture, data, position ->
            val selectResultCode = confirmSelect(data, selectedView.isSelected)
            selectedMedia(selectedView, ivPicture, isSelected(data))
            mAdapter.notifyItemChanged(position)
            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                val animation = AnimationUtils.loadAnimation(context, com.luck.picture.lib.R.anim.ps_anim_modal_in)
                selectedView.startAnimation(animation)
            }
        })
        mAdapter.setDisplayCamera(true)
        binding.recyclerView.adapter = mAdapter
        // 图片预览
//        binding.recyclerViewPreview.setHasFixedSize(true)
//        binding.recyclerViewPreview.layoutManager = LinearLayoutManager(mActivity)
//        binding.recyclerViewPreview.adapter =
//            mAdapterPreview.apply { addItemBinder(PreviewImageGroupAdapter()) }
        SelectedManager.addAllSelectResult(mAdapter.data)
        binding.recyclerView.setOnRecyclerViewScrollStateListener(object :
            OnRecyclerViewScrollStateListener {
            override fun onScrollFast() {
                if (PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.pauseRequests(context)
                }
            }

            override fun onScrollSlow() {
                if (PictureSelectionConfig.imageEngine != null) {
                    PictureSelectionConfig.imageEngine.resumeRequests(context)
                }
            }
        })
        val selectedPosition = HashSet<Int>()
        val slideSelectionHandler =
            SlideSelectionHandler(object : SlideSelectionHandler.ISelectionHandler {
                override fun getSelection(): HashSet<Int> {
                    for (i in 0 until SelectedManager.getSelectCount()) {
                        val media = SelectedManager.getSelectedResult()[i]
                        selectedPosition.add(media.position)
                    }
                    return selectedPosition
                }

                override fun changeSelection(
                    start: Int,
                    end: Int,
                    isSelected: Boolean,
                    calledFromOnStart: Boolean,
                ) {
                    // 下标是0的时候不处理 因为是相机
//                    if (start == 0 || end == 0) return
                    val adapterData: ArrayList<LocalMedia> = mAdapter.data as ArrayList
                    if (adapterData.size == 0 || start > adapterData.size) return
                    val media = adapterData[start]
                    val selectResultCode: Int =
                        confirmSelect(media, SelectedManager.getSelectedResult().contains(media))
                    mDragSelectTouchListener?.setActive(selectResultCode != SelectedManager.INVALID)
                }
            })
        mDragSelectTouchListener = SlideSelectTouchListener()
            .setRecyclerViewHeaderCount(if (mAdapter.isDisplayCamera()) 1 else 0)
            .withSelectListener(slideSelectionHandler)
        mDragSelectTouchListener?.let { binding.recyclerView.addOnItemTouchListener(it) }
        val emojiTheming = EmojiTheming(
            ContextCompat.getColor(mActivity, R.color.color_F2F3F5),
            ContextCompat.getColor(mActivity, R.color.black),
            ContextCompat.getColor(mActivity, R.color.blue_color),
            ContextCompat.getColor(mActivity, R.color.color_ECEDF1),
            ContextCompat.getColor(mActivity, R.color.blue_color),
            ContextCompat.getColor(mActivity, R.color.blue_color)
        )
        emojiPopup = EmojiPopup(rootView = binding.emotionContainerFrameLayout,
            editText = binding.layoutBottomEdit.editText,
            onEmojiPopupShownListener = {
                binding.layoutBottomEdit.emotionImageView.setImageResource(R.drawable.icon_chat_key)
            },
            onEmojiPopupDismissListener = {
                binding.layoutBottomEdit.emotionImageView.setImageResource(R.drawable.icon_chat_emo)
            },
            onSoftKeyboardCloseListener = {},
            onSoftKeyboardOpenListener = {},
            onEmojiBackspaceClickListener = {},
            onEmojiClickListener = {}, theming = emojiTheming
        )
        requestLoadData()
    }

    /**
     * 计算选中数量
     */
    private fun notifySelectNumber() {
        val count = SelectedManager.getSelectCount()
        binding.tvSelectNum.text = if (count > 0) {
            StringUtils.getString(R.string.selected_picture, count)
        } else {
            StringUtils.getString(R.string.not_select)
        }
        if (count > 0) {
            binding.tvSelectNum.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.icon_nav_back_black,
                0
            )
            binding.tvSelectNum.setTextColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.color_323233
                )
            )
            binding.tvNumber.text = count.toString()
            binding.tvNumber.textSize = if (count > 99) 11f else 14f
            // 如果没显示的时候 才会弹出动画
            if (!binding.clBottomSendLayout.isVisible) {
                //设置动画，从下向上滑动  布局
                val translateAnimationLayout = TranslateAnimation(
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f,
                    TranslateAnimation.RELATIVE_TO_SELF, 1f,
                    TranslateAnimation.RELATIVE_TO_SELF, 0f
                )
                translateAnimationLayout.duration = 360 //设置动画的过渡时间
                binding.clBottomSendLayout.startAnimation(translateAnimationLayout)
                binding.clBottomSendLayout.animation.setAnimationListener(object :
                    AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {
                        binding.clBottomSendLayout.visible()
                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        binding.clBottomSendLayout.clearAnimation()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }
        } else {
            binding.tvSelectNum.setTextColor(
                ContextCompat.getColor(
                    mActivity,
                    R.color.color_969799
                )
            )
            binding.tvSelectNum.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            binding.tvNumber.text = ""
            //设置动画，从上向下滑动  布局
            val translateAnimationLayout = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 1f
            )
            translateAnimationLayout.duration = 360 //设置动画的过渡时间
            binding.clBottomSendLayout.startAnimation(translateAnimationLayout)
            binding.clBottomSendLayout.animation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    binding.clBottomSendLayout.invisible()
                }

                override fun onAnimationEnd(animation: Animation?) {
                    binding.clBottomSendLayout.clearAnimation()
                    binding.clBottomSendLayout.invisible()
                    binding.emotionContainerFrameLayout.gone()
                    KeyboardUtils.hideSoftInput(binding.layoutBottomEdit.editText)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            binding.clBottomSendLayout.invisible()
        }
    }

    private fun clickListener() {
        binding.layoutBottomEdit.editText.doAfterTextChanged {
            //文字或换行导致输入框超过两行更换输入框圆角背景
            if (binding.layoutBottomEdit.editText.lineCount > 1) {
                if (!is6dpRound) {
                    binding.layoutBottomEdit.llEditSpeak.setBackgroundResource(R.drawable.shape_white_radius_6)
                    is6dpRound = true
                }
            } else {
                binding.layoutBottomEdit.llEditSpeak.setBackgroundResource(R.drawable.shape_white_radius_20)
                is6dpRound = false
            }
        }
        binding.ivBack.setOnClickListener { dismissWithDelayRun() }
        binding.llTitle.setOnClickListener {
            albumListPopWindow.showAsDropDown(binding.tvTitle)
        }
        binding.viewSpace.setOnClickListener { dismissWithDelayRun() }
        binding.layoutBottomEdit.llEditSpeak.setOnClickListener { KeyboardUtils.showSoftInput(binding.layoutBottomEdit.editText) }
        binding.layoutBottomEdit.clBottomSendEdit.setOnLongClickListener { true }
        binding.layoutBottomEdit.emotionImageView.setOnClickListener {
            emojiPopup?.toggle()
        }
        binding.tvSelectNum.setOnClickListener {
//            switchAlbumPreviewAnimation()
        }
        binding.sendButton.setOnClickListener { dispatchTransformResult() }
    }

    fun onSelectedChange(isAddRemove: Boolean, currentMedia: LocalMedia) {
        notifySelectNumber()
        mAdapter.notifyItemChanged(currentMedia.position, NOTIFY_DATA_CHANGE)
        if (!isAddRemove) {
            sendChangeSubSelectPositionEvent()
        }

//        // 拿到所有数据 设置图片预览
//        val result = SelectedManager.getSelectedResult()
//        val previewBean = arrayListOf<ImageTextMsgBean>()
//        val imageData = arrayListOf<ImageBean>()
//        result.forEachIndexed { index, localMedia ->
//            if (localMedia.width == 0 || localMedia.height == 0) {
//                BitmapUtils.getPicHW(mActivity, result) {
//                    onSelectedChange(isAddRemove, currentMedia)
//                }
//                return
//            }
//            val imageBean = ImageBean(
//                localMedia.width.toFloat(),
//                localMedia.height.toFloat(),
//                localMedia.path,
//                type = localMedia.mimeType,
//            )
//            imageData.add(imageBean)
//            // 一组最多为10个  如果是10的倍数 那么就清除数据重新添加
//            if ((index + 1) % 10 == 0) {
//                val list = arrayListOf<ImageBean>()
//                list.addAll(imageData)
//                val bean = ImageTextMsgBean(list, "")
//                previewBean.add(bean)
//                imageData.clear()
//            }
//            // 如果是最后一个的时候 那么也添加数据 如果是10的倍数 那么已经添加了 就不用再添加了
//            if (result.size == index + 1 && (index + 1) % 10 != 0) {
//                val list = arrayListOf<ImageBean>()
//                list.addAll(imageData)
//                val bean = ImageTextMsgBean(list, "")
//                previewBean.add(bean)
//            }
//        }
//        mAdapterPreview.setList(previewBean)
    }

    private fun sendChangeSubSelectPositionEvent() {
        for (index in 0 until SelectedManager.getSelectCount()) {
            val media = SelectedManager.getSelectedResult()[index]
            media.num = index + 1
            mAdapter.notifyItemChanged(media.position, NOTIFY_DATA_CHANGE)
        }
    }

    override fun onRecyclerViewPreloadMore() {
        loadMoreMediaData()
    }

    private fun preloadPageFirstData(): Boolean {
        var isPreload = false
        if (config.isPageStrategy) {
            val firstFolder = LocalMediaFolder()
            firstFolder.bucketId = PictureConfig.ALL.toLong()
            SelectedManager.setCurrentLocalMediaFolder(firstFolder)
            loadFirstPageMediaData(firstFolder.bucketId)
            isPreload = true
        }
        return isPreload
    }

    // 加载数据 判断权限
    private fun requestLoadData() {
        if (XXPermissions.isGranted(mActivity, Permission.Group.STORAGE)) {
            loadAllAlbumData()
        } else {
            XXPermissions.with(mActivity)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                .request(object : OnPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            val tips = StringUtils.getString(R.string.photos_media_missing_storage_permissions)
                            permissionsDialog(mActivity, permissions, tips)
                            return
                        }
                        loadAllAlbumData()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(mActivity, permissions)
                        } else {
                            val tips =
                                StringUtils.getString(R.string.photos_media_missing_storage_permissions)
                            permissionsDialog(mActivity, permissions, tips)
                        }
                    }
                })
        }
    }

    override fun loadAllAlbumData() {
        preloadPageFirstData()
        mLoader.loadAllAlbum { localMediaFolder ->
            handleAllAlbumData(localMediaFolder)
        }
    }

    private fun handleAllAlbumData(result: List<LocalMediaFolder>) {
        if (ActivityCompatHelper.isDestroy(mActivity)) return
        if (result.isNotEmpty()) {
            val firstFolder = result[0]
            SelectedManager.setCurrentLocalMediaFolder(firstFolder)
            binding.tvTitle.text = StringUtils.getString(R.string.album)
            albumListPopWindow.bindAlbumData(result)
            binding.recyclerView.isEnabledLoadMore = true
        } else {
            showDataNull()
        }
    }

    override fun loadFirstPageMediaData(firstBucketId: Long) {
        mPage = 1
        binding.recyclerView.isEnabledLoadMore = true
        mLoader.loadPageMediaData(firstBucketId, mPage, mPage * config.pageSize,
            object : OnQueryDataResultListener<LocalMedia>() {
                override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                    handleFirstPageMedia(result, isHasMore)
                }
            })
    }

    private fun handleFirstPageMedia(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(mActivity)) return
        binding.recyclerView.isEnabledLoadMore = isHasMore
        if (binding.recyclerView.isEnabledLoadMore && result.size == 0) {
            // 如果isHasMore为true但result.size() = 0;
            // 那么有可能是开启了某些条件过滤，实际上是还有更多资源的再强制请求
            onRecyclerViewPreloadMore()
        } else {
            setAdapterData(result)
        }
    }

    override fun loadOnlyInAppDirectoryAllMediaData() {
        mLoader.loadOnlyInAppDirAllMedia { folder -> handleInAppDirAllMedia(folder) }
    }

    private fun handleInAppDirAllMedia(folder: LocalMediaFolder?) {
        if (!ActivityCompatHelper.isDestroy(mActivity)) {
            val sandboxDir = config.sandboxDir
            val isNonNull = folder != null
            val folderName = if (isNonNull) folder!!.folderName else File(sandboxDir).name
            binding.tvTitle.text = folderName
            if (isNonNull) {
                SelectedManager.setCurrentLocalMediaFolder(folder)
                setAdapterData(folder!!.data)
            } else {
                showDataNull()
            }
        }
    }

    /**
     * 加载更多
     */
    override fun loadMoreMediaData() {
        if (binding.recyclerView.isEnabledLoadMore) {
            mPage++
            val localMediaFolder = SelectedManager.getCurrentLocalMediaFolder()
            val bucketId = localMediaFolder?.bucketId ?: 0
            mLoader.loadPageMediaData(bucketId, mPage, config.pageSize,
                object : OnQueryDataResultListener<LocalMedia>() {
                    override fun onComplete(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
                        handleMoreMediaData(result, isHasMore)
                    }
                })
        }
    }

    /**
     * 处理加载更多的数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleMoreMediaData(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(mActivity)) return
        binding.recyclerView.isEnabledLoadMore = isHasMore
        if (binding.recyclerView.isEnabledLoadMore) {
            removePageCameraRepeatData(result.toMutableList())
            if (result.isNotEmpty()) {
//                val positionStart: Int = mAdapter.data.size
                mAdapter.data.addAll(result)
                mAdapter.notifyDataSetChanged()
                if (mAdapter.data.isEmpty()) {
                    showDataNull()
                } else {
                    hideDataNull()
                }
            } else {
                // 如果没数据这里在强制调用一下上拉加载更多，防止是因为某些条件过滤导致的假为0的情况
                onRecyclerViewPreloadMore()
            }
            if (result.size < PictureConfig.MIN_PAGE_SIZE) {
                // 当数据量过少时强制触发一下上拉加载更多，防止没有自动触发加载更多
                binding.recyclerView.onScrolled(binding.recyclerView.scrollX,
                    binding.recyclerView.scrollY)
            }
        }
    }

    private fun removePageCameraRepeatData(result: MutableList<LocalMedia>) {
        try {
            if (config.isPageStrategy) {
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    if (mAdapter.data.contains(iterator.next())) {
                        iterator.remove()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {

        }
    }

    /**
     * initAlbumListPopWindow
     */
    private fun initAlbumListPopWindow() {
        albumListPopWindow = AlbumListPopWindow.buildPopWindow(mActivity)
        albumListPopWindow.setOnPopupWindowStatusListener(object :
            AlbumListPopWindow.OnPopupWindowStatusListener {
            override fun onShowPopupWindow() {
                if (!config.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(binding.ivArrow, true)
                }
            }

            override fun onDismissPopupWindow() {
                if (!config.isOnlySandboxDir) {
                    AnimUtils.rotateArrow(binding.ivArrow, false)
                }
            }
        })
        albumListPopWindow.setOnIBridgeAlbumWidget { position, curFolder ->
            val isDisplayCamera = config.isDisplayCamera && curFolder.bucketId == PictureConfig.ALL.toLong()
            mAdapter.setDisplayCamera(isDisplayCamera)
            if (position == 0) {
                binding.tvTitle.text = StringUtils.getString(R.string.album)
            } else {
                binding.tvTitle.text = curFolder.folderName
            }
            val lastFolder = SelectedManager.getCurrentLocalMediaFolder()
            val lastBucketId = lastFolder.bucketId
            if (curFolder.bucketId != lastBucketId) {
                // 1、记录一下上一次相册数据加载到哪了，到时候切回来的时候要续上
                val laseFolderData = ArrayList(mAdapter.data)
                lastFolder.data = laseFolderData
                lastFolder.currentDataPage = mPage
                lastFolder.isHasMore = binding.recyclerView.isEnabledLoadMore

                // 2、判断当前相册是否请求过，如果请求过则不从MediaStore去拉取了
                if (curFolder.data.size > 0 && !curFolder.isHasMore) {
                    setAdapterData(curFolder.data)
                    mPage = curFolder.currentDataPage
                    binding.recyclerView.isEnabledLoadMore = curFolder.isHasMore
                    binding.recyclerView.smoothScrollToPosition(0)
                } else {
                    // 3、从MediaStore拉取数据
                    mPage = 1
                    mLoader.loadPageMediaData(curFolder.bucketId, mPage, config.pageSize,
                        object : OnQueryDataResultListener<LocalMedia>() {
                            override fun onComplete(
                                result: ArrayList<LocalMedia>,
                                isHasMore: Boolean,
                            ) {
                                handleSwitchAlbum(result, isHasMore)
                            }
                        })
                }
            }
            SelectedManager.setCurrentLocalMediaFolder(curFolder)
            albumListPopWindow.dismiss()
            if (mDragSelectTouchListener != null && config.isFastSlidingSelect) {
                mDragSelectTouchListener!!.setRecyclerViewHeaderCount(if (mAdapter.isDisplayCamera()) 1 else 0)
            }
        }
    }

    private fun handleSwitchAlbum(result: ArrayList<LocalMedia>, isHasMore: Boolean) {
        if (ActivityCompatHelper.isDestroy(mActivity)) return
        binding.recyclerView.isEnabledLoadMore = isHasMore
        if (result.size == 0) {
            // 如果从MediaStore拉取都没有数据了，adapter里的可能是缓存所以也清除
            mAdapter.data.clear()
        }
        setAdapterData(result)
        binding.recyclerView.onScrolled(0, 0)
        binding.recyclerView.smoothScrollToPosition(0)
    }

    private fun setAdapterData(result: ArrayList<LocalMedia>) {
        mAdapter.setList(result)
        SelectedManager.clearAlbumDataSource()
        SelectedManager.clearDataSource()
        if (mAdapter.data.isEmpty()) {
            showDataNull()
        } else {
            hideDataNull()
        }
        if (isFirstLoadData && mAdapter.data.isNotEmpty()) {
            isFirstLoadData = false
            val delayMills = if (RomUtils.isSamsung()) ALERT_LAYOUT_TRANSLATION_DELAY else 0L
            mHandler.postDelayed({ mAdapter.notifyItemChanged(0, NOTIFY_DATA_CHANGE) }, delayMills)
        }
    }

    private fun confirmSelect(currentMedia: LocalMedia, isSelected: Boolean): Int {
//        val checkSelectValidity = isCheckSelectValidity(currentMedia, isSelected)
//        if (checkSelectValidity != SelectedManager.SUCCESS) {
//            return SelectedManager.INVALID
//        }
        // 先做选中判断逻辑  在执行以下   能否选中 在这里判断

        val selectedResult: MutableList<LocalMedia> = SelectedManager.getSelectedResult()
        val resultCode: Int
        if (isSelected) {
            selectedResult.remove(currentMedia)
            resultCode = SelectedManager.REMOVE
        } else {
            selectedResult.add(currentMedia)
            currentMedia.num = selectedResult.size
            resultCode = SelectedManager.ADD_SUCCESS
        }
        onSelectedChange(resultCode == SelectedManager.ADD_SUCCESS, currentMedia)
        return resultCode
    }

    /**
     * 预览图片
     *
     * @param position        预览图片下标
     */
    private fun onStartPreview(position: Int) {
        dialog?.window?.setWindowAnimations(R.style.DialogNoAnimation)
        binding.emotionContainerFrameLayout.gone()
        KeyboardUtils.hideSoftInput(binding.layoutBottomEdit.editText)
        val data = ArrayList(mAdapter.data)
        val totalNum = SelectedManager.getCurrentLocalMediaFolder().folderTotalNum
        val currentBucketId = SelectedManager.getCurrentLocalMediaFolder().bucketId
//        PictureSelectorPreviewActivity.newInstance(
//            mActivity,
//            false,
//            binding.tvTitle.text.toString(),
//            false,
//            if (mAdapter.isDisplayCamera()) position - 1 else position,
//            totalNum,
//            mPage,
//            currentBucketId,
//            data
//        )
    }

    //<editor-fold desc="打开相机">

    /**
     * 打开相机activity
     */
    private fun openCameraActivity() {
        // 先关闭列表中的相机
        ActivityUtils.startActivityForResult(
            mActivity,
            CameraViewActivity::class.java,
            PictureConfig.REQUEST_CAMERA
        )
    }

    /**
     * 打开相机  判断权限
     */
    private fun openCameraPermissions() {
        if (PermissionChecker.checkSelfPermission(
                mActivity,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        ) {
            openCameraActivity()
        } else {
            XXPermissions.with(mActivity)
                // 申请多个权限
                .permission(Permission.CAMERA, Permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback {

                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        if (!allGranted) {
                            val tips =
                                StringUtils.getString(R.string.camera_missing_storage_permissions)
                            permissionsDialog(mActivity, permissions, tips)
                            return
                        }
                        openCameraActivity()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean,
                    ) {
                        if (doNotAskAgain) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(mActivity, permissions)
                        } else {
                            val tips =
                                StringUtils.getString(R.string.camera_missing_storage_permissions)
                            permissionsDialog(mActivity, permissions, tips)
                        }
                    }
                })
        }
    }
    //    </editor-fold>

    //<editor-fold desc="相机事件回调处理">
//    ***********************  相机事件回调处理  ***********************
    /**
     * 相机事件回调处理
     */
    fun dispatchHandleCamera(intent: Intent?) {
        ForegroundService.stopService(mActivity)
        PictureThreadUtils.executeByIo(object : PictureThreadUtils.SimpleTask<LocalMedia?>() {
            override fun doInBackground(): LocalMedia? {
                val outputPath = getOutputPath(intent)
                if (!TextUtils.isEmpty(outputPath)) {
                    config.cameraPath = outputPath
                }
                if (TextUtils.isEmpty(config.cameraPath)) {
                    return null
                }
                if (config.chooseMode == SelectMimeType.ofAudio()) {
                    copyOutputAudioToDir()
                }
                return buildLocalMedia(config.cameraPath)
            }

            override fun onSuccess(result: LocalMedia?) {
                PictureThreadUtils.cancel(this)
                if (result != null) {
                    onScannerScanFile(result)
                    dispatchCameraMediaResult(result)
                }
            }
        })
    }

    /**
     * 尝试匹配查找自定义相机返回的路径
     *
     * @param data
     * @return
     */
    private fun getOutputPath(data: Intent?): String? {
        if (data == null) return null
//        var outPutUri = data.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        val url = data.getStringExtra("url")
//        if (config.chooseMode == SelectMimeType.ofAudio() && outPutUri == null) {
//            outPutUri = data.data
//        }
//        if (outPutUri == null) {
//            return null
//        }
        return if (PictureMimeType.isContent(url.toString())) url.toString() else Uri.parse(url)
            .toString()
//        return if (PictureMimeType.isContent(outPutUri.toString())) outPutUri.toString() else outPutUri.path
    }

    /**
     * 刷新相册
     *
     * @param media 要刷新的对象
     */
    private fun onScannerScanFile(media: LocalMedia) {
        if (ActivityCompatHelper.isDestroy(mActivity)) return
        if (SdkVersionUtils.isQ()) {
            if (PictureMimeType.isHasVideo(media.mimeType) && PictureMimeType.isContent(config.cameraPath)) {
                PictureMediaScannerConnection(mActivity, media.realPath)
            }
        } else {
            val path =
                if (PictureMimeType.isContent(config.cameraPath)) media.realPath else config.cameraPath
            PictureMediaScannerConnection(mActivity, path)
            if (PictureMimeType.isHasImage(media.mimeType)) {
                val dirFile = File(path)
                val lastImageId = MediaUtils.getDCIMLastImageId(mActivity, dirFile.parent)
                if (lastImageId != -1) {
                    MediaUtils.removeMedia(mActivity, lastImageId)
                }
            }
        }
    }

    /**
     * buildLocalMedia
     *
     * @param absolutePath
     */
    private fun buildLocalMedia(absolutePath: String?): LocalMedia {
        val media: LocalMedia = LocalMedia.generateLocalMedia(mActivity, absolutePath)
        media.chooseModel = config.chooseMode
        if (SdkVersionUtils.isQ() && !PictureMimeType.isContent(absolutePath)) {
            media.sandboxPath = absolutePath
        } else {
            media.sandboxPath = null
        }
        if (config.isCameraRotateImage && PictureMimeType.isHasImage(media.mimeType)) {
            BitmapUtils.rotateImage(mActivity, absolutePath)
        }
        return media
    }

    /**
     * copy录音文件至指定目录
     */
    private fun copyOutputAudioToDir() {
        try {
            if (!TextUtils.isEmpty(config.outPutAudioDir) && PictureMimeType.isContent(config.cameraPath)) {
                val inputStream =
                    PictureContentResolver.getContentResolverOpenInputStream(
                        mActivity,
                        Uri.parse(config.cameraPath)
                    )
                val audioFileName: String = if (TextUtils.isEmpty(config.outPutAudioFileName)) {
                    ""
                } else {
                    if (config.isOnlyCamera) config.outPutAudioFileName else System.currentTimeMillis()
                        .toString() + "_" + config.outPutAudioFileName
                }
                val outputFile = PictureFileUtils.createCameraFile(
                    mActivity,
                    config.chooseMode, audioFileName, "", config.outPutAudioDir
                )
                val outputStream = FileOutputStream(outputFile.absolutePath)
                val isCopyStatus = PictureFileUtils.writeFileFromIS(inputStream, outputStream)
                if (isCopyStatus) {
                    MediaUtils.deleteUri(mActivity, config.cameraPath)
                    config.cameraPath = outputFile.absolutePath
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun dispatchCameraMediaResult(media: LocalMedia) {
        val exitsTotalNum = albumListPopWindow.firstAlbumImageCount
        if (!isAddSameImp(exitsTotalNum)) {
            mAdapter.data.add(0, media)
        }
        // 进入下面 永远等于false
        if (config.selectionMode == SelectModeConfig.SINGLE && config.isDirectReturnSingle) {
            SelectedManager.clearSelectResult()
            val selectResultCode = confirmSelect(media, false)
            if (selectResultCode == SelectedManager.ADD_SUCCESS) {
                dispatchTransformResult()
            }
        } else {
            confirmSelect(media, false)
        }
        mAdapter.notifyItemInserted(if (mAdapter.isDisplayCamera()) 1 else 0)
        mAdapter.notifyItemRangeChanged(if (mAdapter.isDisplayCamera()) 1 else 0,
            mAdapter.getDefItemCountDataSize())
        if (config.isOnlySandboxDir) {
            var currentLocalMediaFolder = SelectedManager.getCurrentLocalMediaFolder()
            if (currentLocalMediaFolder == null) {
                currentLocalMediaFolder = LocalMediaFolder()
            }
            currentLocalMediaFolder.bucketId = ValueOf.toLong(media.parentFolderName.hashCode())
            currentLocalMediaFolder.folderName = media.parentFolderName
            currentLocalMediaFolder.firstMimeType = media.mimeType
            currentLocalMediaFolder.firstImagePath = media.path
            currentLocalMediaFolder.folderTotalNum = mAdapter.data.size
            currentLocalMediaFolder.currentDataPage = mPage
            currentLocalMediaFolder.isHasMore = false
            val data = ArrayList(mAdapter.data)
            currentLocalMediaFolder.data = data
            binding.recyclerView.isEnabledLoadMore = false
            SelectedManager.setCurrentLocalMediaFolder(currentLocalMediaFolder)
        } else {
            mergeFolder(media)
        }
        allFolderSize = 0
    }

    /**
     * 拍照出来的合并到相应的专辑目录中去
     *
     * @param media
     */
    private fun mergeFolder(media: LocalMedia) {
        val allFolder: LocalMediaFolder
        val albumList = albumListPopWindow.albumList
        if (albumListPopWindow.folderCount == 0) {
            // 1、没有相册时需要手动创建相机胶卷
            allFolder = LocalMediaFolder()
            val folderName: String = if (TextUtils.isEmpty(config.defaultAlbumName)) {
                if (config.chooseMode == SelectMimeType.ofAudio())
                    StringUtils.getString(com.luck.picture.lib.R.string.ps_all_audio)
                else
                    StringUtils.getString(com.luck.picture.lib.R.string.ps_camera_roll)
            } else {
                config.defaultAlbumName
            }
            allFolder.folderName = folderName
            allFolder.firstImagePath = ""
            allFolder.bucketId = PictureConfig.ALL.toLong()
            albumList.add(0, allFolder)
        } else {
            // 2、有相册就找到对应的相册把数据加进去
            allFolder = albumListPopWindow.getFolder(0)
        }
        allFolder.firstImagePath = media.path
        allFolder.firstMimeType = media.mimeType
        val data = ArrayList(mAdapter.data)
        allFolder.data = data
        allFolder.bucketId = PictureConfig.ALL.toLong()
        allFolder.folderTotalNum =
            if (isAddSameImp(allFolder.folderTotalNum)) allFolder.folderTotalNum else allFolder.folderTotalNum + 1
        val currentLocalMediaFolder = SelectedManager.getCurrentLocalMediaFolder()
        if (currentLocalMediaFolder == null || currentLocalMediaFolder.folderTotalNum == 0) {
            SelectedManager.setCurrentLocalMediaFolder(allFolder)
        }
        // 先查找Camera目录，没有找到则创建一个Camera目录
        var cameraFolder: LocalMediaFolder? = null
        for (i in albumList.indices) {
            val exitsFolder = albumList[i]
            if (TextUtils.equals(exitsFolder.folderName, media.parentFolderName)) {
                cameraFolder = exitsFolder
                break
            }
        }
        if (cameraFolder == null) {
            // 还没有这个目录，创建一个
            cameraFolder = LocalMediaFolder()
            albumList.add(cameraFolder)
        }
        cameraFolder.folderName = media.parentFolderName
        if (cameraFolder.bucketId == -1L || cameraFolder.bucketId == 0L) {
            cameraFolder.bucketId = media.bucketId
        }
        // 分页模式下，切换到Camera目录下时，会直接从MediaStore拉取
        if (config.isPageStrategy) {
            cameraFolder.isHasMore = true
        } else {
            // 非分页模式数据都是存在目录的data下，所以直接添加进去就行
            if (!isAddSameImp(allFolder.folderTotalNum)
                || !TextUtils.isEmpty(config.outPutCameraDir)
                || !TextUtils.isEmpty(config.outPutAudioDir)
            ) {
                cameraFolder.data.add(0, media)
            }
        }
        cameraFolder.folderTotalNum =
            if (isAddSameImp(allFolder.folderTotalNum)) cameraFolder.folderTotalNum else cameraFolder.folderTotalNum + 1
        cameraFolder.firstImagePath = config.cameraPath
        cameraFolder.firstMimeType = media.mimeType
        albumListPopWindow.bindAlbumData(albumList)
    }

    /**
     * 数量是否一致
     */
    private fun isAddSameImp(totalNum: Int): Boolean {
        return if (totalNum == 0) {
            false
        } else allFolderSize in 1 until totalNum
    }

    /**
     * 分发处理结果，比如压缩、裁剪、沙盒路径转换
     */
    fun dispatchTransformResult(text: String = "") {
        val selectedResult = SelectedManager.getSelectedResult()
        val result = ArrayList(selectedResult)
        onResultEvent(result, text)
    }

    /**
     * 处理结果
     */
    private fun onResultEvent(result: ArrayList<LocalMedia>, text: String) {
        dismissWithDelayRun(true)
        val editTextStr = if (!StringUtils.isEmpty(text)) text else binding.layoutBottomEdit.editText.text.toString().trim()
//        LiveEventBus.get(RESULT_EVENT_PHOTO)
//            .post(ImageResult(result, editTextStr, binding.layoutBottomEdit.editText.lineCount))
    }

    //    </editor-fold>

    /**
     * @param isDismissDialog 不管执行什么操作  如果为true 那么执行完操作 会关闭弹框  默认为false
     */
    private fun dismissWithDelayRun(isDismissDialog: Boolean = false, block: (() -> Unit)? = null) {
        if (KeyboardUtils.isSoftInputVisible(mActivity)) {
            binding.emotionContainerFrameLayout.gone()
            KeyboardUtils.hideSoftInput(binding.layoutBottomEdit.editText)
            if (isDismissDialog) {
                dismissDelayRun(block)
            }
        } else {
            if (isDismissDialog) {
                dismissDelayRun(block)
            } else {
                // 如果有选中的  二次提示弹框
                if (SelectedManager.getSelectCount() > 0) {
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setTitle("是否关闭dialog")
                    dialog.setMessage("是否放弃选择关闭dialog")
                    dialog.setNegativeButton("否") { d, _ ->
                        d.cancel()
                    }
                    dialog.setPositiveButton("是") { d, _ ->
                        d.dismiss()
                        dismissDelayRun(block)
                    }
                    dialog.show()
                } else {
                    dismissDelayRun(block)
                }
            }
        }
    }

    /**
     * @param block 关闭弹框
     */
    private fun dismissDelayRun(block: (() -> Unit)? = null) {
        SelectedManager.clearDataSource()
        SelectedManager.clearAlbumDataSource()
        SelectedManager.clearSelectResult()
        binding.nestedScrolling.setDoPerformAnyCallbacks(false)
        // 在关闭消失之前 添加动画
        dialog?.window?.setWindowAnimations(R.style.DialogBottomAnim)
        // 关闭之前先把颜色设置透明 在执行关闭
        dialog?.window?.statusBarColor = Color.TRANSPARENT
        binding.nestedScrolling.setBackgroundColor(0)
        mHandler.postDelayed({
            if (block != null) {
                block.invoke()
            } else {
                dismissAllowingStateLoss()
            }
        }, ALERT_LAYOUT_TRANSLATION_DELAY)
    }

    private var translateLeftRight = false

    /**
     * 切换相册、预览
     */
    private fun switchAlbumPreviewAnimation() {
        translateLeftRight = !translateLeftRight
        if (translateLeftRight) {
            val translateAnimationXYLeft = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 1f, TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f
            )
            translateAnimationXYLeft.duration = ALERT_LAYOUT_TRANSLATION //设置动画的过渡时间
            val translateAnimationLeft = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, -1f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f
            )
            translateAnimationLeft.duration = ALERT_LAYOUT_TRANSLATION //设置动画的过渡时间
            binding.recyclerViewPreview.postDelayed({
                binding.recyclerViewPreview.visible()
                binding.recyclerView.startAnimation(translateAnimationLeft)
                binding.recyclerViewPreview.startAnimation(translateAnimationXYLeft)
            }, ALERT_LAYOUT_TRANSLATION_DELAY)
        } else {
            //设置动画，从左向右滑动  布局
            val translateAnimationRight = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 1f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f
            )
            //设置动画，从左向右滑动  布局
            val translateAnimationXRight = TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_SELF, -1f, TranslateAnimation.RELATIVE_TO_SELF, 0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0f, TranslateAnimation.RELATIVE_TO_SELF, 0f
            )
            translateAnimationRight.duration = ALERT_LAYOUT_TRANSLATION //设置动画的过渡时间
            translateAnimationXRight.duration = ALERT_LAYOUT_TRANSLATION //设置动画的过渡时间
            binding.recyclerView.postDelayed({
                binding.recyclerViewPreview.startAnimation(translateAnimationRight)
                binding.recyclerView.startAnimation(translateAnimationXRight)
                binding.recyclerViewPreview.postDelayed(
                    { binding.recyclerViewPreview.invisible() },
                    ALERT_LAYOUT_TRANSLATION
                )
            }, ALERT_LAYOUT_TRANSLATION_DELAY)
        }
        switchAlbumPreviewTitle()
    }

    /**
     * 切换内容的标题
     */
    private fun switchAlbumPreviewTitle() {
        mHandler.postDelayed({
            if (translateLeftRight) {
                binding.tvSelectNum.text = StringUtils.getString(R.string.message_preview)
                binding.tvSelectNum.setTextColor(
                    ContextCompat.getColor(
                        mActivity,
                        R.color.color_323233
                    )
                )
                binding.tvSelectNum.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.icon_nav_back_left,
                    0,
                    0,
                    0
                )
                // 标题
                title = binding.tvTitle.text.toString()
                binding.tvTitle.text = StringUtils.getString(R.string.message_preview)
                binding.ivArrow.gone()
                binding.llTitle.isEnabled = false
            } else {
                binding.tvSelectNum.text = StringUtils.getString(
                    R.string.selected_picture,
                    SelectedManager.getSelectCount()
                )
                binding.tvSelectNum.setTextColor(
                    ContextCompat.getColor(
                        mActivity,
                        R.color.color_323233
                    )
                )
                binding.tvSelectNum.setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.icon_nav_back_black,
                    0
                )
                // 标题
                binding.tvTitle.text = title
                binding.ivArrow.visible()
                binding.llTitle.isEnabled = true
            }
        }, ALERT_LAYOUT_TRANSLATION_DELAY)
    }

    /**
     * 显示数据为空提示
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun showDataNull() {
        if (SelectedManager.getCurrentLocalMediaFolder() == null
            || SelectedManager.getCurrentLocalMediaFolder().bucketId == PictureConfig.ALL.toLong()
        ) {
            binding.emptyLayout.tvEmpty.text = StringUtils.getString(com.luck.picture.lib.R.string.ps_empty)
            binding.emptyLayout.ivEmptyView.setImageResource(com.luck.picture.lib.R.drawable.ps_ic_no_data)
            binding.emptyLayout.root.visible()
            mAdapter.notifyDataSetChanged()
            binding.recyclerView.gone()
        }
    }

    /**
     * 隐藏数据为空提示
     */
    private fun hideDataNull() {
        binding.emptyLayout.root.gone()
        binding.recyclerView.visible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mDragSelectTouchListener != null) {
            mDragSelectTouchListener!!.stopAutoScroll()
        }
    }
}