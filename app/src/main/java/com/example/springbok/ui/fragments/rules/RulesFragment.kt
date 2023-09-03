package com.example.springbok.ui.fragments.rules

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.databinding.FragmentRulesBinding

class RulesFragment : Fragment(R.layout.fragment_rules) {

    private val binding by viewBinding(FragmentRulesBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        ivGoToBackMenu.setOnClickListener{
            findNavController().navigateUp()
        }
    }
}