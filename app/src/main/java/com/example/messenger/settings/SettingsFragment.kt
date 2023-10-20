package com.example.messenger.settings

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentSettingsBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var currentUId: String
    @Inject lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        injectDependencies()
        currentUId = viewModel.getCurrentUserId()!!

        setNavigationBarOnItemListener()
        getInfoAboutUser()
        setPhoto()
        getSavedMessagesColors()

        binding.friendsMessagesColorButton.setOnClickListener { openColorPicker(false) }
        binding.myMessagesColorButton.setOnClickListener { openColorPicker(true) }
        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)
    }

    private fun getSavedMessagesColors() {
        currentUId
        val sharedPrefs = activity?.getSharedPreferences("colors", Context.MODE_PRIVATE)
        val myColor = sharedPrefs?.getInt("myColor$currentUId", R.color.turquoise)
        val friendColor = sharedPrefs?.getInt("friendsColor$currentUId", R.color.turquoise)

        myColor?.also {
            binding.myMessagesColorButton.setTextColor(myColor)
            binding.myMessagesColorButton.iconTint = ColorStateList.valueOf(myColor)
        }
        friendColor?.also {
            binding.friendsMessagesColorButton.setTextColor(friendColor)
            binding.friendsMessagesColorButton.iconTint = ColorStateList.valueOf(friendColor)
        }
    }

    private fun setChosenColorToSharedPrefs(color: Int, myColor: Boolean) {
        val sharedPrefs = activity?.getSharedPreferences("colors", Context.MODE_PRIVATE)
        sharedPrefs?.edit()?.putInt(
            if (myColor) "myColor$currentUId"
            else "friendsColor$currentUId",
            color)?.apply()

        if (myColor) {
            binding.myMessagesColorButton.setTextColor(color)
            binding.myMessagesColorButton.iconTint = ColorStateList.valueOf(color)
        } else {
            binding.friendsMessagesColorButton.setTextColor(color)
            binding.friendsMessagesColorButton.iconTint = ColorStateList.valueOf(color)
        }
    }

    private fun openColorPicker(myColor: Boolean) {
        ColorPickerDialog.Builder(requireContext())
            .setTitle("Pick a color")
            .setPositiveButton("Select", object: ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    envelope?.also {
                        setChosenColorToSharedPrefs(envelope.color, myColor)
                    }
                }

            })
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .show()
    }
    private fun setPhoto() {
        viewModel.downloadImage(viewLifecycleOwner)
        viewModel.mainPhotoBitmap.observe(viewLifecycleOwner) {bitmap ->
            binding.mainPhoto.setImageBitmap(bitmap)
        }

        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            Glide.with(requireContext())
                .load(uri)
                .into(binding.mainPhoto)
        }
    }

    private fun setInfoAboutUser(user: User) {
        binding.myName.text = user.fullName
        binding.login.text = user.login
        binding.myEmail.text = user.email
    }

    private fun getInfoAboutUser() {
        if (viewModel.isInternetAvailable()) {
            viewModel.getCurrentFirebaseUser().observe(viewLifecycleOwner) {firebaseUser ->
                setInfoAboutUser(firebaseUser)
            }
        } else {
            viewModel.getCurrentRoomUser().observe(viewLifecycleOwner) {roomUser ->
                roomUser.apply {
                    val user = User(fullName, email, login, firebaseUserId, mutableListOf(),
                        mutableListOf(), mutableListOf()
                    )

                    setInfoAboutUser(user)
                }
            }
        }
    }

    private fun setNavigationBarOnItemListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.friends -> findNavController().navigate(R.id.friends_fragment)
                R.id.chats -> findNavController().navigate(R.id.chats_fragment)
                R.id.settings -> findNavController().navigate(R.id.settings_fragment)
            }
            true
        }
    }
}