package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.lock_screen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.MainActivity

const val JOB_ID = 1001

class LockScreenService: JobIntentService() {
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null) {
                when(intent.action) {
                    Intent.ACTION_SCREEN_OFF -> {
                        val newIntent = Intent(context, LockScreenActivity::class.java)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        val newIntent = Intent(context, LockScreenActivity::class.java)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    }
                }
            }
        }
    }

    private final val ALARM_ID = "com.bluewhale.lockscreentest.lockscreen"

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun enqueueWork(context: Context, work: Intent) {
        enqueueWork(context, LockScreenService::class.java, JOB_ID, work)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHandleWork(intent: Intent) {
        val filter = IntentFilter()
        // 이거네!!.
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        println("PPPPPPPPP")
        registerReceiver(receiver, filter)

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            ALARM_ID,
            "잠금화면 테스트",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        nm.createNotificationChannel(channel)

        val pending = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val notification = Notification.Builder(this, ALARM_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("잠금화면 서비스")
            .setContentText("잠금화면 서비스 동작중")
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        startForeground(1, notification)
    }
}