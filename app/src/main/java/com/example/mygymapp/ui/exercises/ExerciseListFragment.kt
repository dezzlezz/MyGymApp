package com.example.mygymapp.ui.exercises

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygymapp.R
import com.example.mygymapp.databinding.FragmentExerciseListBinding
import com.example.mygymapp.ui.viewmodel.ExerciseViewModel
import androidx.navigation.fragment.findNavController
import androidx.core.view.isVisible

class ExerciseListFragment : Fragment(R.layout.fragment_exercise_list) {

    private var _binding: FragmentExerciseListBinding? = null
    private val binding get() = _binding!!
    private val vm: ExerciseViewModel by viewModels()
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExerciseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseAdapter { ex -> vm.delete(ex.id) }
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        vm.allExercises.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyView.isVisible = list.isEmpty()
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_exerciseListFragment_to_addExerciseFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}  // ← Ganz wichtig: die abschließende “}” hier
