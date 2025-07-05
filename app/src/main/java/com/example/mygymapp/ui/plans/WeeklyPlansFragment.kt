// Path: app/src/main/java/com/example/mygymapp/ui/plans/WeeklyPlansFragment.kt
package com.example.mygymapp.ui.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygymapp.R
import com.example.mygymapp.databinding.FragmentWeeklyPlansBinding
import com.example.mygymapp.ui.viewmodel.PlansViewModel

class WeeklyPlansFragment : Fragment() {
    private var _binding: FragmentWeeklyPlansBinding? = null
    private val binding get() = _binding!!
    private val vm: PlansViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        val adapter = WeeklyPlanAdapter(
            onAddExercise = { planId, exId -> vm.addExerciseToWeeklyPlan(planId, exId) },
            onRemoveExercise = { planId, exId -> vm.removeExerciseFromWeeklyPlan(planId, exId) }
        )
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Observe LiveData
        vm.weeklyPlansWithExercises.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.isVisible = list.isEmpty()
        }

        // FAB to add a new Weekly Plan
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_weeklyPlansFragment_to_addWeeklyPlanFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}