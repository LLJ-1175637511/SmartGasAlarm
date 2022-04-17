package com.wlysmartgasaarm.history

import com.aliyun.iot20180120.models.QueryDevicePropertyDataResponseBody
import com.wlysmartgasaarm.R
import com.wlysmartgasaarm.databinding.FragmentHistoryBinding

class HistoryDialog(private val list: List<QueryDevicePropertyDataResponseBody.QueryDevicePropertyDataResponseBodyDataListPropertyInfo>) : BaseDialog<FragmentHistoryBinding>() {

    override fun getLayoutId() = R.layout.fragment_history

    override fun initCreateView() {
        super.initCreateView()
        mDataBinding.recyclerView.adapter = HistoryRV(list)
        mDataBinding.ivQuitRecharge.setOnClickListener {
            destroyDialog()
        }
    }

}