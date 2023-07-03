package com.dbz.view.adapter.album

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.dbz.view.R
import com.dbz.view.ext.isSelected
import com.dbz.view.ext.notifySelectNumberStyle
import com.dbz.view.ext.selectedMedia
import com.dbz.view.view.camera.CameraViewPreview
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.widget.MediumBoldTextView

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/4/17 13:51
 */
class BasePictureAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val openCameraClick: () -> Unit,
    private val onItemLongClick: (data: LocalMedia, position: Int) -> Unit,
    private val onItemClickListener: (data: LocalMedia, position: Int) -> Unit,
    private val onSelectListener: (view: View, ivPicture: AppCompatImageView, data: LocalMedia, position: Int) -> Unit,
) : RecyclerView.Adapter<BaseRecyclerMediaHolder>() {

    private var isDisplayCamera = true
    var data = arrayListOf<LocalMedia>()
        internal set

    companion object {
        const val NOTIFY_DATA_CHANGE = 100

        /**
         * 拍照
         */
        const val ADAPTER_TYPE_CAMERA = 1

        /**
         * 图片
         */
        const val ADAPTER_TYPE_IMAGE = 2

        /**
         * 视频
         */
        const val ADAPTER_TYPE_VIDEO = 3

        /**
         * 音频
         */
        const val ADAPTER_TYPE_AUDIO = 4
    }

    fun isDisplayCamera(): Boolean {
        return isDisplayCamera
    }

    fun setDisplayCamera(displayCamera: Boolean) {
        isDisplayCamera = displayCamera
    }

    private fun generate(
        parent: ViewGroup,
        viewType: Int,
        resource: Int,
    ): BaseRecyclerMediaHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(resource, parent, false)
        return when (viewType) {
            ADAPTER_TYPE_CAMERA -> CameraViewHolder(itemView)
            ADAPTER_TYPE_VIDEO -> VideoViewHolder(itemView)
            ADAPTER_TYPE_AUDIO -> AudioViewHolder(itemView)
            else -> ImageViewHolder(itemView)
        }
    }

    /**
     * getItemResourceId
     *
     * @param viewType
     * @return
     */
    private fun getItemResourceId(viewType: Int): Int {
        return when (viewType) {
            ADAPTER_TYPE_CAMERA -> R.layout.item_grid_chat_camera
            ADAPTER_TYPE_VIDEO -> R.layout.item_grid_chat_video
            ADAPTER_TYPE_AUDIO -> R.layout.item_grid_chat_audio
            else -> R.layout.item_grid_chat_image
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerMediaHolder {
        return generate(parent, viewType, getItemResourceId(viewType))
    }

    override fun onBindViewHolder(holder: BaseRecyclerMediaHolder, position: Int) {
        if (getItemViewType(position) == ADAPTER_TYPE_CAMERA) {
//            val cameraView = holder.itemView.findViewById<CameraViewPreview>(R.id.cameraView)
//            cameraView.setBindToLifecycle(lifecycleOwner)
            holder.itemView.findViewById<View>(R.id.view_divider).setOnClickListener{
                openCameraClick.invoke()
            }
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            val media: LocalMedia = data[adapterPosition]
            holder.bindData(media, position)
            holder.setOnItemClickListener(onItemLongClick, onItemClickListener, onSelectListener)
        }
    }

    override fun onBindViewHolder(
        holder: BaseRecyclerMediaHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        for (payload in payloads) {
            if (payload is Int && payload == NOTIFY_DATA_CHANGE) {
                if (getItemViewType(position) != ADAPTER_TYPE_CAMERA) {
                    val tvCheck = holder.getView<MediumBoldTextView>(R.id.tvNumber)
                    val btnCheck = holder.getView<View>(R.id.btnCheck)
                    val ivPicture = holder.getView<AppCompatImageView>(R.id.ivPicture)

                    val adapterPosition = if (isDisplayCamera) position - 1 else position
                    val media: LocalMedia = data[adapterPosition]

                    selectedMedia(btnCheck, ivPicture, isSelected(media))
                    notifySelectNumberStyle(tvCheck, media)
                } else {
                    val cameraView = holder.itemView.findViewById<CameraViewPreview>(R.id.cameraView)
                    cameraView.setBindToLifecycle(lifecycleOwner)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isDisplayCamera && position == 0) {
            ADAPTER_TYPE_CAMERA
        } else {
            val adapterPosition = if (isDisplayCamera) position - 1 else position
            val mimeType = this.data[adapterPosition].mimeType
            if (PictureMimeType.isHasVideo(mimeType)) {
                return ADAPTER_TYPE_VIDEO
            } else if (PictureMimeType.isHasAudio(mimeType)) {
                return ADAPTER_TYPE_AUDIO
            }
            ADAPTER_TYPE_IMAGE
        }
    }

    fun getDefItemCountDataSize() = itemCount

    override fun getItemCount() = if (isDisplayCamera) data.size + 1 else data.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: Collection<LocalMedia>?) {
        if (list !== this.data) {
            this.data.clear()
            if (!list.isNullOrEmpty()) {
                this.data.addAll(list)
            }
        } else {
            if (list.isNotEmpty()) {
                val newList = ArrayList(list)
                this.data.clear()
                this.data.addAll(newList)
            } else {
                this.data.clear()
            }
        }
        notifyDataSetChanged()
    }
}