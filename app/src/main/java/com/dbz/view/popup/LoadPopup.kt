package com.dbz.view.popup

import android.content.Context
import android.view.Gravity
import com.dbz.view.R

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/26 17:03
 */
class LoadPopup(context: Context) : BasePopup(context) {

    init {
        setContentView(R.layout.popup_loading)
        popupGravity = Gravity.CENTER
        setOutSideDismiss(false)
        isOutSideTouchable = false
        showPopupWindow()
    }
}