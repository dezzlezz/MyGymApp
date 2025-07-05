package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseCategory
import com.example.mygymapp.data.MuscleGroup
import com.example.mygymapp.databinding.FragmentAddExerciseBinding
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel

class AddExerciseFragment : Fragment() {

    private var _binding: FragmentAddExerciseBinding? = null
    private val binding get() = _binding!!
    private val vm: ExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExerciseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Category Spinner
        val categoryValues = ExerciseCategory.values().map { it.name }
        binding.categorySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categoryValues
        )
        binding.categorySpinner.setSelection(0)

        // MuscleGroup Spinner
        val muscleGroupValues = MuscleGroup.values().map { it.name }
        binding.muscleGroupSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            muscleGroupValues
        )
        binding.muscleGroupSpinner.setSelection(0)

        binding.saveButton.setOnClickListener {
            try {
                val name = binding.nameInput.text?.toString()?.trim() ?: ""
                val description = binding.descriptionInput.text?.toString()?.trim() ?: ""
                val muscle = binding.muscleInput.text?.toString()?.trim() ?: ""

                // Validation
                if (name.isBlank()) {
                    binding.nameLayout.error = "Required"
                    return@setOnClickListener
                } else {
                    binding.nameLayout.error = null
                }
                if (muscle.isBlank()) {
                    binding.muscleLayout.error = "Required"
                    return@setOnClickListener
                } else {
                    binding.muscleLayout.error = null
                }

                val categoryString = binding.categorySpinner.selectedItem?.toString() ?: ""
                val muscleGroupString = binding.muscleGroupSpinner.selectedItem?.toString() ?: ""

                val category = ExerciseCategory.values().firstOrNull { it.name == categoryString }
                    ?: ExerciseCategory.Gym // Fallback
                val muscleGroup = MuscleGroup.values().firstOrNull { it.name == muscleGroupString }
                    ?: MuscleGroup.Arms // Fallback

                val likeability = binding.likeabilityRatingBar.rating.toInt()

                // Insert Exercise
                vm.insert(
                    Exercise(
                        name = name,
                        description = description,
                        category = category,
                        likeability = likeability,
                        muscleGroup = muscleGroup,
                        muscle = muscle
                    )
                )

                Toast.makeText(requireContext(), "Exercise saved!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
