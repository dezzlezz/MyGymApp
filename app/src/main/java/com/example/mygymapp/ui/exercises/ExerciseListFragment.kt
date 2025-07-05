package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.R
import com.example.mygymapp.ui.screens.ExerciseListScreen




class ExerciseListFragment : Fragment() {
    private val vm: ExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ComposeView als View zurückgeben!
        return ComposeView(requireContext()).apply {
            setContent {
                com.example.mygymapp.ui.screens.ExerciseListScreen(
                    viewModel = vm,
                    onAddExercise = {
                        // Navigation zu AddExerciseFragment
                        findNavController().navigate(R.id.action_exerciseListFragment_to_addExerciseFragment)
                    },
                    onEditExercise = { exerciseId ->
                        // Navigation zu EditExerciseFragment (wenn vorhanden)
                        // findNavController().navigate(R.id.action_exerciseListFragment_to_editExerciseFragment)
                    }
                )
            }
        }
    }
}
