package com.dbz.view.adapter.album

import android.view.View
import android.widget.TextView
import com.dbz.view.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/5/10 16:10
 */
class VideoViewHolder @JvmOverloads constructor(
    val item: View,
) : BaseRecyclerMediaHolder(item) {

    private val tvDuration = itemView.findViewById<TextView>(R.id.tv_duration)

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        tvDuration.text = DateUtils.formatDurationTime(media.duration)
    }
}