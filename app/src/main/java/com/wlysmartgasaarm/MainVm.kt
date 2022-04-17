package com.wlysmartgasaarm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aliyun.iot20180120.Client
import com.aliyun.iot20180120.models.PubRequest
import com.aliyun.iot20180120.models.QueryDevicePropertyDataRequest
import com.aliyun.iot20180120.models.QueryDevicePropertyDataResponseBody
import com.aliyun.teaopenapi.models.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
class MainVm : ViewModel() {

    private val client: Client by lazy {
        val config = Config()
        config.accessKeyId = "LTAI5tKuSPicsHA6G92yEN7z"
        config.accessKeySecret = "rF59ZWdGpMofl6qPPeU7CbKctlfuC0"
        config.regionId = "cn-shanghai"
        Client(config)
    }

    private var dangerInterface: DangerInterface ?= null

    private val productKey = "a1IaNzikelm"
    private val deviceName = "CAmcMQusTo4qz4HU4vjN"

    private var lastADCValue = -1

    val ADCListLiveData =
        MutableLiveData<List<QueryDevicePropertyDataResponseBody.QueryDevicePropertyDataResponseBodyDataListPropertyInfo>>()
    val ADCValueLiveData = MutableLiveData<Int>()

    val requestToastLiveData = MutableLiveData<String>()

    private val getRequest by lazy {
        QueryDevicePropertyDataRequest()
            .setAsc(0)
            .setIdentifier("ADC")
            .setPageSize(40)
            .setDeviceName(deviceName)
            .setProductKey(productKey)
    }

    private var sendOpenRequest = PubRequest()
        .setProductKey(productKey)
        .setMessageContent(IoStringUtils.convertOrder("CloseDevice"))
        .setTopicFullName("/${productKey}/${deviceName}/user/get")
        .setQos(0)

    private var sendCloseRequest = PubRequest()
        .setProductKey(productKey)
        .setMessageContent(IoStringUtils.convertOrder("OpenDevice"))
        .setTopicFullName("/${productKey}/${deviceName}/user/get")
        .setQos(0)

    init {
        queryRepeatJob()
    }

    private fun queryRepeatJob() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(1500)
                queryDevData()
            }
        }
    }

    fun openDev() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val response = client.pub(sendOpenRequest)
                //调用成功。
                if (response.getBody().getSuccess()) {
                    //获取云端消息ID
                    requestToastLiveData.postValue("燃气阀门已打开")
                }
            }.onFailure {
                requestToastLiveData.postValue("阀门打开失败：${it.message}")
            }
        }
    }

    fun closeDev() {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val response = client.pub(sendCloseRequest)
                //调用成功。
                if (response.getBody().getSuccess()) {
                    //获取云端消息ID
                    requestToastLiveData.postValue("燃气阀门已关闭")
                }
            }.onFailure {
                requestToastLiveData.postValue("阀门关闭失败：${it.message}")
            }
        }
    }

    fun initCallBack(d: DangerInterface){
        dangerInterface = d
    }

    private fun queryDevData() {
        kotlin.runCatching {
            val end = System.currentTimeMillis()
            val start = end - 60 * 1000 * 60 * 24 * 2
            getRequest.apply {
                setEndTime(end)
                setStartTime(start)
            }
            val body = client.queryDevicePropertyData(getRequest).body
            if (body.getSuccess()) {
                val data = body.data.list.propertyInfo
                ADCListLiveData.postValue(data)
                //防止多次报警
                if (data != null && data.isNotEmpty()) {
                    val newValue = data[0].value.toInt()
                    ADCValueLiveData.postValue(newValue)
                    if (newValue >= DANGER_VALUE) {
                        if (newValue != lastADCValue) {
                            closeDev()
                            lastADCValue = newValue
                        }
                        dangerInterface?.danger(newValue)
                    }
                }
            } else {
                requestToastLiveData.postValue("查询失败：${body.errorMessage}")
            }
        }.onFailure {
            requestToastLiveData.postValue("设备数据获取失败：${it.message}")
        }
    }

    companion object {
        const val DANGER_VALUE = 200
    }

}