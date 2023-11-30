package com.example.messenger.presenter.addFriend

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.data.model.User
import de.hdodenhof.circleimageview.CircleImageView

class FriendsSearchAdapter(
    private val loadImage: (Uri, ImageView) -> Unit,
    private val createDialog: (User, Uri) -> Unit
): RecyclerView.Adapter<FriendsSearchAdapter.ViewHolder>() {

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

        loadImage(images[position], holder.mainPhoto)
        holder.view.setOnClickListener { createDialog(user, images[position]) }
    }

    override fun getItemCount() = foundUsers.size
}