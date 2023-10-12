package com.example.messenger.addFriend

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.MyApp
import com.example.messenger.ViewModelFactory
import com.example.messenger.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment() {
    lateinit var app : MyApp
    private lateinit var adapter : FriendsSearchAdapter
    private lateinit var binding: FragmentAddFriendBinding
    private lateinit var viewModel: AddFriendViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        app = requireActivity().application as MyApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddFriendBinding.inflate(layoutInflater)
        val factory = ViewModelFactory(app, AddFriendViewModel::class.java)
        viewModel = ViewModelProvider(this, factory)[AddFriendViewModel::class.java]

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
        adapter = FriendsSearchAdapter(requireContext(), layoutInflater, viewModel, viewLifecycleOwner)
        binding.friends.adapter = adapter
        binding.friends.layoutManager = LinearLayoutManager(requireContext())
    }
}