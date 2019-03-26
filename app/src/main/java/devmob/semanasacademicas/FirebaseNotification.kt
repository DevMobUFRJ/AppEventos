package devmob.semanasacademicas

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.Color
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import devmob.semanasacademicas.activities.DetalhesAtividade
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.notificationManager
import java.util.*

const val CHANNEL_ID = "42"

public class FirebaseNotification: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)

        val data = message?.data

        val intent = intentFor<DetalhesAtividade>(
            ARG_WEEK_ID to data?.get("weekId"),
            ARG_ATIVIDADE_ID to data?.get("activityId")
        ).clearTop()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            setupChannel()

        val notificationId = Random().nextInt(500000)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(data?.get("title"))
            .setContentText(data?.get("body"))
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        Log.e("aaaaaa", data.toString())

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    @RequiresApi(api = android.os.Build.VERSION_CODES.O)
    fun setupChannel(){
        val adminChannelName = "APPEVENTOS_NOTIFICATION_CHANNEL"
        val adminChannelDescription = "Channel for appeventos"

        val adminChannel = NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.run {
            description = adminChannelDescription
            lightColor = Color.RED
            enableLights(true)
            enableVibration(true)
        }

        notificationManager.createNotificationChannel(adminChannel)
    }

}