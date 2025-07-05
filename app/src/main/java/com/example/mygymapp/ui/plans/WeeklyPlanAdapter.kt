// Path: app/src/main/java/com/example/mygymapp/ui/plans/WeeklyPlanAdapter.kt
package com.example.mygymapp.ui.plans

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.data.WeeklyPlanWithExercises
import com.example.mygymapp.databinding.ItemWeeklyPlanBinding

class WeeklyPlanAdapter(
    private val onAddExercise: (planId: String, exerciseId: Long) -> Unit,
    private val onRemoveExercise: (planId: String, exerciseId: Long) -> Unit
) : ListAdapter<WeeklyPlanWithExercises, WeeklyPlanAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WeeklyPlanWithExercises>() {
            override fun areItemsTheSame(old: WeeklyPlanWithExercises, new: WeeklyPlanWithExercises) =
                old.plan.planId == new.plan.planId

            override fun areContentsTheSame(old: WeeklyPlanWithExercises, new: WeeklyPlanWithExercises) =
                old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemWeeklyPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemWeeklyPlanBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: WeeklyPlanWithExercises) {
            b.title.text = item.plan.name
            b.emptyView.isVisible = item.exercises.isEmpty()
            b.btnAdd.setOnClickListener {
                onAddExercise(item.plan.planId, 1L)
            }
            // TODO: implement onRemoveExercise UI
        }
    }
}