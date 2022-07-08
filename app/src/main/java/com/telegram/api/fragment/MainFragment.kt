package com.telegram.api.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.telegram.api.databinding.FragmentMainBinding
import com.telegram.api.service.TypingService
import com.telegram.api.utils.isMyServiceRunning
import kotlinx.coroutines.Dispatchers


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


        binding.switcherTyping.isChecked =
            requireContext().isMyServiceRunning(TypingService::class.java)

        binding.switcherTyping.setOnCheckedChangeListener { compoundButton, isChecked ->
            val intent = Intent(
                requireContext(),
                TypingService::class.java
            )
            
            val field = intent.javaClass.getMethod("action")
            field.isAccessible = true


            if (isChecked)
                requireContext().startService(intent)
            else
                requireContext().stopService(intent)
        }
//        val set = mutableSetOf<TdApi.UpdateUser>()
//
//        TGetChats{
////            Log.d("test13", it?.javaClass?.name.toString())
//            if(it is TdApi.UpdateUser){
//                set.add(it)
//                Log.d("test13.1", it.user.firstName + it.user.lastName )
//            }
//        }
    }
}
