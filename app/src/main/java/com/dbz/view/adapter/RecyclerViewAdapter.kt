package com.dbz.view.adapter

import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.adapter.base.BaseVBHolder
import com.dbz.view.adapter.base.BaseVBQuickAdapter
import com.dbz.view.databinding.ItemRecyclerviewBinding

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/6/12 13:46
 */
class RecyclerViewAdapter : BaseVBQuickAdapter<String, ItemRecyclerviewBinding>() {

    override fun convert(holder: BaseVBHolder<ItemRecyclerviewBinding>, item: String) {
        holder.viewBinding.tvText.text = item
        holder.itemView.setOnClickListener {
            ToastUtils.showShort(item)
        }
    }
}