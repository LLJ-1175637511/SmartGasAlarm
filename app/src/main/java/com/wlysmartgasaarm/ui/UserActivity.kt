package com.wlysmartgasaarm.ui

import com.llj.baselib.ui.IOTBaseActivity
import com.wlysmartgasaarm.R
import com.wlysmartgasaarm.databinding.ActivityUserBinding

class UserActivity:IOTBaseActivity<ActivityUserBinding>() {

    override fun getLayoutId() = R.layout.activity_user

    override fun init() {
        super.init()
        mDataBinding.tvQuit.setOnClickListener {
            startActivityAndFinish<LoginActivity>()
        }
    }

}