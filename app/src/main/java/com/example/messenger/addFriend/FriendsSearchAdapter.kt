package com.example.messenger.addFriend

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.databinding.DialogProfileDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class FriendsSearchAdapter @Inject constructor(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val viewModel: AddFriendViewModel) :
    RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {

    private val currentUId = viewModel.getCurrentUserId()
    private var foundUsers = listOf<User>()
    private var images = listOf<Uri>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val login: TextView = view.findViewById(R.id.login)
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.holder_found_user,
                parent,
                false
            )
        )
    }

    fun setFoundUsers(users: List<User>) {
        this.foundUsers = users
    }

    fun setImages(images: List<Uri>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = foundUsers[position]
        holder.name.text = user.fullName
        holder.login.text = user.login

        loadImage(images[position], holder)
        holder.view.setOnClickListener { createDialogWithProfileDetails(user, position)}
    }

    private fun loadImage(uri: Uri, holder: ViewHolder) {
        Glide.with(context)
            .load(uri)
            .into(holder.mainPhoto)
    }

    private fun disableAddFriendButton(view: DialogProfileDetailsBinding) {
        view.addToFriendsButton.visibility = View.GONE
        view.requestSent.visibility = View.VISIBLE
    }

    private fun createDialogWithProfileDetails(clickedUser: User, position: Int) {
        val view = DialogProfileDetailsBinding.inflate(layoutInflater)

        view.fullName.text = clickedUser.fullName
        view.login.text = clickedUser.login
        view.friends.text = "${clickedUser.friends.size} friends"

        Glide.with(context)
            .load(images[position])
            .into(view.mainPhoto)

        // checking if friend request has been already sent to this user
        if (clickedUser.receivedFriendRequests.contains(currentUId)) {
            disableAddFriendButton(view)
        } else {
            view.addToFriendsButton.setOnClickListener {
                sendFriendRequest(clickedUser)
                disableAddFriendButton(view)
            }
        }

        MaterialAlertDialogBuilder(context)
            .setView(view.root)
            .show()
    }

    private fun sendFriendRequest(clickedUser: User) {
        // add current user's uId to clicked user's receivedFriendRequests
        clickedUser.receivedFriendRequests += currentUId!!

        // update clickedUser in database
        viewModel.updateUser(clickedUser)
    }

    override fun getItemCount() = foundUsers.size
}