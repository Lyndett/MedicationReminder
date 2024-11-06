package com.example.medicamentoreminder

import Medication
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AddMedicationActivity : AppCompatActivity() {

    private lateinit var medNameInput: EditText
    private lateinit var medQuantityInput: EditText
    private lateinit var medIntervalInput: EditText
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var medications: MutableList<Medication>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medication)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)

        medNameInput = findViewById(R.id.medNameInput)
        medQuantityInput = findViewById(R.id.medQuantityInput)
        medIntervalInput = findViewById(R.id.medIntervalInput)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)

        // Cargar medicamentos existentes desde SharedPreferences usando MedicationUtils
        medications = MedicationUtils.loadMedications(getSharedPreferences("medication_prefs", MODE_PRIVATE)).toMutableList()

        medIntervalInput.setOnClickListener {
            MedicationUtils.showTimePickerDialog(this) { timeString ->
                medIntervalInput.setText(timeString)
            }
        }

        saveButton.setOnClickListener {
            addMedication()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun addMedication() {
        val medName = medNameInput.text.toString()
        val quantity = medQuantityInput.text.toString()
        val intervalStr = medIntervalInput.text.toString()

        if (!MedicationUtils.validateMedicationInput(medName, quantity, intervalStr)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val intervalInMinutes = MedicationUtils.parseIntervalToMinutes(intervalStr)

        // Generar el índice de medicamento (aquí simplemente será el tamaño actual de la lista)
        val medicationIndex = medications.size

        // Crear nuevo medicamento con el medicationIndex
        val newMedication = Medication(
            name = medName,
            quantity = quantity,
            interval = intervalStr,
            intervalInMinutes = intervalInMinutes,
            medicationIndex = medicationIndex // Utiliza medicationIndex en lugar de uniqueID
        )

        // Agregar el nuevo medicamento a la lista y guardar en SharedPreferences usando MedicationUtils
        medications.add(newMedication)
        MedicationUtils.saveMedications(getSharedPreferences("medication_prefs", MODE_PRIVATE), medications)

        // Programar la alarma usando el nuevo medicamento
        newMedication.alarmIDs.forEach { alarmID ->
            MedicationUtils.scheduleAlarm(this, newMedication, alarmID)
        }

        val resultIntent = Intent().apply {
            putExtra("medName", medName)
            putExtra("quantity", quantity)
            putExtra("interval", intervalStr)
            putExtra("intervalInMinutes", intervalInMinutes)
            putExtra("medicationIndex", medicationIndex) // Enviar el medicationIndex
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}