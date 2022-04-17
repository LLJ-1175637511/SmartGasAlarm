package com.wlysmartgasaarm.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.iot20180120.models.QueryDevicePropertyDataResponseBody
import com.wlysmartgasaarm.R
import com.wlysmartgasaarm.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryRV(val list: List<QueryDevicePropertyDataResponseBody.QueryDevicePropertyDataResponseBodyDataListPropertyInfo>) : RecyclerView.Adapter<HistoryRV.Holder>() {

    class Holder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(item: QueryDevicePropertyDataResponseBody.QueryDevicePropertyDataResponseBodyDataListPropertyInfo){
            binding.tvItemData.text = item.value.toString()
            binding.tvTime.text = item.time.toLong().timeConvert()
        }

        private fun Long.timeConvert():String = SimpleDateFormat("yyyy年MM月dd日").format(Date(this))
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemHistoryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_history,
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount() = list.size

}