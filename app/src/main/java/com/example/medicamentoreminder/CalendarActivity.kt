package com.example.medicamentoreminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

        private lateinit var calendarGridView: GridView
        private lateinit var monthTextView: TextView
        private lateinit var backButton: Button
        private lateinit var prevMonthButton: Button
        private lateinit var nextMonthButton: Button
        private lateinit var calendar: Calendar
        private lateinit var sharedPreferences: SharedPreferences
        private val events = mutableListOf<Event>()
        private val gson = Gson()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_calendar)
            window.statusBarColor = ContextCompat.getColor(this, R.color.purple_500)
            sharedPreferences = getSharedPreferences("EventPrefs", MODE_PRIVATE)

            // Agregar un evento de prueba
            events.add(Event("05-11-2024", "Evento de prueba", "Nota del evento de prueba"))
            saveEvents() // Guardarlo
            loadEvents() // Luego, intentar cargarlo

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
            loadEvents()
            // Obtener los días en el mes actual
            val daysInMonth = getDaysInMonth(calendar)

            // Crear y configurar el adaptador
            Log.d("EventInfo", "Lista de eventos antes de pasar al adaptador: $events")
            val adapter = CalendarAdapter(this, daysInMonth, calendar, events)
            calendarGridView.adapter = adapter
            adapter.notifyDataSetChanged()

            // Configurar un listener para cuando un día se selecciona
            calendarGridView.setOnItemClickListener { _, _, position, _ ->
                val selectedDay = daysInMonth[position]
                if (selectedDay.isNotEmpty()) {
                    val eventTitles = ArrayList<String>()
                    val eventNotes = ArrayList<String>()

                    // Filtrar eventos por el día seleccionado
                    for (event in events) {
                        if (event.date == selectedDay) {
                            eventTitles.add(event.title)
                            eventNotes.add(event.note)
                        }
                    }

                    // Pasar los detalles del día seleccionado y los eventos al DayDetailsActivity
                    openDayDetails(selectedDay)
                }
            }
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

        fun openDayDetails(selectedDay: String) {
            val intent = Intent(this, DayDetailsActivity::class.java)
            intent.putExtra("SELECTED_DAY", selectedDay)
            intent.putExtra("MONTH_YEAR", SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time))

            startActivityForResult(intent, 1001) // Usa un requestCode
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                loadEvents() // Recarga los eventos para obtener los cambios de DayDetailsActivity
                updateCalendar() // Actualiza la vista del calendario
            }
        }


        private fun loadEvents() {
            events.clear()
            events.addAll(EventUtils.loadEvents(sharedPreferences))
        }

        private fun saveEvents() {
           EventUtils.saveEvents(sharedPreferences, events)
        }
    }
