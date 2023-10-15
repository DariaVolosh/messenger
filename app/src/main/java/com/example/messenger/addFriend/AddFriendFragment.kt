package com.example.messenger.addFriend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
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
        (requireActivity().application as MyApp).appComponent.create(
            requireContext(),
            layoutInflater,
            viewLifecycleOwner,
            findNavController()
        ).inject(this)

        initializeRecyclerViewAdapter()

        binding.friendsSearch.addTextChangedListener {loginQuery ->
            if (loginQuery.toString() != viewModel.previousQuery) {
                viewModel.searchUserByLogin(loginQuery.toString())
            }
        }

        viewModel.foundUsers.observe(viewLifecycleOwner) {users ->
            adapter.setData(users)
        }

        return binding.root
    }

    private fun initializeRecyclerViewAdapter() {
        binding.friends.adapter = adapter
        binding.friends.layoutManager = LinearLayoutManager(requireContext())
    }
}