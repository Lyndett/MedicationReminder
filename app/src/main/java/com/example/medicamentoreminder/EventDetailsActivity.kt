package com.example.medicamentoreminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var noteTextView: TextView
    private lateinit var deleteButton: Button
    private lateinit var editButton: Button
    private lateinit var backButton: Button
    private var eventDate: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        // Inicializar las vistas
        titleTextView = findViewById(R.id.titleTextView)
        noteTextView = findViewById(R.id.noteTextView)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)
        backButton = findViewById(R.id.backButton)
        // Obtener los datos del Intent
        val eventTitle = intent.getStringExtra("EVENT_TITLE") ?: "Sin título"
        val eventNote = intent.getStringExtra("EVENT_NOTE") ?: "Sin nota"
        eventDate = intent.getStringExtra("EVENT_DATE")

        // Mostrar la información del evento
        titleTextView.text = eventTitle
        noteTextView.text = eventNote

        deleteButton.setOnClickListener {
            // Aquí puedes eliminar el evento de la lista
            deleteEvent(eventDate)
        }

        // Configurar el botón de editar
        editButton.setOnClickListener {
            // Aquí puedes abrir un formulario para editar el evento
            val intent = Intent(this, EditEventActivity::class.java)
            intent.putExtra("EVENT_TITLE", eventTitle)
            intent.putExtra("EVENT_NOTE", eventNote)
            intent.putExtra("EVENT_DATE", eventDate)
            startActivityForResult(intent, 1) // Solicitar un resultado de la actividad de edición
        }

        // Configurar el botón de regresar
        backButton.setOnClickListener {
            finish() // Regresar a la actividad anterior
        }
    }
    private fun deleteEvent(eventDate: String?) {
        // Aquí implementas la lógica para eliminar el evento de la lista de eventos
        // Esto puede involucrar actualizar el Adapter del calendario o la lista de eventos
       /* val events = getSampleEvents().toMutableList()
        events.removeAll { it.date == eventDate }

        val resultIntent = Intent().apply {
            putExtra("EVENT_DELETED", true)
            putExtra("EVENT_INDEX", intent.getIntExtra("EVENT_INDEX", -1))
        }

        setResult(RESULT_OK, resultIntent)*/
        // Luego, debes asegurarte de actualizar cualquier vista o adapter que dependa de esta lista.
        finish() // Regresar a la actividad anterior después de eliminar el evento
    }

    // Recibir el resultado de la actividad de edición
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val updatedTitle = data?.getStringExtra("UPDATED_TITLE")
            val updatedNote = data?.getStringExtra("UPDATED_NOTE")
            titleTextView.text = updatedTitle
            noteTextView.text = updatedNote
        }
    }

}
