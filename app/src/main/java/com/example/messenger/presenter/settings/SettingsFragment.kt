package com.example.messenger.presenter.settings

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.example.messenger.databinding.FragmentSettingsBinding
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    @Inject lateinit var viewModel: SettingsViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        injectDependencies()

        setNavigationBarOnItemListener()
        getInfoAboutUser()
        setPhoto()
        getSavedMessagesColors()
        setSignOutButtonListener()

        binding.friendsMessagesColorButton.setOnClickListener { openColorPicker(false) }
        binding.myMessagesColorButton.setOnClickListener { openColorPicker(true) }
        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent?.inject(this)
    }

    private fun setSignOutButtonListener() {
        binding.signOut.setOnClickListener {
            viewModel.signOutUser()
            //findNavController().navigate(R.id.sign_in_fragment)
        }
    }

    private fun getSavedMessagesColors() {
        viewModel.currentUserId.observe(viewLifecycleOwner) { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val myColor = viewModel.getMessagesColor("myColor$id")
                val friendColor = viewModel.getMessagesColor("friendsColor$id")

                binding.myMessagesColorButton.setTextColor(myColor)
                binding.myMessagesColorButton.iconTint = ColorStateList.valueOf(myColor)
                binding.friendsMessagesColorButton.setTextColor(friendColor)
                binding.friendsMessagesColorButton.iconTint = ColorStateList.valueOf(friendColor)
            }
        }
    }

    private fun saveChosenColorToSharedPrefs(color: Int, myColor: Boolean) {
        viewModel.currentUserId.observe(viewLifecycleOwner) {id ->
            viewModel.setMessagesColor(color, if (myColor) "myColor$id" else "friendsColor$id")

            if (myColor) {
                binding.myMessagesColorButton.setTextColor(color)
                binding.myMessagesColorButton.iconTint = ColorStateList.valueOf(color)
            } else {
                binding.friendsMessagesColorButton.setTextColor(color)
                binding.friendsMessagesColorButton.iconTint = ColorStateList.valueOf(color)
            }
        }
    }

    private fun openColorPicker(myColor: Boolean) {
        ColorPickerDialog.Builder(requireContext())
            .setTitle(getString(R.string.pick_a_color_title))
            .setPositiveButton(getString(R.string.select_button_hint), object: ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    envelope?.also {
                        viewModel.currentUserId.value?.let {_ ->
                            saveChosenColorToSharedPrefs(envelope.color, myColor)
                        }
                    }
                }

            })
            .setNegativeButton(getString(R.string.cancel_button_hint)) { dialog, _ ->
                dialog.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)
            .show()
    }
    private fun setPhoto() {
        viewModel.mainPhotoBitmap.observe(viewLifecycleOwner) {bitmap ->
            binding.mainPhoto.setImageBitmap(bitmap)
        }

        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            viewModel.loadImage(uri, binding.mainPhoto)
        }
    }

    private fun setInfoAboutUser(user: User) {
        binding.myName.text = user.fullName
        binding.login.text = user.login
        binding.myEmail.text = user.email
    }

    private fun getInfoAboutUser() {
        viewModel.currentFirebaseUser.observe(viewLifecycleOwner) {firebaseUser ->
            setInfoAboutUser(firebaseUser)
        }

        viewModel.currentRoomUser.observe(viewLifecycleOwner) {roomUser ->
            roomUser.apply {
                val user = User(fullName, email, login, firebaseUserId, mutableListOf(),
                    mutableListOf(), mutableListOf(), false
                )

                setInfoAboutUser(user)
            }
        }
    }

    private fun setNavigationBarOnItemListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                //R.id.friends -> findNavController().navigate(R.id.friends_fragment)
                //R.id.chats -> findNavController().navigate(R.id.chats_fragment)
               // R.id.settings -> findNavController().navigate(R.id.settings_fragment)
            }
            true
        }
    }
}