// Path: app/src/main/java/com/example/mygymapp/ui/plans/DailyPlanAdapter.kt
package com.example.mygymapp.ui.plans

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.data.DailyPlanWithExercises
import com.example.mygymapp.databinding.ItemDailyPlanBinding

class DailyPlanAdapter(
    private val onAddExercise: (planId: String, exerciseId: Long) -> Unit,
    private val onRemoveExercise: (planId: String, exerciseId: Long) -> Unit
) : ListAdapter<DailyPlanWithExercises, DailyPlanAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DailyPlanWithExercises>() {
            override fun areItemsTheSame(old: DailyPlanWithExercises, new: DailyPlanWithExercises) =
                old.plan.planId == new.plan.planId

            override fun areContentsTheSame(old: DailyPlanWithExercises, new: DailyPlanWithExercises) =
                old == new
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemDailyPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemDailyPlanBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: DailyPlanWithExercises) {
            b.title.text = item.plan.name
            b.emptyView.isVisible = item.exercises.isEmpty()
            b.btnAdd.setOnClickListener {
                onAddExercise(item.plan.planId, 1L)
            }
            // TODO: implement onRemoveExercise UI
        }
    }
}
