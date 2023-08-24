package com.example.springbok.ui.fragments.lose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.springbok.R
import com.example.springbok.databinding.FragmentLoseBinding

class LoseFragment : Fragment(R.layout.fragment_lose) {

    private val binding by viewBinding(FragmentLoseBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_loseFragment_to_playFieldFragment)
        }
    }
}