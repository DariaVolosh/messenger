package com.example.messenger.chats

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.ViewModelFactory
import com.example.messenger.databinding.FragmentChatsBinding


class ChatsFragment : Fragment(){
    lateinit var app : MyApp
    private lateinit var binding: FragmentChatsBinding
    private lateinit var adapter: ChatsAdapter
    private lateinit var viewModel: ChatsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)
        val factory = ViewModelFactory(app, ChatsViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[ChatsViewModel::class.java]

        if (!viewModel.mainPhotoUri.isInitialized) { viewModel.downloadImage() }

        viewModel.chatList.observe(viewLifecycleOwner) { users ->
            adapter.setChatList(users)
        }

        setNavigationBarOnItemListener()
        initializeAdapter()
        loadImage()

        binding.fabAddFriend.setOnClickListener {
            findNavController().navigate(R.id.add_friends_fragment)
        }

        binding.signOut.setOnClickListener {
            app.auth.signOut()
            findNavController().navigate(R.id.sign_in_fragment)
        }

        return binding.root
    }

    private fun initializeAdapter() {
        adapter =
            ChatsAdapter(findNavController(), viewModel, requireContext(), viewLifecycleOwner)
        binding.chatsList.adapter = adapter
        binding.chatsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setNavigationBarOnItemListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.friends -> findNavController().navigate(R.id.friends_fragment)
            }
            true
        }
    }

    private fun loadImage() {
        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            Glide.with(requireContext())
                .load(uri)
                .into(binding.mainPhoto)
        }
    }
}
