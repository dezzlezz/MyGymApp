// Datei: app/src/main/java/com/example/mygymapp/ui/exercises/AddExerciseFragment.kt
package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.Exercise
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.databinding.FragmentAddExerciseBinding
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.viewmodel.ExerciseViewModelFactory

class AddExerciseFragment : Fragment() {

    private var _binding: FragmentAddExerciseBinding? = null
    private val binding get() = _binding!!

    // Konstanten für Dropdown-Lists
    private val TAGS = listOf("calisthanics","flexibility","warmup","cardio","gym")
    private val BROAD = listOf("legs","arms","core","back","chest")
    private val SPECIFIC = mapOf(
        "legs"   to listOf("quads","hamstrings","glutes"),
        "arms"   to listOf("biceps","triceps","forearms"),
        "core"   to listOf("abs","obliques"),
        "back"   to listOf("lats","traps"),
        "chest"  to listOf("pecs")
    )

    private lateinit var viewModel: ExerciseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddExerciseBinding.inflate(inflater, container, false)

        // ─── ViewModel / Repository einrichten ─────────────────────
        val db      = AppDatabase.getInstance(requireContext())
        val repo    = ExerciseRepository(db.exerciseDao())
        val factory = ExerciseViewModelFactory(repo)
        viewModel   = ViewModelProvider(this, factory)
            .get(ExerciseViewModel::class.java)

        // ─── Spinner & Adapter ─────────────────────────────────────
        binding.spinnerTag.adapter   = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, TAGS)
        binding.spinnerBroad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, BROAD)

        binding.spinnerBroad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val list = SPECIFIC[BROAD[pos]] ?: emptyList()
                binding.spinnerSpecific.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // initial spezifische Gruppe füllen
        binding.spinnerBroad.setSelection(0)

        // ─── Speichern-Button ───────────────────────────────────────
        binding.btnSave.setOnClickListener {
            // Entity erstellen
            val exercise = Exercise(
                name          = binding.etName.text.toString(),
                description   = binding.etDescription.text.toString(),
                imageUri      = binding.etImageUri.text.toString(),
                tag           = binding.spinnerTag.selectedItem as String,
                rating        = binding.ratingBar.rating.toInt(),
                muscleGroup   = binding.spinnerBroad.selectedItem as String,
                specificMuscle= binding.spinnerSpecific.selectedItem as String
            )
            // in DB speichern
            viewModel.insert(exercise)
            // zurück zur Liste
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
