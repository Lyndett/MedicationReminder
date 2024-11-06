package com.example.medicamentoreminder

import Medication
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton

class MainScreenActivity : AppCompatActivity() {
    private var selectedMedicationIndex: Int = -1
    private lateinit var medicationRecyclerView: RecyclerView
    private lateinit var medicationAdapter: MedicationAdapter
    private val medications = mutableListOf<Medication>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)
        sharedPreferences = getSharedPreferences("medication_prefs", MODE_PRIVATE)
        loadMedications()
        setupCalendarButton() // Agregar esta línea

        initRecyclerView()
        setupAddButton()
    }

    private fun initRecyclerView() {
        medicationRecyclerView = findViewById(R.id.medicationRecyclerView)
        medicationRecyclerView.layoutManager = GridLayoutManager(this, 2)
        medicationAdapter = MedicationAdapter(medications) { medication, position ->
            selectedMedicationIndex = position
            startEditActivity(medication, position)
        }
        medicationRecyclerView.adapter = medicationAdapter
    }

    private fun setupAddButton() {
        findViewById<MaterialButton>(R.id.addMedicationButton).setOnClickListener {
            startActivityForResult(Intent(this, AddMedicationActivity::class.java), ADD_MEDICATION_REQUEST)
        }
    }

    private fun startEditActivity(medication: Medication, position: Int) {
        val intent = Intent(this, MedicationDetailsActivity::class.java).apply {
            putExtra("medName", medication.name)
            putExtra("quantity", medication.quantity)
            putExtra("interval", medication.interval)
            putExtra("medicationIndex", position)
            putExtra("isEditing", true) // Indica que se está editando
        }
        startActivityForResult(intent, EDIT_MEDICATION_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ADD_MEDICATION_REQUEST -> handleAddMedication(data)
                EDIT_MEDICATION_REQUEST -> handleMedicationResult(data, true) // Cambiado aquí
            }
        }
    }

    private fun handleAddMedication(data: Intent?) {
        data?.let {
            val medName = it.getStringExtra("medName")
            val quantity = it.getStringExtra("quantity")
            val interval = it.getStringExtra("interval")
            val intervalInMinutes = it.getIntExtra("intervalInMinutes", 0)

            if (medName != null && quantity != null && interval != null) {
                // Utiliza el tamaño actual de la lista como índice del nuevo medicamento
                val newMedicationIndex = medications.size
                val newMedication = Medication(medName, quantity, interval, intervalInMinutes, newMedicationIndex)
                medications.add(newMedication)
                medicationAdapter.notifyItemInserted(medications.size - 1)
                saveMedications()

                // Programar alarma
                MedicationUtils.scheduleAlarm(this, newMedication, newMedicationIndex) // Usa el índice como ID
            }
        }
    }

    private fun handleMedicationResult(data: Intent?, isEditing: Boolean) {
        data?.let {
            val medName = it.getStringExtra("medName")
            val quantity = it.getStringExtra("quantity")
            val interval = it.getStringExtra("interval")
            val intervalInMinutes = it.getIntExtra("intervalInMinutes", 0)

            if (medName != null && quantity != null && interval != null) {
                val newMedicationIndex = medications.size // Obtener el índice para el nuevo medicamento
                val alarmIDs = mutableListOf<Int>() // Inicializar la lista de alarmIDs

                if (isEditing) {
                    // Actualizar medicamento existente
                    medications[selectedMedicationIndex] = Medication(medName, quantity, interval, intervalInMinutes, selectedMedicationIndex, alarmIDs)
                    medicationAdapter.notifyItemChanged(selectedMedicationIndex)
                } else {
                    // Agregar nuevo medicamento
                    medications.add(Medication(medName, quantity, interval, intervalInMinutes, newMedicationIndex, alarmIDs))
                    medicationAdapter.notifyItemInserted(medications.size - 1)
                }
                saveMedications()
            }

            // Manejar eliminación
            if (it.getBooleanExtra("delete", false)) {
                // Eliminar medicamento y cancelar alarmas
                MedicationUtils.deleteMedication(this, selectedMedicationIndex, medications)
                medications.removeAt(selectedMedicationIndex)
                medicationAdapter.notifyItemRemoved(selectedMedicationIndex)
                saveMedications()
            }
        }
    }

    private fun loadMedications() {
        medications.clear()
        medications.addAll(MedicationUtils.loadMedications(sharedPreferences))
    }

    private fun saveMedications() {
        MedicationUtils.saveMedications(sharedPreferences, medications)
    }

    companion object {
        const val ADD_MEDICATION_REQUEST = 1
        const val EDIT_MEDICATION_REQUEST = 2
    }

    private fun setupCalendarButton() {
        findViewById<MaterialButton>(R.id.calendarButton).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }
}