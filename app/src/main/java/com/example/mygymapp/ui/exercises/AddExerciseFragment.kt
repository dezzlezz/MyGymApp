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
    private var _b: FragmentAddExerciseBinding? = null
    private val b get() = _b!!
    private lateinit var vm: ExerciseViewModel

    private val TAGS = listOf("calisthanics","flexibility","warmup","cardio","gym")
    private val BROAD = listOf("legs","arms","core","back","chest")
    private val SPEC = mapOf(
        "legs" to listOf("quads","hamstrings","glutes"),
        "arms" to listOf("biceps","triceps","forearms"),
        "core" to listOf("abs","obliques"),
        "back" to listOf("lats","traps"),
        "chest" to listOf("pecs")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentAddExerciseBinding.inflate(inflater, container, false)

        val db = AppDatabase.getInstance(requireContext())
        val repo = ExerciseRepository(db.exerciseDao())
        val f = ExerciseViewModelFactory(repo)
        vm = ViewModelProvider(this, f).get(ExerciseViewModel::class.java)

        b.spinnerTag.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, TAGS)
        b.spinnerBroad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, BROAD)
        b.spinnerBroad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val list = SPEC[BROAD[pos]] ?: emptyList()
                b.spinnerSpecific.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        b.spinnerBroad.setSelection(0)

        b.btnSave.setOnClickListener {
            val ex = Exercise(
                name = b.etName.text.toString(),
                description = b.etDescription.text.toString(),
                imageUri = b.etImageUri.text.toString(),
                tag = b.spinnerTag.selectedItem as String,
                rating = b.ratingBar.rating.toInt(),
                muscleGroup = b.spinnerBroad.selectedItem as String,
                specificMuscle = b.spinnerSpecific.selectedItem as String
            )
            vm.insert(ex)
            findNavController().popBackStack()
        }

        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
