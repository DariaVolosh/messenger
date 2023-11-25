package com.example.messenger.addFriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.databinding.FragmentAddFriendBinding
import javax.inject.Inject

class AddFriendFragment : Fragment() {
    @Inject lateinit var adapter : FriendsSearchAdapter
    @Inject lateinit var viewModel: AddFriendViewModel
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
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)
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
        binding.foundUsers.adapter = adapter
        binding.foundUsers.layoutManager = LinearLayoutManager(requireContext())
    }
}