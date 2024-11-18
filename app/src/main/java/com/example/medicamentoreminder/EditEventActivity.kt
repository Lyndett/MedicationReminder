package com.example.medicamentoreminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class EditEventActivity : Activity() {

    private lateinit var titleEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_event)

        // Inicializar vistas
        titleEditText = findViewById(R.id.titleEditText)
        noteEditText = findViewById(R.id.noteEditText)
        saveButton = findViewById(R.id.saveButton)

        // Obtener los datos del Intent
        val eventTitle = intent.getStringExtra("EVENT_TITLE")
        val eventNote = intent.getStringExtra("EVENT_NOTE")

        // Rellenar los campos con los datos actuales
        titleEditText.setText(eventTitle)
        noteEditText.setText(eventNote)

        // Configurar el botón de guardar
        saveButton.setOnClickListener {
            val updatedTitle = titleEditText.text.toString()
            val updatedNote = noteEditText.text.toString()

            // Devolver los datos editados a la actividad anterior
            val resultIntent = Intent()
            resultIntent.putExtra("UPDATED_TITLE", updatedTitle)
            resultIntent.putExtra("UPDATED_NOTE", updatedNote)

            setResult(RESULT_OK, resultIntent)
            finish() // Cerrar la actividad de edición
        }
    }
}
