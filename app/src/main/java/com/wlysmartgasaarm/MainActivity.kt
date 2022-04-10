package com.wlysmartgasaarm

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aliyun.alink.dm.api.DeviceInfo
import com.aliyun.alink.dm.api.IoTApiClientConfig
import com.aliyun.alink.linkkit.api.ILinkKitConnectListener
import com.aliyun.alink.linkkit.api.LinkKit
import com.aliyun.alink.linkkit.api.LinkKitInitParams
import com.aliyun.alink.linksdk.channel.core.persistent.mqtt.request.MqttRrpcRequest
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest
import com.aliyun.alink.linksdk.cmp.core.base.AMessage
import com.aliyun.alink.linksdk.cmp.core.base.ARequest
import com.aliyun.alink.linksdk.cmp.core.base.AResponse
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper
import com.aliyun.alink.linksdk.tools.AError
import java.io.UnsupportedEncodingException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        LinkKit.getInstance().registerOnPushListener(notifyListener)
        // 通过identifier获取物模型对应属性的值
        findViewById<Button>(R.id.button).setOnClickListener {
            LinkKit.getInstance().getDeviceCOTA().setCOTAChangeListener(iConnectRrpcListener);
//            val identifier = "ADC"
//            println(LinkKit.getInstance().getDeviceThing().getPropertyValue(identifier))
        }
//

        findViewById<Button>(R.id.button2).setOnClickListener{
                    LinkKit.getInstance().registerOnPushListener(notifyListener)
        }
//// 获取所有属性
//        LinkKit.getInstance().getDeviceThing().getProperties()
    }

    override fun onDestroy() {
        super.onDestroy()
        LinkKit.getInstance().unRegisterOnPushListener(notifyListener);
    }

    private val iConnectRrpcListener = object : IConnectRrpcListener {

        override fun onSubscribeSuccess(aRequest: ARequest?) {
            Log.d(
                "notifyListener",
                "onSubscribeSuccess() called with: aRequest = [" + aRequest + "]"
            )
            Toast.makeText(this@MainActivity, "订阅成功", Toast.LENGTH_SHORT).show()
        }

        override fun onSubscribeFailed(aRequest: ARequest?, aError: AError?) {
            Log.d(
                "notifyListener",
                "onSubscribeFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]"
            );
            Toast.makeText(this@MainActivity, "订阅失败", Toast.LENGTH_SHORT).show()
        }

        override fun onReceived(aRequest: ARequest?, iConnectRrpcHandle: IConnectRrpcHandle?) {
            Log.d(
                "notifyListener",
                "onReceived() called with: aRequest = [" + aRequest + "], iConnectRrpcHandle = [" + iConnectRrpcHandle + "]"
            );
            Log.d("notifyListener", "接收到下行数据");
            if (aRequest is MqttRrpcRequest) {
                // 云端下行数据 拿到
                try {
                    val data = String(((aRequest as MqttRrpcRequest).payloadObj) as ByteArray)
                    Log.d("notifyListener", "payloadObj=" + data);
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace();
                }
            }

            // 如果不一定是json格式，可以参考如下方式回复
            val rrpcResponse = MqttPublishRequest()
            if (aRequest is MqttRrpcRequest) {
                rrpcResponse.topic = (aRequest as MqttRrpcRequest).topic;
            }
            rrpcResponse.payloadObj = "{\"id\":\"123\", \"code\":\"200\"" + ",\"data\":{} }";

            LinkKit.getInstance().publish(rrpcResponse, object : IConnectSendListener {

                override fun onResponse(aRequest: ARequest?, aResponse: AResponse?) {
                    Log.d(
                        "notifyListener",
                        "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + aResponse + "]"
                    );

                }

                override fun onFailure(aRequest: ARequest?, aError: AError?) {
                    Log.d(
                        "notifyListener",
                        "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]"
                    );
                }
            });
        }

        override fun onResponseSuccess(aRequest: ARequest?) {
            Log.d(
                "notifyListener",
                "onResponseSuccess() called with: aRequest = [" + aRequest + "]"
            );
        }

        override fun onResponseFailed(aRequest: ARequest?, aError: AError?) {
            Log.d(
                "notifyListener",
                "onResponseFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]"
            );
        }
    }

    private val notifyListener = object : IConnectNotifyListener {
        override fun onNotify(connectId: String?, topic: String?, aMessage: AMessage?) {
            val pushData = String(aMessage?.data as ByteArray)
            Log.d("notifyListener", "connectId:${connectId}")
            Log.d("notifyListener", "topic:${topic}")
            Log.d("notifyListener", "aMessage:${pushData}")

            // 下行数据回调
            // connectId连接类型Topic下行Topic; aMessage下行数据
            // 数据解析如下：

            // pushData 示例  {"method":"thing.service.test_service","id":"123374967","params":{"vv":60},"version":"1.0.0"}
            // method 服务类型； params 下推数据内容
        }

        override fun shouldHandle(p0: String?, p1: String?): Boolean {
            // 选择是否不处理某个Topic的下行数据
            // 如果不处理某个Topic，则onNotify不会收到对应Topic的下行数据
            return true; //TODO 根据实际情况设置
        }

        override fun onConnectStateChange(p0: String?, p1: ConnectState?) {
            // 对应连接类型的连接状态变化回调，具体连接状态参考SDK ConnectState
        }

    }

}