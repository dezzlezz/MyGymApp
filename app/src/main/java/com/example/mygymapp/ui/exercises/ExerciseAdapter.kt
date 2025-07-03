package com.example.mygymapp.ui.exercises

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.databinding.ItemExerciseBinding

class ExerciseAdapter
    : ListAdapter<Exercise, ExerciseAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemExerciseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: ItemExerciseBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(ex: Exercise) {
            binding.tvName.text = ex.name
            binding.tvTag.text  = ex.tag
            // …
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(o1: Exercise, o2: Exercise) =
                o1.id == o2.id
            override fun areContentsTheSame(o1: Exercise, o2: Exercise) =
                o1 == o2
        }
    }
}
