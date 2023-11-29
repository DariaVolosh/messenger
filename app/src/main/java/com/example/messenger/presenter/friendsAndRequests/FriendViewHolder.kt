package com.example.messenger.presenter.friendsAndRequests

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.example.messenger.R
import com.example.messenger.data.model.User
import de.hdodenhof.circleimageview.CircleImageView

class FriendViewHolder(itemView: View, private val viewModel: FriendsViewModel):
    DataAdapterViewHolder(itemView) {

    fun bind(item: DataModel.Friend, photoUri: Uri) {
        val mainPhoto: CircleImageView = itemView.findViewById(R.id.main_photo)
        val name: TextView = itemView.findViewById(R.id.name)
        val login: TextView = itemView.findViewById(R.id.login)
        val button: TextView = itemView.findViewById(R.id.send_message_button)

        name.text = item.name
        login.text = item.login
        viewModel.loadImage(photoUri, mainPhoto)
        button.setOnClickListener {openChat(item.userId)}
    }

    private fun openChat(uId: String) {
        viewModel.getUserObjectById(uId)
        itemView.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
            val observer = object : Observer<User> {
                override fun onChanged(user: User) {
                    viewModel.openChat()
                    viewModel.friend = MutableLiveData<User>()
                    viewModel.friend.removeObserver(this)
                }
            }

            viewModel.friend.observe(lifecycleOwner, observer)
        }
    }
}