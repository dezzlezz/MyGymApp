// app/src/main/java/com/example/mygymapp/ui/plans/DailyPlansFragment.kt
package com.example.mygymapp.ui.plans


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.mygymapp.R
import com.example.mygymapp.MyApp
import com.example.mygymapp.data.DailyPlanRepository
import com.example.mygymapp.ui.viewmodel.DailyPlansViewModel
import com.example.mygymapp.ui.viewmodel.DailyPlansViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DailyPlansFragment : Fragment(R.layout.fragment_daily_plans) {

    // ViewModel per Factory aus der Application holen
    private val viewModel: DailyPlansViewModel by viewModels {
        DailyPlansViewModelFactory(
            (requireActivity().application as MyApp).dailyPlanRepository
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Beobachte LiveData
        viewModel.dailyPlans.observe(viewLifecycleOwner) { plans ->
            // TODO: Adapter updaten
        }

        // FAB für neuen Plan
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            // TODO: Dialog öffnen und viewModel.addPlan(...) aufrufen
        }
    }
}


