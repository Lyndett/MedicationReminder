package com.example.medicamentoreminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MedicationAdapter(
    private val medications: List<Medication>,
    private val onMedicationClick: (Medication, Int) -> Unit // Callback para el clic
) : RecyclerView.Adapter<MedicationAdapter.MedicationViewHolder>() {

    // ViewHolder que se encarga de cada elemento del RecyclerView
    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.medicationNameTextView)

        // Método para enlazar los datos del medicamento a la vista
        fun bind(medication: Medication) {
            // Establece los datos del medicamento en la vista
            nameTextView.text = medication.name

            // Maneja el clic en el elemento
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMedicationClick(medication, position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        // Inflamos el layout personalizado para el cuadrado
        val view = LayoutInflater.from(parent.context).inflate(R.layout.medication_item, parent, false)
        return MedicationViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        val medication = medications[position]
        holder.bind(medication)
    }

    override fun getItemCount(): Int {
        return medications.size // Devuelve el tamaño real de la lista
    }
}