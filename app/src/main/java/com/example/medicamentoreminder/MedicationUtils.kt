package com.example.medicamentoreminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.View
import android.widget.Button
import android.widget.TimePicker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

object MedicationUtils {

    fun parseIntervalToMinutes(interval: String): Int {
        if (!interval.matches(Regex("\\d{1,2}:\\d{2}"))) {
            throw IllegalArgumentException("Formato inválido. Usa HH:MM.")
        }
        val (hours, minutes) = interval.split(":").map { it.toInt() }
        return hours * 60 + minutes
    }

    fun scheduleAlarm(
        context: Context,
        medication: Medication,
        alarmID: Int
    ) {
        // Log para depurar
        Log.d("MedicationUtils", "Configurando alarma con alarmID: $alarmID para ${medication.name}")

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = "com.example.ALARM_ACTION"  // Custom action to identify the intent
            putExtra("medName", medication.name)
            putExtra("quantity", medication.quantity)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            alarmID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + (medication.intervalInMinutes * 60 * 1000)
        val intervalMillis = medication.intervalInMinutes * 60 * 1000L

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            intervalMillis,
            pendingIntent
        )

    }

    fun cancelAlarm(context: Context, uniqueID: Int) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = "com.example.ALARM_ACTION"  // Custom action to identify the intent
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            uniqueID,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }


    fun cancelNotification(context: Context, uniqueID: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("cancelnotification", "Cancelando alarma antes de eliminar o editar: $uniqueID")
        notificationManager.cancel(uniqueID)
    }

    fun deleteMedication(context: Context, medicationIndex: Int, medications: List<Medication>) {
        // Obtener el medicamento
        val medication = medications[medicationIndex]

        // Cancelar todas las alarmas

            cancelAlarm(context, medication.uniqueID)
            cancelNotification(context, medication.uniqueID)


        // Eliminar el medicamento de la lista de SharedPreferences
        deleteMedicationFromSharedPreferences(context, medicationIndex)
    }


    fun deleteMedicationFromSharedPreferences(context: Context, medicationIndex: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("medication_prefs", Context.MODE_PRIVATE)
        val medications = loadMedications(sharedPreferences).toMutableList()

        // Verificar si el índice es válido
        if (medicationIndex in medications.indices) {
            // Eliminar el medicamento de la lista
            medications.removeAt(medicationIndex)

            // Guardar la lista actualizada en SharedPreferences
            saveMedications(sharedPreferences, medications)
        }
    }

    fun showTimePickerDialog(
        context: Context,
        initialHour: Int = 0,
        initialMinute: Int = 0,
        onTimeSelected: (String) -> Unit
    ) {
        val dialogView = View.inflate(context, R.layout.dialog_time_picker, null)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirmButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        timePicker.setIs24HourView(true)
        timePicker.hour = initialHour
        timePicker.minute = initialMinute

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.show()

        confirmButton.setOnClickListener {
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute
            val timeString = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(timeString)
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun validateMedicationInput(medName: String, quantity: String, interval: String): Boolean {
        return medName.isNotBlank() && quantity.isNotBlank() && interval.isNotBlank()
    }

    fun loadMedications(sharedPreferences: SharedPreferences): List<Medication> {
        val json = sharedPreferences.getString("medications", null)
        val type = object : TypeToken<List<Medication>>() {}.type
        return Gson().fromJson(json, type) ?: emptyList()
    }

    fun saveMedications(sharedPreferences: SharedPreferences, medications: List<Medication>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(medications)
        editor.putString("medications", json)
        editor.apply()
    }
}
