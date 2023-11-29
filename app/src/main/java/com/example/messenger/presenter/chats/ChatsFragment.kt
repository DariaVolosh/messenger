package com.example.messenger.presenter.chats

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.example.messenger.databinding.FragmentChatsBinding
import com.example.messenger.presenter.addFriend.AddFriendFragment
import com.example.messenger.presenter.friendsAndRequests.FriendsFragment
import com.example.messenger.presenter.messages.MessagesFragment
import com.example.messenger.presenter.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject

class ChatsFragment : Fragment(), ChatsAdapter.MessageDisplayListener, ChatsAdapter.LoadImage {
    private lateinit var binding: FragmentChatsBinding
    private lateinit var adapter: ChatsAdapter
    private var orientation = 0
    @Inject lateinit var viewModel: ChatsViewModel

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
        (requireActivity().application as MyApp).appComponent.inject(this)
    }

    private fun observeMainPhoto() {
        viewModel.mainPhotoBitmap.observe(viewLifecycleOwner) {bitmap ->
            binding.mainPhoto.setImageBitmap(bitmap)
        }

        viewModel.mainPhotoUri.observe(viewLifecycleOwner) {uri ->
            viewModel.loadImage(uri, binding.mainPhoto)
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

    private fun initializeAdapter() {
        // manually inject adapter, because i need to pass a listener to it
        adapter = ChatsAdapter(
            this, this)
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
                        friendsFragment
                            .view?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                            ?.visibility = View.GONE
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
                        settingsFragment
                            .view?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
                            ?.visibility = View.GONE
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
            messagesFragment.arguments = bundle
            childFragmentManager.beginTransaction()
                .replace(R.id.details_fragment, messagesFragment)
                .commitNow()
        } else {
            findNavController().navigate(R.id.messages_fragment, bundle)
        }
    }

    override fun loadImage(uri: Uri, imageView: ImageView) {
        viewModel.loadImage(uri, imageView)
    }
}
