package com.example.mygymapp.ui.plans

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.R
import com.example.mygymapp.data.Exercise

// Datenklasse für ausgewählte Übungen
data class SelectedExercise(
    val exercise: Exercise,
    var reps: Int = 10,
    var sets: Int = 3
)

class SelectedExerciseAdapter : RecyclerView.Adapter<SelectedExerciseAdapter.VH>() {

    private val items = mutableListOf<SelectedExercise>()
    val currentList: List<SelectedExercise> get() = items

    fun submitList(list: List<SelectedExercise>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_exercise, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvExerciseName)
        private val etReps: EditText = view.findViewById(R.id.etReps)
        private val etSets: EditText = view.findViewById(R.id.etSets)
        val handle: ImageView = view.findViewById(R.id.ivHandle)

        fun bind(item: SelectedExercise) {
            tvName.text = item.exercise.name
            etReps.setText(item.reps.toString())
            etSets.setText(item.sets.toString())

            etReps.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.reps = s.toString().toIntOrNull() ?: item.reps
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            etSets.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.sets = s.toString().toIntOrNull() ?: item.sets
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}