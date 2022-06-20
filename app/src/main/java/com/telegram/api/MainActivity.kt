package com.telegram.api

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.telegram.api.databinding.ActivityMainBinding
import com.telegram.api.service.TypingService
import inc.brody.tapi.TApi
import inc.brody.tapi.data.appdata.TConstants
import inc.brody.tapi.requests.TCheckAuthenticationPassword
import inc.brody.tapi.utils.Session
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi


class MainActivity : AppCompatActivity() {
    private val TAG = "TApiMain"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Init telegram API here, we will probably need variable
        // 'telegramApi' later, so it'd be better to make it global.
        val telegramApi = TApi.init(this.applicationContext)


        telegramApi.authState.setOnChangeListener {
            when (it) {
                TConstants.AUTH_WAIT_PHONE -> {
                    telegramApi.sendCodeOnPhone("+380950000597")
                    //There is going to be the whore number with country code

                }
                TConstants.AUTH_WAIT_PASS -> {
                    telegramApi.loginWithPassword("password")
                    //It also can require a Telegram password (Never faced it IRL)
                }
                TConstants.AUTH_WAIT_CODE -> {
                    binding.btnLogin.setOnClickListener {
                        telegramApi.loginWithReceivedCode(binding.editText.text.toString())
                        //Send a received code here
                    }
                }
                TConstants.AUTH_OK -> {
                    //If your auth is successful, you receive AUTH_OK and then you can
                    //fetch whatever you want.
                    //Here you can go to your App's main screen

                    startService(Intent(this, TypingService::class.java))
                }
                TConstants.AUTH_ERROR -> {
                    //We are going to log an error here
                    Log.d(TAG, telegramApi.authState.error.toString())
                }
            }
        }
    }
}