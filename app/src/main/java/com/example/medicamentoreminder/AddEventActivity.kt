package com.example.medicamentoreminder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddEventActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        // Inicializar las vistas
        titleEditText = findViewById(R.id.titleEditText)
        noteEditText = findViewById(R.id.noteEditText)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)

        // Configurar el botón de guardar
        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val note = noteEditText.text.toString()

            // Crear el intent de resultado para pasar el nuevo evento de regreso
            val resultIntent = Intent()
            resultIntent.putExtra("EVENT_TITLE", title)
            resultIntent.putExtra("EVENT_NOTE", note)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // Configurar el botón de cancelar
        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
