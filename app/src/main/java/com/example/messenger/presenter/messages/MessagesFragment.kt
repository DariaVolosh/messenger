package com.example.messenger.presenter.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.data.Message
import com.example.messenger.data.User
import com.example.messenger.databinding.FragmentMessagesBinding
import javax.inject.Inject

class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var friendId: String
    private lateinit var adapter: MessagesAdapter
    @Inject lateinit var viewModel: MessagesViewModel

    companion object {
        const val FRIEND_UID = "friend_uId"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(layoutInflater)

        injectDependencies()
        initializeIds()
        getCurrentMessagesPath()
        populateToolbarWithFriendInfo()

        binding.sendMessageButton.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater
        ).inject(this)
    }

    private fun getCurrentMessagesPath() {
        var currentUser = User(
            "","","","", mutableListOf(), mutableListOf(), mutableListOf()
        )
        viewModel.currentUser.observe(viewLifecycleOwner) {user ->
            currentUser = user
            viewModel.getExistingMessagesPath(friendId)
        }
        viewModel.existingMessagesPath.observe(viewLifecycleOwner) {
            initializeAdapter()
            listenForMessages(currentUser)
            viewModel.addMessagesListener()
        }
    }

    private fun listenForMessages(user: User) {
        viewModel.messages.observe(viewLifecycleOwner) {messages ->
            binding.messages.scrollToPosition(messages.size - 1)
            adapter.setData(messages, user)
        }
    }

    private fun initializeIds() {
        requireArguments().getString(FRIEND_UID)?.let { fetchedFriendId ->
            friendId = fetchedFriendId
        }
    }

    private fun populateToolbarWithFriendInfo() {
        setFullName()
        downloadPhoto()
    }

    private fun setFullName() {
        viewModel.getUserObjectById(friendId)
        viewModel.friendObject.observe(viewLifecycleOwner) {user ->
            binding.name.text = user.fullName
        }
    }

    private fun downloadPhoto() {
        viewModel.downloadFriendMainPhoto(friendId)
        viewModel.friendPhotoUri.observe(viewLifecycleOwner) {uri ->
            viewModel.loadImage(uri, binding.mainPhoto)
        }
    }

    private fun initializeAdapter() {
        viewModel.currentUser.observe(viewLifecycleOwner) {user ->
            val myColor = viewModel.getMessagesColor("myColor${user.userId}")
            val friendsColor = viewModel.getMessagesColor("friendsColor${user.userId}")
            adapter = MessagesAdapter(myColor, friendsColor)
            binding.messages.adapter = adapter
            binding.messages.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun sendMessage() {
        val messageId = viewModel.existingMessagesPath.value?.push()?.key
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val message = Message(
                System.currentTimeMillis(), binding.messageEditText.text.toString(),
                user.userId, friendId, messageId ?: ""
            )
            viewModel.sendMessage(message)
            binding.messageEditText.text.clear()
            viewModel.addChatToChatsList(friendId)
        }
    }
}