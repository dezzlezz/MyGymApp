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
import com.example.mygymapp.databinding.FragmentDailyPlansBinding
import com.example.mygymapp.ui.viewmodel.PlansViewModel

class DailyPlansFragment : Fragment() {
    private var _binding: FragmentDailyPlansBinding? = null
    private val binding get() = _binding!!
    private val vm: PlansViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup with reps/sets handling
        val adapter = DailyPlanAdapter(
            onAddExercise = { planId, exId, reps, sets ->
                vm.addExerciseToDailyPlan(planId, exId, reps, sets)
            },
            onRemoveExercise = { planId, exId ->
                vm.removeExerciseFromDailyPlan(planId, exId)
            }
        )

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        // Observe LiveData
        vm.dailyPlansWithExercises.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.isVisible = list.isEmpty()
        }

        // FAB to add a new Daily Plan
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_dailyPlansFragment_to_addDailyPlanFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
