package com.wlysmartgasaarm

import android.app.Application
import android.util.Log
import com.aliyun.alink.dm.api.DeviceInfo
import com.aliyun.alink.dm.api.IoTApiClientConfig
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener
import com.aliyun.alink.linkkit.api.IoTMqttClientConfig
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linkkit.api.LinkKitInitParams
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper
import com.aliyun.alink.linksdk.tools.AError

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        /**
         * 设置设备认证信息
         */
        /**
         * 设置设备认证信息
         */
        val deviceInfo = DeviceInfo()
        val productKey = "a1IaNzikelm"
        val deviceName = "CAmcMQusTo4qz4HU4vjN"
        val deviceSecret = "bd003567d4a664dfe97498aefb204afd"
        deviceInfo.productKey = productKey // 产品类型

        deviceInfo.deviceName = deviceName // 设备名称

        deviceInfo.deviceSecret = deviceSecret // 设备密钥

//如果使用deviceToken和clientID连接物联网平台，那么设备的deviceSecret需要设置为null
//一型一密免白名单动态注册之后建联需要设置deviceToken、clientId这两个值，这两个值由deviceDynamicRegister接口返回
//如果使用deviceToken和clientID连接物联网平台，那么设备的deviceSecret需要设置为null
//一型一密免白名单动态注册之后建联需要设置deviceToken、clientId这两个值，这两个值由deviceDynamicRegister接口返回
//        deviceToken = deviceToken
//        clientId = clientId
        /**
         * 设置设备当前的初始状态值，属性需要和物联网平台创建的物模型属性一致
         * 若此处为空，物模型属性的初始值则为空。
         * 调用物模型上报接口之后，物模型会有相关数据缓存。
         */
        /**
         * 设置设备当前的初始状态值，属性需要和物联网平台创建的物模型属性一致
         * 若此处为空，物模型属性的初始值则为空。
         * 调用物模型上报接口之后，物模型会有相关数据缓存。
         */
        val propertyValues: MutableMap<String, ValueWrapper<*>> = HashMap()
// 示例
//        propertyValues.put("ADC", ValueWrapper.IntValueWrapper(20))
        val clientConfig = IoTMqttClientConfig(productKey, deviceName, deviceSecret)
        val params = LinkKitInitParams()
        params.deviceInfo = deviceInfo
        params.propertyValues = propertyValues
        params.connectConfig = IoTApiClientConfig()
        params.mqttClientConfig = clientConfig
        /**
         * 设备初始化建联
         * onError 初始化建联失败，需要用户重试初始化。如因网络问题导致初始化失败。
         * onInitDone 初始化成功
         */
        /**
         * 设备初始化建联
         * onError 初始化建联失败，需要用户重试初始化。如因网络问题导致初始化失败。
         * onInitDone 初始化成功
         */
        LinkKit.getInstance().init(applicationContext, params, object : ILinkKitConnectListener {
            override fun onError(error: AError?) {
                // 初始化失败 error包含初始化错误信息
                Log.d("notifyListener", "code:${error?.code} err:${error?.msg}")
            }

            override fun onInitDone(data: Any) {
                // 初始化成功 data 作为预留参数
                Log.d("notifyListener", "init suc")

            }
        })
    }
}