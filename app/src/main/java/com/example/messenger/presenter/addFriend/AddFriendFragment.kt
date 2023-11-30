package com.example.messenger.presenter.addFriend

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.example.messenger.databinding.DialogProfileDetailsBinding
import com.example.messenger.databinding.FragmentAddFriendBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class AddFriendFragment : Fragment() {
    @Inject lateinit var viewModel: AddFriendViewModel
    lateinit var adapter : FriendsSearchAdapter
    private lateinit var binding: FragmentAddFriendBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFriendBinding.inflate(layoutInflater)
        injectDependencies()
        addLoginRequestChangedListener()
        observeFoundUsers()
        observeDownloadedImages()
        initializeRecyclerViewAdapter()

        return binding.root
    }

    private fun injectDependencies() {
        (requireActivity().application as MyApp).appComponent.inject(this)
    }

    private fun observeDownloadedImages() {
        viewModel.images.observe(viewLifecycleOwner) {images ->
            adapter.setImages(images)
        }
    }
    private fun observeFoundUsers() {
        viewModel.foundUsers.observe(viewLifecycleOwner) {users ->
            adapter.setFoundUsers(users)
            viewModel.downloadImages(users)
        }
    }

    private fun addLoginRequestChangedListener() {
        binding.friendsSearch.addTextChangedListener {loginQuery ->
            viewModel.searchUserByLogin(loginQuery.toString())
        }
    }

    private fun initializeRecyclerViewAdapter() {
        adapter = FriendsSearchAdapter(viewModel::loadImage, this::createDialogWithProfileDetails)
        binding.foundUsers.adapter = adapter
        binding.foundUsers.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun disableAddFriendButton(view: DialogProfileDetailsBinding) {
        view.addToFriendsButton.visibility = View.GONE
        view.requestSent.visibility = View.VISIBLE
    }

    private fun sendFriendRequest(clickedUser: User) {
        // add current user's uId to clicked user's receivedFriendRequests
        viewModel.currentUserId.value?.let { id ->
            clickedUser.receivedFriendRequests += id

            // update clickedUser in database
            viewModel.updateUser(clickedUser)
        }
    }

    private fun createDialogWithProfileDetails(clickedUser: User, photoUri: Uri) {
        val view = DialogProfileDetailsBinding.inflate(layoutInflater)

        view.fullName.text = clickedUser.fullName
        view.login.text = clickedUser.login
        view.friends.text =
            getString(R.string.dialog_friends_quantity_text, clickedUser.friends.size)

        viewModel.loadImage(photoUri, view.mainPhoto)

        // checking if friend request has been already sent to this user
        viewModel.currentUserId.value?.let { id ->
            if (clickedUser.receivedFriendRequests.contains(id)) {
                disableAddFriendButton(view)
            } else {
                view.addToFriendsButton.setOnClickListener {
                    sendFriendRequest(clickedUser)
                    disableAddFriendButton(view)
                }
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(view.root)
            .show()
    }
}