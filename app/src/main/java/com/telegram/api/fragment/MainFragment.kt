package com.telegram.api.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telegram.api.databinding.FragmentMainBinding
import com.telegram.api.service.TypingService

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.switcherTyping.setOnCheckedChangeListener { compoundButton, b ->
            val intent = Intent(
                requireContext(),
                TypingService::class.java
            )
            if (b)
                requireContext().startService(intent)
            else
                requireContext().stopService(intent)

        }

    }
}