package com.example.medicamentoreminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarGridView: GridView
    private lateinit var monthTextView: TextView
    private lateinit var backButton: Button
    private lateinit var prevMonthButton: Button
    private lateinit var nextMonthButton: Button
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)

        // Inicializar las vistas
        calendarGridView = findViewById(R.id.calendarGridView)
        monthTextView = findViewById(R.id.monthTextView)
        backButton = findViewById(R.id.backButton)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)

        // Configurar el listener del botón de regreso
        backButton.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Inicializa el calendario con el mes actual
        calendar = Calendar.getInstance()
        updateCalendar()

        // Configurar los listeners para los botones de cambio de mes
        prevMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonthButton.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    private fun updateCalendar() {
        // Actualizar el título del mes
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTextView.text = monthFormat.format(calendar.time)

        // Obtener los días en el mes actual
        val daysInMonth = getDaysInMonth(calendar)

        // Crear y configurar el adaptador
        val adapter = CalendarAdapter(this, daysInMonth, calendar)
        calendarGridView.adapter = adapter
    }

    private fun getDaysInMonth(calendar: Calendar): List<String> {
        val days = mutableListOf<String>()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        // Obtener el número de días en el mes actual
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Rellenar días vacíos al principio
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        for (i in 1 until firstDayOfWeek) {
            days.add("") // Agregar espacios vacíos para los días anteriores al primer día del mes
        }

        // Agregar los días del mes a la lista
        for (day in 1..daysInMonth) {
            days.add(day.toString())
        }

        return days
    }
}
