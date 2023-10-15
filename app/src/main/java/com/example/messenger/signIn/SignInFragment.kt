package com.example.messenger.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding : FragmentSignInBinding
    @Inject lateinit var viewModel: SignInViewModel
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(layoutInflater)

        super.onStart()
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            findNavController().navigate(R.id.chats_fragment)
        }

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
}