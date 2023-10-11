package com.example.messenger.signIn

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.ViewModelFactory
import com.example.messenger.databinding.FragmentSignInBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding : FragmentSignInBinding
    lateinit var app : MyApp
    private lateinit var viewModel: SignInViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        val factory = ViewModelFactory(app, SignInViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]

        binding.joinNow.setOnClickListener {
            findNavController().navigate(R.id.sign_up_fragment)
        }

        binding.loginButton.setOnClickListener {
            viewModel.signInUser(
                binding.email.text.toString(),
                binding.password.text.toString(),
                findNavController()
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val currentUser = app.auth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.chats_fragment)
        }
    }
}