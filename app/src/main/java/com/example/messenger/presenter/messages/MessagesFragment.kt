package com.example.messenger.presenter.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.model.Message
import com.example.messenger.data.model.User
import com.example.messenger.databinding.FragmentMessagesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesFragment : Fragment() {
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var friendId: String
    private lateinit var adapter: MessagesAdapter
    private lateinit var onlineStatus: Job
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
        listenForFriendOnlineStatus()

        binding.sendMessageButton.setOnClickListener { sendMessage() }

        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent?.inject(this)
    }

    private fun getCurrentMessagesPath() {
        var currentUser = User()
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
            CoroutineScope(Dispatchers.Main).launch {
                val myColor = viewModel.getMessagesColor("myColor${user.userId}")
                val friendsColor = viewModel.getMessagesColor("friendsColor${user.userId}")
                adapter = MessagesAdapter(myColor, friendsColor)
                binding.messages.adapter = adapter
                binding.messages.layoutManager = LinearLayoutManager(requireContext())
            }
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

    private fun listenForFriendOnlineStatus() {
        viewModel.onlineStatus.observe(viewLifecycleOwner) {list ->
            viewModel.emitOnlineStatus(list)
            onlineStatus = CoroutineScope(Dispatchers.Main).launch {
                viewModel.getFlowById(friendId).collect {online ->
                    binding.onlineStatus.apply {
                        if (online) {
                            text = getString(R.string.active_now)
                            setTextColor(requireContext().getColor(R.color.green))
                        } else {
                            text = getString(R.string.offline)
                            setTextColor(requireContext().getColor(R.color.red))
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onlineStatus.cancel()
    }
}