package com.example.mygymapp.ui.workout

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygymapp.R
import com.example.mygymapp.databinding.FragmentWorkoutBinding
import com.example.mygymapp.ui.viewmodel.MainViewModel

class WorkoutFragment : Fragment(R.layout.fragment_workout) {

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private val vm: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWorkoutBinding.bind(view)

        // RecyclerView initialisieren
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        val adapter = WeeklyPlanAdapter(vm.weeklyPlans.value ?: emptyList())
        binding.recycler.adapter = adapter

        // LiveData beobachten
        vm.weeklyPlans.observe(viewLifecycleOwner) { plans ->
            adapter.update(plans)
            binding.nextBtn.isEnabled = plans.isNotEmpty()
        }

        // Button-Click
        binding.nextBtn.setOnClickListener {
            // Deine Logik hier…
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
