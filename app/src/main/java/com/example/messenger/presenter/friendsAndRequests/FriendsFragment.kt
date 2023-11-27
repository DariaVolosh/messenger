package com.example.messenger.presenter.friendsAndRequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentFriendsBinding
import com.example.messenger.presenter.messages.MessagesFragment
import javax.inject.Inject

class FriendsFragment: Fragment(R.layout.fragment_friends) {
    private lateinit var binding : FragmentFriendsBinding
    @Inject lateinit var viewModel: FriendsViewModel
    @Inject lateinit var friendsAndRequestsAdapter: DataAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        injectDependencies()
        setNavigationBarOnItemListener()
        initializeAdapter()

        viewModel.currentUser.observe(viewLifecycleOwner) {currentUser ->
            setData(currentUser)
        }

        viewModel.chatOpened.observe(viewLifecycleOwner) {chatOpened ->
            if (chatOpened) {
                val bundle = bundleOf()
                bundle.putString(MessagesFragment.FRIEND_UID, viewModel.friend.value?.userId)
                findNavController().navigate(R.id.messages_fragment, bundle)
            }
        }

        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater
        ).inject(this)
    }

    private fun initializeAdapter() {
        binding.friendsAndFriendsRequestsList.adapter = friendsAndRequestsAdapter
        binding.friendsAndFriendsRequestsList.layoutManager = LinearLayoutManager(requireContext())
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

    private fun setData(currentUser: User) {
        val friends = currentUser.friends
        val requests = currentUser.receivedFriendRequests

        val friendsAndRequests = friends + requests

        viewModel.getUsersFromUId(friendsAndRequests)
        viewModel.friendsAndRequestsList.observe(viewLifecycleOwner) { requestsAndFriends ->
            viewModel.getDataModelListFromUserList(requestsAndFriends)
        }

        viewModel.dataModelList.observe(viewLifecycleOwner) { dataModelList ->
            friendsAndRequestsAdapter.setFriendsAndRequestsList(dataModelList)
        }

        viewModel.images.observe(viewLifecycleOwner) {images ->
            friendsAndRequestsAdapter.setImages(images)
        }
    }
}