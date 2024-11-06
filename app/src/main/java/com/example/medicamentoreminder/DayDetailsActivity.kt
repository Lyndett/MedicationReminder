package com.example.medicamentoreminder

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DayDetailsActivity : AppCompatActivity() {

    private lateinit var dayTextView: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_details) // Asegúrate de que el layout exista

        dayTextView = findViewById(R.id.dayTextView)
        backButton = findViewById(R.id.backButton)

        // Obtener el día seleccionado y el mes/año desde el intent
        val selectedDay = intent.getStringExtra("SELECTED_DAY") ?: "No day selected"
        val monthYear = intent.getStringExtra("MONTH_YEAR") ?: ""

        // Mostrar el día y mes/año en el TextView
        dayTextView.text = "$selectedDay de $monthYear"

        // Configurar el botón de regresar
        backButton.setOnClickListener {
            finish() // Cierra la actividad y regresa a la anterior
        }
    }
}
