package com.example.medicamentoreminder

import Medication
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MedicationDetailsActivity : AppCompatActivity() {
    private lateinit var backButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private var medicationIndex: Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var medications: MutableList<Medication>

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medication_details)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("medication_prefs", MODE_PRIVATE)
        medications = MedicationUtils.loadMedications(sharedPreferences).toMutableList()

        backButton = findViewById(R.id.backButton)
        editButton = findViewById(R.id.editMedicationButton)
        deleteButton = findViewById(R.id.deleteMedicationButton)

        // Obtener los datos del medicamento pasados desde MainScreenActivity
        val medicationName = intent.getStringExtra("medName")
        val medicationQuantity = intent.getStringExtra("quantity")
        val medicationInterval = intent.getStringExtra("interval")
        medicationIndex = intent.getIntExtra("medicationIndex", -1)
        val medication = getMedicationByIndex(medicationIndex)

        // Comprobar si el medicamento existe
        if (medication != null) {
            // Vincular los TextViews
            val nameTextView: TextView = findViewById(R.id.medicationNameTextView)
            val quantityTextView: TextView = findViewById(R.id.medicationQuantityTextView)
            val intervalTextView: TextView = findViewById(R.id.medicationIntervalTextView)

            // Establecer los textos
            nameTextView.text = getString(R.string.medication_name_format, medicationName ?: getString(R.string.name_not_available))
            quantityTextView.text = getString(R.string.medication_quantity_format, medicationQuantity ?: getString(R.string.quantity_not_available))
            intervalTextView.text = getString(R.string.medication_interval_format, medicationInterval ?: getString(R.string.interval_not_available))
        } else {
            // Mostrar mensaje si no se encuentra el medicamento
            finish() // Finalizar la actividad si no se encuentra el medicamento
        }

        // Lógica para editar el medicamento
        editButton.setOnClickListener {
            val intent = Intent(this, EditMedicationActivity::class.java).apply {
                putExtra("medName", medicationName)
                putExtra("quantity", medicationQuantity)
                putExtra("interval", medicationInterval)
                putExtra("medicationIndex", medicationIndex) // Usa medicationIndex
            }
            startActivityForResult(intent, EDIT_MEDICATION_REQUEST)
        }

        // Lógica para eliminar el medicamento
        deleteButton.setOnClickListener {
            if (medicationIndex != -1) {
                val resultIntent = Intent().apply {
                    putExtra("delete", true) // Indica que se debe eliminar
                    putExtra("medicationIndex", medicationIndex) // Envía el índice del medicamento
                }

                // Utiliza MedicationUtils para eliminar el medicamento
                MedicationUtils.deleteMedication(this, medicationIndex, medications)

                setResult(RESULT_OK, resultIntent)
                finish() // Termina la actividad y regresa a MainScreenActivity
            }
        }

        backButton.setOnClickListener {
            finish() // Regresar a la pantalla principal
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_MEDICATION_REQUEST && resultCode == RESULT_OK) {
            // Recibir datos actualizados de la actividad de edición
            val updatedName = data?.getStringExtra("medName")
            val updatedQuantity = data?.getStringExtra("quantity")
            val updatedInterval = data?.getStringExtra("interval")

            // Actualizar la lista de medicamentos con los nuevos datos
            if (medicationIndex != -1) {
                medications[medicationIndex] = Medication(
                    name = updatedName ?: "",
                    quantity = updatedQuantity ?: "",
                    interval = updatedInterval ?: "",
                    intervalInMinutes = MedicationUtils.parseIntervalToMinutes(updatedInterval ?: "00:00"),
                    medicationIndex = medicationIndex // Este índice es el mismo
                )

                // Devolver los datos editados a MainScreenActivity
                val resultIntent = Intent().apply {
                    putExtra("medName", updatedName)
                    putExtra("quantity", updatedQuantity)
                    putExtra("interval", updatedInterval)
                    putExtra("medicationIndex", medicationIndex) // Usar el índice seleccionado
                }
                setResult(RESULT_OK, resultIntent)
                finish() // Terminar la actividad después de establecer el resultado
            }
        }
    }

    // Función para obtener el medicamento por su índice
    private fun getMedicationByIndex(index: Int): Medication? {
        return if (index >= 0 && index < medications.size) {
            medications[index]
        } else {
            null
        }
    }

    companion object {
        const val EDIT_MEDICATION_REQUEST = 2
    }
}