package com.example.messenger.fragments

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
import androidx.navigation.fragment.findNavController
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentSignUpBinding
import java.lang.Exception


class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var app : MyApp
    private lateinit var binding : FragmentSignUpBinding

    private var photoUri : Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
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

    private fun describeError(e: Exception) {
        Toast.makeText(
            requireContext(),
            e.localizedMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun createUser() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val fullName = binding.fullName.text.toString()
        val login = binding.login.text.toString()

        app.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = app.auth.currentUser?.uid
                    val user = User(
                        fullName, email, login, userId.toString(),
                        mutableListOf(), mutableListOf(), mutableListOf()
                    )

                    if (userId != null) {
                        app.database.getReference("users").child(userId).setValue(user)
                        uploadPhoto(userId)
                    }
                } else {
                    task.exception?.also { describeError(it) }
                }
            }
    }

    private fun uploadPhoto(userId: String) {
        photoUri?.let { app.storage.getReference("avatars/$userId")
            .putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) findNavController().navigate(R.id.chats_fragment)
                else task.exception?.also { it1 -> describeError(it1) }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater)

        binding.mainPhoto.setOnClickListener{checkPermission()}
        binding.registerButton.setOnClickListener{createUser()}
        return binding.root
    }
}