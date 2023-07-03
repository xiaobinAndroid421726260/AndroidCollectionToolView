package com.dbz.view.base

import android.app.Application
import com.tencent.mmkv.MMKV
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.ios.IosEmojiProvider

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2022/10/19 17:26
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        EmojiManager.install(IosEmojiProvider())
    }
}