package com.example.medicamentoreminder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private val context: Context,
    private val days: List<String>,
    private val calendar: Calendar,
    private val events: MutableList<Event> = mutableListOf()

) : BaseAdapter() {

    private val onDayClick: (String, ArrayList<String>, ArrayList<String>) -> Unit = { selectedDay, eventTitles, eventNotes ->
        (context as? CalendarActivity)?.openDayDetails(selectedDay)
    }


    override fun getCount(): Int {
        return days.size
    }

    override fun getItem(position: Int): Any {
        return days[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.calendar_day_item, parent, false)
        val dayTextView: TextView = view.findViewById(R.id.dayTextView)
        val eventTextView: TextView = view.findViewById(R.id.eventTextView)

        // Configurar el día actual
        val day = days[position].trim()
        dayTextView.text = if (day.isEmpty()) " " else day
        val dayFormatted = String.format("%02d-%02d-%04d", day.toIntOrNull() ?: 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        Log.d("MyTag", "dayFormatted: $dayFormatted")
        events.forEach { println("Event date: ${it.date}") }
        events.forEach { event ->
            Log.d("EventInfo", event.toString())

        }
        val dayEvents = events.filter { it.date == dayFormatted }

        val eventTitles = ArrayList(dayEvents.map { it.title })
        val eventNotes = ArrayList(dayEvents.map { it.note })

        val event = events.filter { it.date == dayFormatted }

        // Si hay un evento, mostrar el título
        if (dayEvents.isNotEmpty()) {
            eventTextView.text = dayEvents[0].title
            eventTextView.visibility = View.VISIBLE
        } else {
            eventTextView.visibility = View.GONE
        }
        // Configurar estilos de texto
        dayTextView.apply {
            textSize = 18f  // Aumentamos el tamaño de la fuente
            setTextColor(context.resources.getColor(R.color.black))  // Color destacado para los números
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(8, 8, 8, 8)
        }
        eventTextView.apply {
            textSize = 12f  // Tamaño más pequeño para los eventos
            setTextColor(context.resources.getColor(R.color.black))  // Color distintivo para los títulos
            visibility = if (event != null) View.VISIBLE else View.GONE
        }
        // Configurar el listener de clic
        val onClickListener = View.OnClickListener {
            if (day.isNotEmpty()) onDayClick(day,eventTitles, eventNotes)
        }
        dayTextView.setOnClickListener(onClickListener)
        eventTextView.setOnClickListener(onClickListener)

        return view
    }

}
