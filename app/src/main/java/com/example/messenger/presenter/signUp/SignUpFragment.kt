package com.example.messenger.presenter.signUp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.example.messenger.databinding.FragmentSignUpBinding
import javax.inject.Inject


class SignUpFragment: Fragment(R.layout.fragment_sign_up) {
    private lateinit var binding: FragmentSignUpBinding
    private var photoUri : Uri? = null
    @Inject lateinit var viewModel: SignUpViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        injectDependencies()

        binding.mainPhoto.setOnClickListener{checkPermission()}
        binding.registerButton.setOnClickListener{createUser()}
        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.inject(this)
    }


    // launcher used to request permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted ->
            if (isGranted) {
                pickPhoto()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.grant_permission_toast_text),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // launcher used to pick photo
    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                photoUri = result.data?.data
                binding.mainPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.mainPhoto.setImageURI(photoUri)
            }
        }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        photoPickerLauncher.launch(intent)
    }

    private fun checkPermission() {
        if (ContextCompat
                .checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
            PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            pickPhoto()
        }
    }

    private fun createUser() {
        val email = binding.emailSignUp.text.toString()
        val login = binding.login.text.toString()
        val password = binding.passwordSignUp.text.toString()
        val fullName = binding.fullName.text.toString()

        val user = User(fullName, email, login, "", mutableListOf(), mutableListOf(), mutableListOf())

        photoUri?.let { photoUri -> viewModel.signUpUser(user, password, photoUri) }
        viewModel.signedUp.observe(viewLifecycleOwner) {signedUp ->
            if (signedUp) {
                findNavController().navigate(R.id.chats_fragment)
                viewModel.getCurrentUserObject()
            }
        }
    }
}