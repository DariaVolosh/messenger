package com.example.messenger.messages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.MyApp
import com.example.messenger.ViewModelFactory
import com.example.messenger.databinding.FragmentMessagesBinding
import com.example.messenger.data.Message

class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var app: MyApp
    private lateinit var friendId: String
    private lateinit var currentUserUId: String
    private lateinit var adapter: MessagesAdapter
    private lateinit var viewModel: MessagesViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    companion object {
        const val FRIEND_UID = "friend_uId"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(layoutInflater)
        val factory = ViewModelFactory(app, MessagesViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[MessagesViewModel::class.java]

        initializeIds()
        populateToolbarWithFriendInfo()

        viewModel.getExistingMessagesApp(friendId)
        viewModel.existingMessagesPath.observe(viewLifecycleOwner) {path ->
            initializeAdapter()
        }

        binding.sendMessageButton.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun listenForMessages() {
        viewModel.messages.observe(viewLifecycleOwner) {messages ->
            binding.messages.scrollToPosition(messages.size - 1)
            adapter.setData(messages)
        }
    }

    private fun initializeIds() {
        friendId = requireArguments().getString(FRIEND_UID)!!
        currentUserUId = viewModel.getCurrentUserId()!!
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
            Glide.with(requireContext())
                .load(uri)
                .into(binding.mainPhoto)
        }
    }

    private fun initializeAdapter() {
        listenForMessages()
        adapter = MessagesAdapter(currentUserUId)
        binding.messages.adapter = adapter
        binding.messages.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun sendMessage() {
        val messageId = viewModel.existingMessagesPath.value?.push()?.key!!
        val message = Message(
            System.currentTimeMillis(), binding.messageEditText.text.toString(),
            currentUserUId, friendId, messageId
        )
        viewModel.sendMessage(message, messageId)
        binding.messageEditText.text.clear()
        viewModel.addChatToChatsList(friendId)
    }
}