package com.example.medicamentoreminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val medName = intent.getStringExtra("medName")
        val quantity = intent.getStringExtra("quantity")
        val medicationIndex = intent.getIntExtra("medicationIndex", -1)

        // Crea un canal de notificación
        val channelId = "medication_channel"
        createNotificationChannel(context, channelId)

        // Verifica y solicita permisos de notificación
        if (isNotificationPermissionGranted(context)) {
            createNotification(context, medName, quantity, medicationIndex, channelId)
        }
    }

    private fun createNotification(context: Context, medName: String?, quantity: String?, medicationIndex: Int, channelId: String) {
        val uniqueIntent = createNotificationIntent(context, medName, quantity, medicationIndex)

        val pendingIntent = PendingIntent.getActivity(
            context,
            medicationIndex, // Usa medicationIndex para la consistencia
            uniqueIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Crear un uniqueID solo para la notificación
        val uniqueID = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Hora de tomar tu medicamento")
            .setContentText("Toma ${quantity ?: "una cantidad"} de ${medName ?: "tu medicamento"}")
            .setSmallIcon(R.mipmap.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Usar uniqueID para evitar sobrescribir notificaciones
        NotificationManagerCompat.from(context).notify(uniqueID, notificationBuilder.build())
    }

    private fun createNotificationIntent(context: Context, medName: String?, quantity: String?, medicationIndex: Int): Intent {
        return Intent(context, MainScreenActivity::class.java).apply {
            putExtra("medName", medName)
            putExtra("quantity", quantity)
            putExtra("medicationIndex", medicationIndex)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    private fun createNotificationChannel(context: Context, channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Medication Reminders"
            val descriptionText = "Channel for medication reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }
}