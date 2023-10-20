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
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding: FragmentSignInBinding
    private var currentUser: FirebaseUser? = null
    @Inject lateinit var viewModel: SignInViewModel
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onStart()
        binding = FragmentSignInBinding.inflate(layoutInflater)
        injectDependencies()
        currentUser = firebaseAuth.currentUser
        checkIfUserSignedIn()
        navigateToSignUpScreen()
        loginUser()

        return binding.root
    }

    private fun loginUser() {
        binding.loginButton.setOnClickListener {
            viewModel.signInUser(
                binding.emailSignIn.text.toString(),
                binding.passwordSignIn.text.toString(),
                findNavController()
            )
        }
    }

    private fun navigateToSignUpScreen() {
        binding.joinNow.setOnClickListener {
            findNavController().navigate(R.id.sign_up_fragment)
        }
    }

    private fun checkIfUserSignedIn() {
        if (currentUser != null) {
            findNavController().navigate(R.id.chats_fragment)
        }
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)
    }
}