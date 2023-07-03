package com.dbz.view.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * description:
 *
 * @author Db_z
 * @Date 2021/10/7 15:40
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {

    /**
     * 运行在UI线程的协程 viewModelScope 已经实现了在onCleared取消协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    /**
     * 在主线程中执行一个协程
     */
    fun launchOnMain(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(Dispatchers.Main) { block() }

}