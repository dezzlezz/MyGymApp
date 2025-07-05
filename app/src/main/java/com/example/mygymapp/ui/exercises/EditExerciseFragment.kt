package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.screens.ExerciseAddEditScreen

class EditExerciseFragment : Fragment() {
    private val args by navArgs<EditExerciseFragmentArgs>()
    private val vm: ExerciseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ExerciseAddEditScreen(
                    viewModel = vm,
                    exerciseId = args.exerciseId,        // <-- Hier kommt die ID zum Bearbeiten
                    onSaved = { findNavController().popBackStack() } // <-- Nach dem Speichern zurück!
                )
            }
        }
    }
}
