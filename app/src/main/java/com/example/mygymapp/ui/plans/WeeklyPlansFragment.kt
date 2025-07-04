package com.example.mygymapp.ui.plans

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mygymapp.R
import com.example.mygymapp.MyApp
import com.example.mygymapp.data.WeeklyPlanRepository
import com.example.mygymapp.ui.viewmodel.WeeklyPlansViewModel
import com.example.mygymapp.ui.viewmodel.WeeklyPlansViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WeeklyPlansFragment : Fragment(R.layout.fragment_weekly_plans) {

    private val viewModel: WeeklyPlansViewModel by viewModels {
        WeeklyPlansViewModelFactory(
            (requireActivity().application as MyApp).weeklyPlanRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.weeklyPlans.observe(viewLifecycleOwner) { plans ->
            // TODO: Adapter updaten
        }

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            // TODO: Dialog öffnen und viewModel.addPlan(...) aufrufen
        }
    }
}
