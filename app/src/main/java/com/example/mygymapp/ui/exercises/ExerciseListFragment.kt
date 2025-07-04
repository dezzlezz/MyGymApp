package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygymapp.data.AppDatabase
import com.example.mygymapp.data.ExerciseRepository
import com.example.mygymapp.databinding.FragmentExerciseListBinding
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import com.example.mygymapp.ui.viewmodel.ExerciseViewModelFactory
import com.example.mygymapp.R
class ExerciseListFragment : Fragment() {
    private var _b: FragmentExerciseListBinding? = null
    private val b get() = _b!!
    private lateinit var vm: ExerciseViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentExerciseListBinding.inflate(inflater, container, false)

        val db = AppDatabase.getInstance(requireContext())
        val repo = ExerciseRepository(db.exerciseDao())
        val fac = ExerciseViewModelFactory(repo)
        vm = ViewModelProvider(this, fac).get(ExerciseViewModel::class.java)

        val adapter = ExerciseAdapter()
        b.rvExercises.layoutManager = LinearLayoutManager(requireContext())
        b.rvExercises.adapter = adapter

        vm.exercises.observe(viewLifecycleOwner) { adapter.submitList(it) }
        b.fabAdd.setOnClickListener { findNavController().navigate(R.id.action_list_to_add) }

        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
