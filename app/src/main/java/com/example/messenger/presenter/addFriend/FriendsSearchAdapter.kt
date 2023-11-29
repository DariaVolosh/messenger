package com.example.messenger.presenter.addFriend

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.data.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class FriendsSearchAdapter @Inject constructor(
    private val context: Context,
    private val viewModel: AddFriendViewModel
) :
    RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {

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

        viewModel.loadImage(images[position], holder.mainPhoto)
        holder.view.setOnClickListener { createDialogWithProfileDetails(user, position, holder.view)}
    }

    private fun disableAddFriendButton(view: View) {
        view.findViewById<Button>(R.id.add_to_friends_button).visibility = View.GONE
        view.findViewById<TextView>(R.id.request_sent).visibility = View.VISIBLE
    }

    private fun createDialogWithProfileDetails(clickedUser: User, position: Int, viewGroup: View) {
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_profile_details, viewGroup as ViewGroup, false
        )

        view.findViewById<TextView>(R.id.full_name).text = clickedUser.fullName
        view.findViewById<TextView>(R.id.login).text = clickedUser.login
        view.findViewById<TextView>(R.id.friends).text =
            context.getString(R.string.dialog_friends_quantity_text, clickedUser.friends.size)

        viewModel.loadImage(images[position], view.findViewById(R.id.main_photo))

        // checking if friend request has been already sent to this user
        viewModel.currentUserId.value?.let { id ->
            if (clickedUser.receivedFriendRequests.contains(id)) {
                disableAddFriendButton(view)
            } else {
                view.findViewById<Button>(R.id.add_to_friends_button).setOnClickListener {
                    sendFriendRequest(clickedUser)
                    disableAddFriendButton(view)
                }
            }
        }

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .show()
    }

    private fun sendFriendRequest(clickedUser: User) {
        // add current user's uId to clicked user's receivedFriendRequests
        viewModel.currentUserId.value?.let { id ->
            clickedUser.receivedFriendRequests += id
        }

        // update clickedUser in database
        viewModel.updateUser(clickedUser)
    }

    override fun getItemCount() = foundUsers.size
}