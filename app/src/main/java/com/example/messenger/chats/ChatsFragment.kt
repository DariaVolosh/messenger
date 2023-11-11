package com.example.messenger.chats

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.addFriend.AddFriendFragment
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentChatsBinding
import com.example.messenger.friendsAndRequests.FriendsFragment
import com.example.messenger.messages.MessagesFragment
import com.example.messenger.settings.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ChatsFragment : Fragment(), ChatsAdapter.MessageDisplayListener {
    private lateinit var binding: FragmentChatsBinding
    lateinit var adapter: ChatsAdapter
    private var orientation = 0
    @Inject lateinit var app: MyApp
    @Inject lateinit var viewModel: ChatsViewModel
    @Inject lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(layoutInflater)
        orientation = resources.configuration.orientation

        injectDependencies()
        observeChatList()
        observeImages()
        observeMainPhoto()
        observeLastMessages()
        setNavigationBarOnItemListener()
        initializeAdapter()
        setFabAddFriendButtonListener()
        setSignOutButtonListener()

        return binding.root
    }

    private fun observeLastMessages() {
        viewModel.lastMessages.observe(viewLifecycleOwner) {messages ->
            adapter.setLastMessages(messages)
        }
    }
    private fun observeChatList() {
        viewModel.chatList.observe(viewLifecycleOwner) { users ->
            adapter.setChatsList(users)
        }
    }

    private fun observeImages() {
        viewModel.photoUris.observe(viewLifecycleOwner) {list ->
            adapter.setImages(list)
        }
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)
    }

    private fun observeMainPhoto() {
        viewModel.mainPhotoBitmap.observe(viewLifecycleOwner) {bitmap ->
            binding.mainPhoto.setImageBitmap(bitmap)
        }

        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            Glide.with(requireContext())
                .load(uri)
                .into(binding.mainPhoto)
        }
    }

    private fun setFabAddFriendButtonListener() {
        binding.fabAddFriend.setOnClickListener {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val addFriendFragment = AddFriendFragment()
                childFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment, addFriendFragment)
                    .commitNow()
            } else {
                findNavController().navigate(R.id.add_friends_fragment)
            }
        }
    }

    private fun setSignOutButtonListener() {
        binding.signOut.setOnClickListener {
            firebaseAuth.signOut()
            findNavController().navigate(R.id.sign_in_fragment)
        }
    }

    private fun initializeAdapter() {
        // manually inject adapter, because i need to pass a listener to it
        adapter = ChatsAdapter(
            requireContext(),
            this)
        binding.chatsList.adapter = adapter
        binding.chatsList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setNavigationBarOnItemListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {item ->
            when (item.itemId) {
                R.id.friends -> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        val friendsFragment = FriendsFragment()
                        childFragmentManager.beginTransaction()
                            .replace(R.id.details_fragment, friendsFragment)
                            .commitNow()
                    } else {
                        findNavController().navigate(R.id.friends_fragment)
                    }
                }
                R.id.chats -> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        val fragment = childFragmentManager.findFragmentById(R.id.details_fragment)
                        fragment?.let {
                            childFragmentManager.beginTransaction()
                                .remove(fragment)
                                .commitNow()
                        }
                    } else {
                        findNavController().navigate(R.id.chats_fragment)
                    }
                }
                R.id.settings -> {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        val settingsFragment = SettingsFragment()
                        childFragmentManager.beginTransaction()
                            .replace(R.id.details_fragment, settingsFragment)
                            .commitNow()
                    } else {
                        findNavController().navigate(R.id.settings_fragment)
                    }
                }
            }
            true
        }
    }

    override fun onDisplayMessages(friend: User) {
        val bundle = bundleOf()
        bundle.putString(MessagesFragment.FRIEND_UID, friend.userId)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val messagesFragment = MessagesFragment()
            childFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, messagesFragment)
                .commitNow()
        } else {
            findNavController().navigate(R.id.messages_fragment, bundle)
        }
    }
}
