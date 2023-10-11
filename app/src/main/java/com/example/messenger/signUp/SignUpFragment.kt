package com.example.messenger.signUp

import android.Manifest
import android.app.Activity
import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.ViewModelFactory
import com.example.messenger.databinding.FragmentSignUpBinding


class SignUpFragment: Fragment(R.layout.fragment_sign_up) {
    lateinit var app: MyApp
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SignUpViewModel

    private var photoUri : Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        val factory = ViewModelFactory(app, SignUpViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]

        binding.mainPhoto.setOnClickListener{checkPermission()}
        binding.registerButton.setOnClickListener{createUser()}
        return binding.root
    }

    // launcher used to request permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {isGranted ->
            if (isGranted) {
                pickPhoto()
            } else {
                Toast.makeText(
                    context,
                    "You have to grant storage permission to upload photo",
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
        val email = binding.email.text.toString()
        val login = binding.login.text.toString()
        val password = binding.password.text.toString()
        val fullName = binding.fullName.text.toString()

        photoUri?.let { viewModel.createUser(email, login, fullName, password, it, findNavController()) }
    }
}