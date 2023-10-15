package com.example.messenger.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentFriendsBinding
import javax.inject.Inject

class FriendsFragment: Fragment(R.layout.fragment_friends), FriendsRequestsAdapter.SetRequestsText {
    private lateinit var binding : FragmentFriendsBinding
    private lateinit var friendsRequestsAdapter: FriendsRequestsAdapter
    @Inject lateinit var viewModel: FriendsViewModel
    @Inject lateinit var friendsListAdapter: FriendsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(layoutInflater)
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)

        setFriendRequestsQuantity()
        setNavigationBarOnItemListener()
        initializeFriendRequestsAdapter()

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
        viewModel.getUsersFromUId(currentUser.friends, true)
        binding.friendsList.adapter = friendsListAdapter
        binding.friendsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initializeFriendRequestsAdapter() {
        friendsRequestsAdapter = FriendsRequestsAdapter(
            requireContext(),
            viewModel,
            viewLifecycleOwner,
            this)
        binding.friendRequestsList.adapter = friendsRequestsAdapter
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

    override fun setText(size: Int) {
        binding.friendRequestsText.text = "You have $size new requests"
    }
}