package com.example.mygymapp.ui.exercises

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.databinding.ItemExerciseBinding

class ExerciseAdapter
    : ListAdapter<Exercise, ExerciseAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemExerciseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, pos: Int) =
        holder.bind(getItem(pos))

    inner class VH(private val b: ItemExerciseBinding)
        : RecyclerView.ViewHolder(b.root) {
        fun bind(ex: Exercise) {
            b.tvName.text = ex.name
            b.tvTag.text  = ex.tag
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(a: Exercise, b: Exercise) = a.id == b.id
            override fun areContentsTheSame(a: Exercise, b: Exercise) = a == b
        }
    }
}
