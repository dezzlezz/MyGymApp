package com.example.mygymapp.ui.workout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.databinding.ItemWeeklyPlanBinding
import com.example.mygymapp.model.WeeklyPlan

class WeeklyPlanAdapter(
    private var items: List<WeeklyPlan>
) : RecyclerView.Adapter<WeeklyPlanAdapter.VH>() {

    // ViewHolder-Klasse
    inner class VH(val binding: ItemWeeklyPlanBinding) : RecyclerView.ViewHolder(binding.root)

    // Layout inflater für jede Zeile
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemWeeklyPlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    // Daten auf ViewHolder binden
    override fun onBindViewHolder(holder: VH, position: Int) {
        val plan = items[position]
        holder.binding.textName.text = plan.name
    }

    // Anzahl der Einträge
    override fun getItemCount(): Int = items.size

    // Methode zum Aktualisieren der Liste
    fun update(newItems: List<WeeklyPlan>) {
        items = newItems
        notifyDataSetChanged()
    }
}
