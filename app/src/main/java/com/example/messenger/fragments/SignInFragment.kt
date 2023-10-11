package com.example.messenger.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.databinding.FragmentSignInBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding : FragmentSignInBinding
    lateinit var app : MyApp

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

        binding.joinNow.setOnClickListener {
            findNavController().navigate(R.id.sign_up_fragment)
        }

        binding.loginButton.setOnClickListener { authenticateUser() }

        return binding.root
    }

    private fun authenticateUser() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        app.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                findNavController().navigate(R.id.chats_fragment)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = app.auth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.chats_fragment)
        }
    }
}