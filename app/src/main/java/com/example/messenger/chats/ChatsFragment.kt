package com.example.messenger.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.databinding.FragmentChatsBinding
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ChatsFragment : Fragment(){
    private lateinit var binding: FragmentChatsBinding
    @Inject lateinit var app: MyApp
    @Inject lateinit var adapter: ChatsAdapter
    @Inject lateinit var viewModel: ChatsViewModel
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)

        viewModel.downloadImage(viewLifecycleOwner)
        viewModel.mainPhotoBitmap.observe(viewLifecycleOwner) {bitmap ->
            binding.mainPhoto.setImageBitmap(bitmap)
        }
        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            Glide.with(requireContext())
                .load(uri)
                .into(binding.mainPhoto)
        }


        viewModel.chatList.observe(viewLifecycleOwner) { users ->
            adapter.setChatList(users)
        }

        setNavigationBarOnItemListener()
        initializeAdapter()

        binding.fabAddFriend.setOnClickListener {
            findNavController().navigate(R.id.add_friends_fragment)
        }

        binding.signOut.setOnClickListener {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.sign_in_fragment)
        }

        return binding.root
    }

    private fun initializeAdapter() {
        binding.chatsList.adapter = adapter
        binding.chatsList.layoutManager = LinearLayoutManager(requireContext())
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
