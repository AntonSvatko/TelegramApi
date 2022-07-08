package com.telegram.api.fragment

import android.content.*
import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telegram.api.databinding.FragmentLoginBinding
import com.telegram.api.utils.batteryOptimization
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

    override fun onResume() {
        super.onResume()
        batteryOptimization()
    }

    private fun batteryOptimization() {
        val pm = requireContext().getSystemService(POWER_SERVICE) as PowerManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (pm!!.isIgnoringBatteryOptimizations(requireContext().packageName)) {
                startTdApi()
            } else {
                requireContext().batteryOptimization()

                val intentFilter = IntentFilter("android.os.action.POWER_SAVE_WHITELIST_CHANGED")
                val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        startTdApi()
                    }
                }
                requireContext().registerReceiver(broadcastReceiver, intentFilter)
            }
        } else {
            startTdApi()
        }
    }


    private fun startTdApi() {
        val telegramApi = TApi.init(requireContext().applicationContext)

        if (telegramApi.authState.value == TConstants.AUTH_OK) {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
        }


        telegramApi.authState.setOnChangeListener {
            Log.d("test4", it.toString())
            when (it) {
                TConstants.AUTH_WAIT_PHONE -> {
                    binding.llRoot.visibility = View.VISIBLE
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