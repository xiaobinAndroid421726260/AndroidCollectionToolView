package com.dbz.view.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.blankj.utilcode.util.StringUtils
import com.dbz.view.R
import com.dbz.view.base.BaseActivity
import com.dbz.view.databinding.ActivityChatBinding
import com.dbz.view.dialog.ChatAttachPhotoAlert
import com.dbz.view.ext.permissionsDialog
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.config.PictureConfig

class ChatActivity : BaseActivity() {

    private val binding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private var chatAttachPhotoAlert: ChatAttachPhotoAlert? = null

    override fun getContentView() = binding.root

    override fun initView(bundle: Bundle?) {
        initImmersionBar(R.color.blue_color)
        binding.toolbar.title = "Android Telegram 媒体"
        binding.toolbar.setTitleTextColor(Color.WHITE)
        binding.toolbar.setNavigationIcon(R.drawable.onback_white)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btn.setOnClickListener {
            if (XXPermissions.isGranted(this,
                    Permission.CAMERA,
                    Permission.READ_MEDIA_IMAGES,
                    Permission.READ_MEDIA_VIDEO,
                    Permission.READ_MEDIA_AUDIO,
                    Permission.WRITE_EXTERNAL_STORAGE)) {

                chatAttachPhotoAlert = ChatAttachPhotoAlert()
                supportFragmentManager
                    .beginTransaction()
                    .add(chatAttachPhotoAlert!!, "ChatAttachPhotoAlert")
                    .commitAllowingStateLoss()

            } else {
                // 申请多个权限
                XXPermissions.with(this).permission(
                        Permission.CAMERA,
                        Permission.READ_MEDIA_IMAGES,
                        Permission.READ_MEDIA_VIDEO,
                        Permission.READ_MEDIA_AUDIO,
                        Permission.WRITE_EXTERNAL_STORAGE
                    )
                    .request(object : OnPermissionCallback {
                        override fun onGranted(
                            permissions: MutableList<String>,
                            allGranted: Boolean,
                        ) {
                            if (!allGranted) {
                                val tips = if (permissions.contains(Permission.CAMERA)) {
                                        StringUtils.getString(R.string.photos_media_missing_storage_permissions)
                                    } else {
                                        StringUtils.getString(R.string.camera_missing_storage_permissions)
                                    }
                                permissionsDialog(
                                    this@ChatActivity,
                                    permissions,
                                    tips
                                )
                                return
                            }
                            chatAttachPhotoAlert = ChatAttachPhotoAlert()
                            supportFragmentManager
                                .beginTransaction()
                                .add(chatAttachPhotoAlert!!, "ChatAttachPhotoAlert")
                                .commitAllowingStateLoss()
                        }

                        override fun onDenied(
                            permissions: MutableList<String>,
                            doNotAskAgain: Boolean,
                        ) {
                            if (doNotAskAgain) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(
                                    this@ChatActivity,
                                    permissions
                                )
                            } else {
                                val tips = if (permissions.contains(Permission.CAMERA)) {
                                        StringUtils.getString(R.string.photos_media_missing_storage_permissions)
                                    } else {
                                        StringUtils.getString(R.string.camera_missing_storage_permissions)
                                    }
                                permissionsDialog(
                                    this@ChatActivity,
                                    permissions,
                                    tips
                                )
                            }
                        }
                    })
            }
        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PictureConfig.REQUEST_CAMERA){
            mHandler.postDelayed({ chatAttachPhotoAlert?.dispatchHandleCamera(data) }, 50)
        }
    }
}