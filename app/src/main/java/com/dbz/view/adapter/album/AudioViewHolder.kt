package com.dbz.view.adapter.album

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.dbz.view.R
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.DateUtils

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/5/10 16:12
 */
class AudioViewHolder @JvmOverloads constructor(
    val item: View,
) : BaseRecyclerMediaHolder(item) {

    private val ivPicture = itemView.findViewById<AppCompatImageView>(R.id.ivPicture)
    private val tvDuration = itemView.findViewById<AppCompatTextView>(R.id.tv_duration)

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        tvDuration.text = DateUtils.formatDurationTime(media.duration)
    }

    override fun loadCover(media: LocalMedia) {
        ivPicture.setImageResource(com.luck.picture.lib.R.drawable.ps_audio_placeholder)
    }
}