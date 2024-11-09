package com.example.medicamentoreminder

import Medication
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class EditMedicationActivity : AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var intervalEditText: EditText
    private lateinit var durationEditText: EditText
    private var medicationIndex: Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var medications: MutableList<Medication>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_medication)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)
        // Inicializar SharedPreferences y cargar medicamentos
        sharedPreferences = getSharedPreferences("medication_prefs", MODE_PRIVATE)
        medications = MedicationUtils.loadMedications(sharedPreferences).toMutableList()

        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        nameEditText = findViewById(R.id.medicationNameEditText)
        quantityEditText = findViewById(R.id.quantityEditText)
        intervalEditText = findViewById(R.id.medIntervalInput)
        durationEditText = findViewById(R.id.medDurationInput)

        // Obtener los datos del medicamento desde MedicationDetailsActivity
        medicationIndex = intent.getIntExtra("medicationIndex", -1)
        val medicationName = intent.getStringExtra("medName")
        val medicationQuantity = intent.getStringExtra("quantity")
        val medicationInterval = intent.getStringExtra("interval")
        val uniqueID = intent.getIntExtra("uniqueID", -1)
        val daysDuration = intent.getIntExtra("daysDuration", -1)
        // Cargar los datos en los EditTexts
        nameEditText.setText(medicationName)
        quantityEditText.setText(medicationQuantity)
        intervalEditText.setText(medicationInterval)
        durationEditText.setText(daysDuration.toString())

        // Configurar el botón de guardar
        saveButton.setOnClickListener {
            saveUpdatedMedication()
        }

        // Configurar el botón de regresar
        backButton.setOnClickListener {
            finish() // Regresa sin guardar cambios
        }

        // Configurar el TimePickerDialog al hacer clic en el campo de intervalo
        intervalEditText.setOnClickListener {
            showCustomTimePickerDialog()
        }
    }
    private fun saveUpdatedMedication() {
        if (medicationIndex != -1) {
            // Primero, obtenemos el medicamento antiguo
            val oldMedication = medications[medicationIndex]
            MedicationUtils.cancelAlarm(this, oldMedication.uniqueID)
            MedicationUtils.cancelNotification(this, oldMedication.uniqueID)
            MedicationUtils.deleteMedication(this, medicationIndex, medications)


            // Ahora, actualizamos el medicamento con los nuevos valores
            val updatedMedication = Medication(
                name = nameEditText.text.toString(),
                quantity = quantityEditText.text.toString(),
                interval = intervalEditText.text.toString(),
                intervalInMinutes = MedicationUtils.parseIntervalToMinutes(intervalEditText.text.toString()),
                medicationIndex = medicationIndex,
                uniqueID = oldMedication.uniqueID,
                daysDuration = durationEditText.text.toString().toInt()
            )

            // Actualizamos el medicamento en la lista
            medications[medicationIndex] = updatedMedication

            // Guardamos los cambios en SharedPreferences
            MedicationUtils.saveMedications(sharedPreferences, medications)

            // Establecemos la nueva alarma para el medicamento editado
            MedicationUtils.scheduleAlarm(this, updatedMedication, updatedMedication.uniqueID)

            // Devolvemos los datos actualizados a MedicationDetailsActivity
            val resultIntent = Intent(applicationContext, AlarmReceiver::class.java).apply {
                action = "com.example.ALARM_ACTION"
                putExtra("medName", updatedMedication.name)
                putExtra("quantity", updatedMedication.quantity)
                putExtra("interval", updatedMedication.interval)
                putExtra("medicationIndex", medicationIndex)
                putExtra("uniqueID", updatedMedication.uniqueID)
                putExtra("daysDuration", updatedMedication.daysDuration)
            }
            setResult(RESULT_OK, resultIntent)
            finish() // Terminar la actividad después de guardar
        }
    }

    private fun showCustomTimePickerDialog() {
        MedicationUtils.showTimePickerDialog(this, onTimeSelected = { time ->
            intervalEditText.setText(time)
        })
    }
}
