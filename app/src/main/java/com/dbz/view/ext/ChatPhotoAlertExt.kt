package com.dbz.view.ext

import android.app.AlertDialog
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import com.dbz.view.R
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.manager.SelectedManager
import com.luck.picture.lib.utils.StyleUtils

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/4/19 14:48
 */

/**
 * 对选择数量进行编号排序
 */
fun notifySelectNumberStyle(tvNumber: AppCompatTextView, item: LocalMedia) {
    tvNumber.text = ""
    for (i in 0 until SelectedManager.getSelectCount()) {
        val media = SelectedManager.getSelectedResult()[i]
        if (TextUtils.equals(media.path, item.path) || media.id == item.id) {
            item.num = media.num
            media.setPosition(item.getPosition())
            tvNumber.text = item.num.toString()
        }
    }
    tvNumber.setBackgroundResource(if (tvNumber.text == "") R.drawable.shape_photo_album_num_bg else R.drawable.shape_photo_album_text_num_select_bg)
    // 如果大于两位数  那么字体缩小 不然显示不下
    tvNumber.textSize = if (item.num > 99) 10f else 13f
}

/**
 * 设置选中缩放动画
 *
 * @param isChecked
 */
fun selectedMedia(view: View, ivPicture: AppCompatImageView, isChecked: Boolean) {
    if (view.isSelected != isChecked) {
        view.isSelected = isChecked
    }
    if (isChecked) {
        val selectColorFilter = StyleUtils.getColorFilter(view.context, R.color.color_80222229)
        ivPicture.colorFilter = selectColorFilter
    } else {
        val defaultColorFilter = StyleUtils.getColorFilter(view.context, R.color.transparent)
        ivPicture.colorFilter = defaultColorFilter
    }
}


/**
 * 检查LocalMedia是否被选中
 *
 * @param currentMedia
 * @return
 */
fun isSelected(currentMedia: LocalMedia): Boolean {
    val selectedResult: List<LocalMedia> = SelectedManager.getSelectedResult()
    val isSelected = selectedResult.contains(currentMedia)
    if (isSelected) {
        val compare = currentMedia.compareLocalMedia
        if (compare != null && compare.isEditorImage) {
            currentMedia.cutPath = compare.cutPath
            currentMedia.isCut = !TextUtils.isEmpty(compare.cutPath)
            currentMedia.isEditorImage = compare.isEditorImage
        }
    }
    return isSelected
}

// 权限请求失败 弹框
fun permissionsDialog(activity: FragmentActivity, permissions: MutableList<String>, tips: String) {
    val dialog = AlertDialog.Builder(activity)
    dialog.setTitle("消息标题")
    dialog.setMessage(tips)
    dialog.setPositiveButton("去设置") { _, _ ->
        XXPermissions.startPermissionActivity(activity, permissions)
    }
    dialog.show()
}