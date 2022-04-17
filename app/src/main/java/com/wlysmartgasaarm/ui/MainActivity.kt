package com.wlysmartgasaarm.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.leaf.library.StatusBarUtil
import com.llj.baselib.IOTLib
import com.llj.baselib.save
import com.llj.baselib.ui.IOTBaseActivity
import com.wlysmartgasaarm.DangerInterface
import com.wlysmartgasaarm.MainVm
import com.wlysmartgasaarm.R
import com.wlysmartgasaarm.databinding.ActivityMainBinding
import com.wlysmartgasaarm.history.HistoryDialog

@RequiresApi(api = Build.VERSION_CODES.O)
class MainActivity : IOTBaseActivity<ActivityMainBinding>(), DangerInterface {

    private val vm by viewModels<MainVm>()

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        StatusBarUtil.setTransparentForWindow(this)
        super.init()

        vm.initCallBack(this)

        vm.ADCListLiveData.observe(this) {
            if (it == null || it.isEmpty()) return@observe
            mDataBinding.tvADC.text = it[it.lastIndex].value.toString()
        }
        mDataBinding.btOpen.setOnClickListener {
            vm.openDev()
        }
        mDataBinding.btClose.setOnClickListener {
            vm.closeDev()
        }
        vm.requestToastLiveData.observe(this) {
            if (it == null) return@observe
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        vm.ADCValueLiveData.observe(this) {
            val v = it ?: return@observe
            if (v >= MainVm.DANGER_VALUE) {
                mDataBinding.tvADC.setTextColor(getColor(R.color.red))
            } else {
                mDataBinding.tvADC.setTextColor(getColor(R.color.grey))
            }
            mDataBinding.tvADC.text = "${v} ppm"
        }
        mDataBinding.clUser.setOnClickListener {
            startCommonActivity<UserActivity>()
        }
        mDataBinding.tvHistory.setOnClickListener {
            val list = vm.ADCListLiveData.value ?: emptyList()
            showDialog(HistoryDialog(list), "history")
        }
    }

    private fun showDialog(df: DialogFragment, tag: String) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prev: Fragment? = supportFragmentManager.findFragmentByTag(tag)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        df.show(ft, tag)
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun danger(v: Int) {
        runOnUiThread {
            buildNotification("燃气浓度已超过安全值（${v}） 请尽快处理")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SmartGasAlarmNotification"
            val descriptionText = "燃气异常警报"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(t: String) {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 10, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.fires)
            .setContentTitle("燃气报警")
            .setContentText(t)
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        NotificationManagerCompat.from(this).notify(100, builder.build())
    }

    companion object {
        const val CHANNEL_ID = "686421"
    }
}