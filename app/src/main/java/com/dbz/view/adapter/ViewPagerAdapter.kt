package com.dbz.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.blankj.utilcode.util.ToastUtils
import com.dbz.view.databinding.ItemViewpagerBinding

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/6/12 13:54
 */
class ViewPagerAdapter : PagerAdapter() {

    private var mContext: Context? = null
    private var mChildCount = 0
    private val data = arrayListOf<String>()

    fun addData(list: List<String>) {
        addData(list, true)
    }

    fun addData(list: List<String>, refresh: Boolean) {
        if (refresh) {
            clear()
        }
        data.addAll(list)
        notifyDataSetChanged()
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    /**
     * 覆盖getItemPosition()方法，当调用notifyDataSetChanged时，
     * 让getItemPosition方法人为的返回POSITION_NONE，
     * 从而达到强迫viewpager重绘所有item的目的。
     */
    override fun notifyDataSetChanged() {
        mChildCount = count
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        if (mChildCount > 0) {
            mChildCount--
            return POSITION_NONE
        }
        return super.getItemPosition(`object`)
    }

    override fun getCount() = data.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mContext == null) {
            mContext = container.context
        }
        val view = ItemViewpagerBinding.inflate(LayoutInflater.from(mContext), container, false)
        view.tvText.text = data[position]
        view.root.setOnClickListener {
            ToastUtils.showShort(data[position])
        }
        container.addView(view.root)
        return view.root
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}