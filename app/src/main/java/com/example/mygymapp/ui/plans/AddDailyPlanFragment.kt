package com.example.mygymapp.ui.plans

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygymapp.R
import com.example.mygymapp.data.DailyPlan
import com.example.mygymapp.databinding.FragmentAddDailyPlanBinding
import com.example.mygymapp.ui.viewmodel.PlansViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddDailyPlanFragment : Fragment() {
    private var _binding: FragmentAddDailyPlanBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlansViewModel by activityViewModels()
    private lateinit var adapter: SelectedExerciseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddDailyPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Recycler setup
        adapter = SelectedExerciseAdapter()
        binding.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExercises.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) =
                adapter.moveItem(vh.adapterPosition, target.adapterPosition)
            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) = Unit
        }).attachToRecyclerView(binding.rvExercises)

        // Übungen laden
        viewModel.dailyPlansWithExercises.observe(viewLifecycleOwner) {
            // hier ggf. separate LiveData für alle Exercises holen
        }

        // Speichern
        binding.fabSave.setOnClickListener {
            val planId = binding.etPlanId.text.toString().takeIf { it.isNotBlank() }
                ?: java.util.UUID.randomUUID().toString()
            val name = binding.etPlanName.text.toString().trim()
            val desc = binding.etPlanDescription.text.toString().trim()

            val selected = adapter.currentList.filter { it.reps > 0 && it.sets > 0 }
            val ids = selected.map { it.exercise.id }
            val repsList = selected.map { it.reps }
            val setsList = selected.map { it.sets }

            viewModel.insertDailyPlanWithDetails(planId, name, desc, ids, repsList, setsList)
            findNavController().popBackStack()
        }

        // Abbrechen
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
