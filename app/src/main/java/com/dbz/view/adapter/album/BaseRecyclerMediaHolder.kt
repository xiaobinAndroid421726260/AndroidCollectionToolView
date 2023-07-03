package com.dbz.view.adapter.album

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.dbz.view.R
import com.dbz.view.ext.dp2px
import com.dbz.view.ext.isSelected
import com.dbz.view.ext.notifySelectNumberStyle
import com.dbz.view.ext.selectedMedia
import com.dbz.view.utils.RoundTransformation
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.widget.MediumBoldTextView

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/5/10 15:33
 */
open class BaseRecyclerMediaHolder constructor(itemView: View) :
    BaseViewHolder(itemView) {

    private lateinit var onItemClickListener: (data: LocalMedia, position: Int) -> Unit
    private lateinit var onItemLongClick: (data: LocalMedia, position: Int) -> Unit
    private lateinit var onSelectListener: (view: View, ivPicture: AppCompatImageView, data: LocalMedia, position: Int) -> Unit
    private var mContext: Context = itemView.context


    /**
     * bind Data
     *
     * @param media
     * @param position
     */
    open fun bindData(media: LocalMedia, position: Int) {
        val ivPicture = itemView.findViewById<AppCompatImageView>(R.id.ivPicture)
        val tvCheck = itemView.findViewById<MediumBoldTextView>(R.id.tvNumber)
        val btnCheck = itemView.findViewById<View>(R.id.btnCheck)
        media.position = absoluteAdapterPosition
        selectedMedia(btnCheck, ivPicture, isSelected(media))
        notifySelectNumberStyle(tvCheck, media)
        loadCover(media)
        tvCheck.setOnClickListener { btnCheck.performClick() }
        btnCheck.setOnClickListener { onSelectListener.invoke(btnCheck, ivPicture, media, position) }
        itemView.setOnClickListener {
            onItemClickListener.invoke(media, position)
        }
        itemView.setOnLongClickListener {
            onItemLongClick.invoke(media, position)
            false
        }
    }

    open fun loadCover(media: LocalMedia) {
        val ivPicture = itemView.findViewById<AppCompatImageView>(R.id.ivPicture)
        val path = if (media.isEditorImage) media.cutPath else media.path
        val radius = 5.dp2px().toFloat()
        // 如果路径为空 那么取原始路径
        val localPath = if (!StringUtils.isEmpty(path)) path else media.originalPath
        if (!StringUtils.isEmpty(localPath)) {
            Glide.with(mContext)
                .load(localPath)
                .centerCrop()
                .transform(CenterCrop(), RoundTransformation(radius, radius, radius, radius))
                .error(com.luck.picture.lib.R.drawable.ps_ic_placeholder)
                .placeholder(com.luck.picture.lib.R.drawable.ps_ic_placeholder)
                .into(ivPicture)
        }
    }

    fun setOnItemClickListener(
        onItemLongClick: (data: LocalMedia, position: Int) -> Unit,
        onItemClickListener: (data: LocalMedia, position: Int) -> Unit,
        onSelectListener: (view: View, ivPicture: AppCompatImageView, data: LocalMedia, position: Int) -> Unit,
    ) {
        this.onItemClickListener = onItemClickListener
        this.onItemLongClick = onItemLongClick
        this.onSelectListener = onSelectListener
    }
}