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
        val uniqueID = intent.getIntExtra("uniqueID", -1)

        // Crea un canal de notificación
        val channelId = "medication_channel"
        createNotificationChannel(context, channelId)

        // Verifica y solicita permisos de notificación
        if (isNotificationPermissionGranted(context)) {
            createNotification(context, medName, quantity, uniqueID, channelId)
        }
        val action = intent.action

        if (action == "com.example.ALARM_ACTION_DELETE") {
            val medicationIndex = intent.getIntExtra("medicationIndex", -1)

            // Usar el contexto de la aplicación para acceder a SharedPreferences
            val sharedPreferences =
                context.getSharedPreferences("medications", Context.MODE_PRIVATE)

            // Recuperar la lista de medicamentos desde SharedPreferences
            val medications = MedicationUtils.getMedicationsFromSharedPreferences(sharedPreferences as Context)

            if (medicationIndex != -1 && medicationIndex < medications.size) {
                // Llamar a MedicationUtils.deleteMedication para eliminar el medicamento
                MedicationUtils.deleteMedication(context, medicationIndex, medications)
            }
        }
    }

    private fun createNotification(context: Context, medName: String?, quantity: String?, uniqueID: Int, channelId: String) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = "com.example.ALARM_ACTION"
            putExtra("medName", medName)
            putExtra("quantity", quantity)
        }

        val pendingIntent = PendingIntent.getActivity(
            context.applicationContext,
            uniqueID,
            intent,
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