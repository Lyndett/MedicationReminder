package com.example.medicamentoreminder

import Medication
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditMedicationActivity : AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var intervalEditText: EditText
    private var medicationIndex: Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var medications: MutableList<Medication>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_medication)

        // Inicializar SharedPreferences y cargar medicamentos
        sharedPreferences = getSharedPreferences("medication_prefs", MODE_PRIVATE)
        medications = MedicationUtils.loadMedications(sharedPreferences).toMutableList()

        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backButton)
        nameEditText = findViewById(R.id.medicationNameEditText)
        quantityEditText = findViewById(R.id.quantityEditText)
        intervalEditText = findViewById(R.id.medIntervalInput)

        // Obtener los datos del medicamento desde MedicationDetailsActivity
        medicationIndex = intent.getIntExtra("medicationIndex", -1)
        val medicationName = intent.getStringExtra("medName")
        val medicationQuantity = intent.getStringExtra("quantity")
        val medicationInterval = intent.getStringExtra("interval")

        // Cargar los datos en los EditTexts
        nameEditText.setText(medicationName)
        quantityEditText.setText(medicationQuantity)
        intervalEditText.setText(medicationInterval)

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
            // Actualizar el medicamento en la lista
            medications[medicationIndex] = Medication(
                name = nameEditText.text.toString(),
                quantity = quantityEditText.text.toString(),
                interval = intervalEditText.text.toString(),
                intervalInMinutes = MedicationUtils.parseIntervalToMinutes(intervalEditText.text.toString()),
                medicationIndex = medicationIndex
            )

            // Guardar los cambios en SharedPreferences
            MedicationUtils.saveMedications(sharedPreferences, medications)

            // Devolver los datos actualizados a MedicationDetailsActivity
            val resultIntent = Intent().apply {
                putExtra("medName", nameEditText.text.toString())
                putExtra("quantity", quantityEditText.text.toString())
                putExtra("interval", intervalEditText.text.toString())
                putExtra("medicationIndex", medicationIndex)
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
