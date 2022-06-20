package com.telegram.api.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telegram.api.databinding.FragmentLoginBinding
import com.telegram.api.service.TypingService
import inc.brody.tapi.TApi
import inc.brody.tapi.data.appdata.TConstants

class LoginFragment : Fragment() {
    private val TAG = "TApiMain"

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.isVisible = false
        //Init telegram API here, we will probably need variable
        // 'telegramApi' later, so it'd be better to make it global.
        val telegramApi = TApi.init(requireContext().applicationContext)


        telegramApi.authState.setOnChangeListener {
            when (it) {
                TConstants.AUTH_WAIT_PHONE -> {
                    binding.root.isVisible = true
                    binding.btnSendCode.setOnClickListener {
                        telegramApi.sendCodeOnPhone(binding.editTextPhone.text.toString())
                    }
                    //There is going to be the whore number with country code

                }
                TConstants.AUTH_WAIT_PASS -> {
                    telegramApi.loginWithPassword("password")
                    //It also can require a Telegram password (Never faced it IRL)
                }
                TConstants.AUTH_WAIT_CODE -> {
                    binding.btnLogin.setOnClickListener {
                        telegramApi.loginWithReceivedCode(binding.editTextCode.text.toString())
                        //Send a received code here
                    }
                }
                TConstants.AUTH_OK -> {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                }
                TConstants.AUTH_ERROR -> {
                    //We are going to log an error here
                    Log.d(TAG, telegramApi.authState.error.toString())
                }
            }
        }
    }
}