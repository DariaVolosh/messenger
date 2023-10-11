package com.example.messenger.friends

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.data.User
import com.example.messenger.messages.MessagesFragment
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView

class FriendsListAdapter(private val navController: NavController,
                         private val viewModel: FriendsViewModel,
                         private val lifecycleOwner: LifecycleOwner,
                         private val context: Context):
    RecyclerView.Adapter<FriendsListAdapter.ViewHolder>() {

    private var friends = listOf<User>()
    private var images = listOf<Uri>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val login: TextView = view.findViewById(R.id.login)
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
        val textButton: MaterialButton = view.findViewById(R.id.send_message_button)
    }

    fun areListsEqual(images: List<Uri>): Boolean {
        if (this.images.size == images.size) {
            val sorted1 = this.images.sorted()
            val sorted2 = images.sorted()
            for (i in sorted1.indices) {
                if (sorted1[i] != sorted2[i]) {
                    return false
                }
            }
        } else {
            return false
        }

        return true
    }

    init {
        viewModel.friendsList.observe(lifecycleOwner) { users ->
            this.friends = users

            viewModel.downloadImages(this.friends, true)
            viewModel.friendsImages.observe(lifecycleOwner) {images ->
                // checking if a new list of images does not equal an old one to make sure only the
                // new value is emitted
                if (!areListsEqual(images)) {
                    this.images = images
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_friend, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = friends[position]
        holder.name.text = user.fullName
        holder.login.text = user.login
        loadImage(holder, images[position])

        holder.textButton.setOnClickListener {openChat(friends[position].userId)}
    }

    private fun loadImage(holder: ViewHolder, uri: Uri) {
        Glide.with(context)
            .load(uri)
            .into(holder.mainPhoto)
    }

    private fun openChat(uId: String) {
        val bundle = bundleOf()
        bundle.putString(MessagesFragment.FRIEND_UID, uId)
        navController.navigate(R.id.messages_fragment, bundle)
    }

    override fun getItemCount() = friends.size
}