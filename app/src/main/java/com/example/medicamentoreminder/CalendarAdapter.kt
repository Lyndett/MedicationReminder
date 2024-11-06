package com.example.medicamentoreminder

import android.content.Context
import android.content.Intent
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
    private val calendar: Calendar
) : BaseAdapter() {

    private val onDayClick: (String) -> Unit = { selectedDay ->
        val intent = Intent(context, DayDetailsActivity::class.java)
        intent.putExtra("SELECTED_DAY", selectedDay)
        intent.putExtra("MONTH_YEAR", SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time))
        context.startActivity(intent)
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

        // Configurar el día actual
        val day = days[position].trim()
        dayTextView.text = if (day.isEmpty()) " " else day

        // Configurar estilos de texto
        dayTextView.textSize = 16f
        dayTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        // Configurar el listener de clic
        dayTextView.setOnClickListener {
            onDayClick(day)
        }

        return view
    }
}
