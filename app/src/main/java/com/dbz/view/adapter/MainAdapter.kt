package com.dbz.view.adapter

import com.dbz.view.bean.MainBean
import com.dbz.view.adapter.base.BaseVBHolder
import com.dbz.view.adapter.base.BaseVBQuickAdapter
import com.dbz.view.databinding.ItemMainCustomViewBinding
import com.dbz.view.ext.logE


/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/6/12 11:30
 */
class MainAdapter(val itemClickListener: (position: Int) -> Unit) : BaseVBQuickAdapter<MainBean, ItemMainCustomViewBinding>() {

    override fun convert(holder: BaseVBHolder<ItemMainCustomViewBinding>, item: MainBean) {
        holder.viewBinding.button.text = item.text
        holder.viewBinding.button.setOnClickListener {
            itemClickListener.invoke(holder.layoutPosition)
        }
    }
}