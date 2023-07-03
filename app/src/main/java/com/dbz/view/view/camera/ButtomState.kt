package com.dbz.view.view.camera

/**
 *  description:
 *
 *  @author  Db_z
 *  @Date    2023/4/24 15:35
 */

const val BUTTON_STATE_ONLY_CAPTURE = 0x101     //只能拍照
const val BUTTON_STATE_ONLY_RECORDER = 0x102    //只能录像
const val BUTTON_STATE_BOTH = 0x103

//闪关灯状态
const val TYPE_FLASH_AUTO = 0x021
const val TYPE_FLASH_ON = 0x022
const val TYPE_FLASH_OFF = 0x023

const val TYPE_PICTURE = 0x001
const val TYPE_VIDEO = 0x002
const val TYPE_SHORT = 0x003
const val TYPE_DEFAULT = 0x004

const val STATE_IDLE = 0x001                //空闲状态
const val STATE_PRESS = 0x002               //按下状态
const val STATE_LONG_PRESS = 0x003          //长按状态
const val STATE_RECORDER_ING = 0x004        //录制状态
const val STATE_BAN = 0x005                 //禁止状态
