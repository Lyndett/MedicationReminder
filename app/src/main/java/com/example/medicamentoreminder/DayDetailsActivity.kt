package com.example.medicamentoreminder

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class DayDetailsActivity : AppCompatActivity() {

    private lateinit var dayTextView: TextView
    private lateinit var eventTitlesTextView: TextView
    private lateinit var addEventButton: Button
    private lateinit var backButton: Button
    private lateinit var eventListContainer: LinearLayout
    private val events: MutableList<Event> = mutableListOf()
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_details)

        // Inicializar las vistas
        dayTextView = findViewById(R.id.dayTextView)
        addEventButton = findViewById(R.id.addEventButton)
        backButton = findViewById(R.id.backButton)
        eventListContainer = findViewById(R.id.eventListContainer)
        eventTitlesTextView = findViewById(R.id.eventTitlesTextView)
        sharedPreferences = getSharedPreferences("EventPreferences", MODE_PRIVATE)

        // Obtener datos pasados con el Intent
        val selectedDay = intent.getStringExtra("SELECTED_DAY")
        val monthYear = intent.getStringExtra("MONTH_YEAR")

        // Mostrar el día seleccionado
        dayTextView.text = "$selectedDay, $monthYear"

        // Configurar el botón para agregar un evento
        addEventButton.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivityForResult(intent, 1001) // Usa un request code diferente si es necesario
        }

        // Configurar el botón de regreso
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun displayEvents(eventTitles: List<Event>) {
        eventListContainer.removeAllViews() // Limpiar lista existente

        if (eventTitles.isEmpty()) {
            eventTitlesTextView.text = "No hay notas"
        } else {
            for ((index, event) in eventTitles.withIndex()) {

                // Crear un CardView para cada evento
                val eventCard = CardView(this)
                val cardLayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                cardLayoutParams.setMargins(32, 0, 32, 16)
                eventCard.layoutParams = cardLayoutParams
                eventCard.radius = 16f
                eventCard.cardElevation = 8f
                eventCard.setCardBackgroundColor(resources.getColor(R.color.teal_700))

                // Crear el layout dentro del CardView
                val eventLayout = LinearLayout(this)
                eventLayout.orientation = LinearLayout.VERTICAL
                eventLayout.setPadding(24, 16, 24, 16)

                // Título del evento
                val titleTextView = TextView(this)
                titleTextView.text = event.title
                titleTextView.textSize = 24f
                titleTextView.setTextColor(resources.getColor(R.color.black))
                titleTextView.setTypeface(null, Typeface.BOLD)
                titleTextView.setPadding(0, 0, 0, 8)

                // Nota del evento
                val noteTextView = TextView(this)
                noteTextView.text = event.note
                noteTextView.textSize = 18f
                noteTextView.setTextColor(resources.getColor(R.color.purple_500))
                noteTextView.setLineSpacing(4f, 1f)

                // Agregar TextViews al layout del evento
                eventLayout.addView(titleTextView)
                eventLayout.addView(noteTextView)

                // Agregar el layout dentro del CardView
                eventCard.addView(eventLayout)

                // Agregar el CardView al contenedor principal
                eventListContainer.addView(eventCard)

                // Listener para el CardView
                eventCard.setOnClickListener {
                    val intent = Intent(this, EventDetailsActivity::class.java)
                    intent.putExtra("EVENT_TITLE", event.title)
                    intent.putExtra("EVENT_NOTE", event.note)
                    intent.putExtra("EVENT_INDEX", index)
                    startActivityForResult(intent, 1)
                }
            }
        }
    }

    private fun saveEvents() {
        val jsonString = gson.toJson(events)
        sharedPreferences.edit().putString("events", jsonString).apply()
        Log.d("DayDetails", "Eventos guardados en SharedPreferences: $jsonString")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedDay = intent.getStringExtra("SELECTED_DAY")
        val monthYear = intent.getStringExtra("MONTH_YEAR")

        if (requestCode == 1 && resultCode == RESULT_OK) {
            val eventDeleted = data?.getBooleanExtra("EVENT_DELETED", false) ?: false
            val eventIndex = data?.getIntExtra("EVENT_INDEX", -1) ?: -1
            if (eventDeleted && eventIndex != -1) {
                events.removeAt(eventIndex)
                saveEvents()
                loadEvents(selectedDay, monthYear) // Cargar eventos de la fecha seleccionada después de eliminar
            }
        }
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val title = data?.getStringExtra("EVENT_TITLE") ?: ""
            val note = data?.getStringExtra("EVENT_NOTE") ?: ""

            // Convertir el texto a un objeto Date, y luego formatearlo a "dd-MM-yyyy"
            val rawDateText = "$selectedDay, $monthYear"
            val inputFormat = SimpleDateFormat("d, MMMM yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = inputFormat.parse(rawDateText)?.let { dateObject ->
                outputFormat.format(dateObject)
            } ?: ""

            Log.d("DayDetails", "Guardando evento: Title=$title, Note=$note, Date=$date")

            // Guardar el evento y actualizar la lista
            events.add(Event(date, title, note))
            saveEvents()
            loadEvents(selectedDay, monthYear) // Cargar eventos de la fecha seleccionada después de agregar
        }
    }

    override fun onResume() {
        super.onResume()
        val selectedDay = intent.getStringExtra("SELECTED_DAY")
        val monthYear = intent.getStringExtra("MONTH_YEAR")
        loadEvents(selectedDay, monthYear)
    }

    private fun loadEvents(selectedDay: String?, monthYear: String?) {
        val jsonString = sharedPreferences.getString("events", null)
        if (jsonString != null) {
            val type = object : TypeToken<MutableList<Event>>() {}.type
            events.clear()
            events.addAll(gson.fromJson(jsonString, type))
            Log.d("DayDetails", "Eventos cargados: $events")
        }

        // Convierte la fecha seleccionada a "dd-MM-yyyy" para el filtro
        val formattedDate = if (selectedDay != null && monthYear != null) {
            val rawDateText = "$selectedDay, $monthYear"
            val inputFormat = SimpleDateFormat("d, MMMM yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            inputFormat.parse(rawDateText)?.let { dateObject -> outputFormat.format(dateObject) } ?: ""
        } else {
            ""
        }
        Log.d("DayDetails", "Fecha seleccionada para filtro: $formattedDate")
        // Filtra los eventos según la fecha formateada
        updateEventList(formattedDate)
    }

    private fun updateEventList(selectedDate: String) {
        // Filtrar eventos que coincidan con la fecha seleccionada
        val filteredEvents = events.filter { it.date == selectedDate }

        // Mostrar solo los eventos filtrados
        displayEvents(filteredEvents)
    }
}
