package com.example.messenger.friends

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.data.User
import de.hdodenhof.circleimageview.CircleImageView

class FriendsRequestsAdapter(
    private val context: Context,
    private val friendRequestsText: TextView,
    private val viewModel: FriendsViewModel,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<FriendsRequestsAdapter.ViewHolder>() {
    private var receivedRequests = mutableListOf<User>()
    private var images = listOf<Uri>()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val button: ImageButton = view.findViewById(R.id.add_to_friends_button)
        val name: TextView = view.findViewById(R.id.name)
        val login: TextView = view.findViewById(R.id.login)
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
    }

    init {
        viewModel.friendRequests.observe(lifecycleOwner) {requests ->
            viewModel.getUsersFromUId(requests, false)
            viewModel.requestsList.observe(lifecycleOwner) { users ->
                this.receivedRequests = users

                viewModel.downloadImages(users, false)
                viewModel.requestsImages.observe(lifecycleOwner) {images ->
                    this.images = images
                    notifyDataSetChanged()
                    friendRequestsText.text = "You have ${receivedRequests.size} new requests"
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_friend_request, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = receivedRequests[position]
        holder.button.setOnClickListener { addFriend(user.userId) }
        holder.name.text = user.fullName
        holder.login.text = user.login
        loadImage(images[position], holder)
    }

    private fun loadImage(uri: Uri, holder: ViewHolder) {
        Glide.with(context)
            .load(uri)
            .into(holder.mainPhoto)
    }

    private fun addFriend(clickedUserId: String) {
        val currentUser = viewModel.currentUser.value
        currentUser!!.receivedFriendRequests.remove(clickedUserId)
        currentUser.friends += clickedUserId
        viewModel.updateUser(currentUser)

        viewModel.getUserObjectById(clickedUserId)
        viewModel.friend.observe(lifecycleOwner){friend ->
            friend.friends += currentUser.userId
            viewModel.updateUser(friend)
            viewModel.getUsersFromUId(currentUser.friends, true)
            val updatedList = receivedRequests.filter { it.userId != clickedUserId }
            receivedRequests.clear()
            receivedRequests.addAll(updatedList)
            notifyDataSetChanged()
            friendRequestsText.text = "You have ${receivedRequests.size} new requests"
        }
    }

    override fun getItemCount() = receivedRequests.size
}