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
import javax.inject.Inject

class FriendsRequestsAdapter @Inject constructor(
    private val context: Context,
    private val viewModel: FriendsViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val setRequestsTextListener: SetRequestsText
) :
    RecyclerView.Adapter<FriendsRequestsAdapter.ViewHolder>() {
    private var receivedRequests = mutableListOf<User>()
    private var requestsImages = listOf<Uri>()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val button: ImageButton = view.findViewById(R.id.add_to_friends_button)
        val name: TextView = view.findViewById(R.id.name)
        val login: TextView = view.findViewById(R.id.login)
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
    }

    fun setImages(requestsImages: List<Uri>) {
        this.requestsImages = requestsImages
    }

    fun setUsers(receivedRequests: MutableList<User>) {
        this.receivedRequests = receivedRequests
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
        loadImage(requestsImages[position], holder)
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
            setRequestsTextListener.setText(receivedRequests.size)
        }
    }

    override fun getItemCount() = receivedRequests.size

    interface SetRequestsText {
        fun setText(size: Int)
    }
}