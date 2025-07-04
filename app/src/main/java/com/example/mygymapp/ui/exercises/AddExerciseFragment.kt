package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mygymapp.R
import com.example.mygymapp.databinding.FragmentAddExerciseBinding
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.google.android.material.snackbar.Snackbar

class AddExerciseFragment : Fragment(R.layout.fragment_add_exercise) {
    private var _binding: FragmentAddExerciseBinding? = null
    private val binding get() = _binding!!
    private val vm: ExerciseViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        FragmentAddExerciseBinding.inflate(inflater, c, false).also { _binding = it }.root

    override fun onViewCreated(v: View, s: Bundle?) {
        super.onViewCreated(v, s)
        binding.saveButton.setOnClickListener {
            val name = binding.nameInput.text.toString().trim()
            val reps = binding.repsInput.text.toString().toIntOrNull() ?: 0
            val sets = binding.setsInput.text.toString().toIntOrNull() ?: 0

            if (name.isEmpty() || reps<=0 || sets<=0) {
                Snackbar.make(v, "Bitte gültige Werte eingeben", Snackbar.LENGTH_SHORT).show()
            } else {
                vm.insert(Exercise(name = name, reps = reps, sets = sets))
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
