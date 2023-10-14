package com.example.messenger.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.ViewModelFactory
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    lateinit var app : MyApp
    private lateinit var binding : FragmentFriendsBinding
    private lateinit var viewModel: FriendsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val factory = ViewModelFactory(app, FriendsViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[FriendsViewModel::class.java]
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        setFriendRequestsQuantity()
        setNavigationBarOnItemListener()

        return binding.root
    }

    private fun setFriendRequestsQuantity() {
        viewModel.currentUser.observe(viewLifecycleOwner) {currentUser ->
            binding.friendRequestsText.text =
                "You have ${currentUser.receivedFriendRequests.size} new requests"
            initializeFriendsListAdapter(currentUser)
        }
    }

    private fun initializeFriendsListAdapter(currentUser: User) {
        val adapter =
            FriendsListAdapter(findNavController(), viewModel, viewLifecycleOwner, requireContext())
        viewModel.getUsersFromUId(currentUser.friends, true)
        binding.friendsList.adapter = adapter
        binding.friendsList.layoutManager = LinearLayoutManager(requireContext())
        initializeFriendRequestsAdapter()
    }

    private fun initializeFriendRequestsAdapter() {
        val adapter =
            FriendsRequestsAdapter(requireContext(), binding.friendRequestsText,
                viewModel, viewLifecycleOwner)
        binding.friendRequestsList.adapter = adapter
        binding.friendRequestsList.layoutManager = LinearLayoutManager(requireContext())
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