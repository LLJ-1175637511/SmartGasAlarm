package com.wlysmartgasaarm

import android.app.Application
import com.llj.baselib.IOTLib
import com.llj.baselib.bean.UserConfigBean

class MyApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        IOTLib.init(applicationContext, UserConfigBean())
    }

}