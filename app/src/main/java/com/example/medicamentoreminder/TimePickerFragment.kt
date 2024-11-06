package com.example.medicamentoreminder

import android.app.Dialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class TimePickerFragment : DialogFragment(), TimePicker.OnTimeChangedListener {

    var listener: TimePickerListener? = null

    interface TimePickerListener {
        fun onTimeSet(hourOfDay: Int, minute: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timePicker = TimePicker(requireContext())
        timePicker.setIs24HourView(true)

        // Establecer el TimePicker en 00:00
        timePicker.hour = 0
        timePicker.minute = 0

        timePicker.setOnTimeChangedListener(this)

        return AlertDialog.Builder(requireActivity())
            .setTitle("Seleccionar Intervalo (HH:MM)")
            .setMessage("Seleccione el intervalo de tiempo en horas y minutos.")
            .setView(timePicker)
            .setPositiveButton("Aceptar") { _, _ ->
                listener?.onTimeSet(timePicker.hour, timePicker.minute) // Usa hour en lugar de currentHour
            }
            .setNegativeButton("Cancelar", null)
            .create()
    }


    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        // Puedes hacer algo aquí si necesitas reaccionar a cambios de tiempo en tiempo real
    }
}
