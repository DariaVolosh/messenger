package com.example.messenger.chats

import android.content.Context
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
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter(private val navController: NavController,
                   private val viewModel: ChatsViewModel,
                   private val context: Context,
                   private val lifecycleOwner: LifecycleOwner):
    RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {
    private var chats = listOf<User>()

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val mainPhoto: CircleImageView = view.findViewById(R.id.main_photo)
        val name: TextView = view.findViewById(R.id.name)
        val message: TextView = view.findViewById(R.id.message)
    }

    fun setChatList(newChats: List<User>) {
        this.chats = newChats
        viewModel.downloadImages(newChats)
        viewModel.photoUris.observe(lifecycleOwner) {
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_chat, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = chats[position]
        holder.name.text = friend.fullName

        Glide.with(context)
            .load(viewModel.photoUris.value?.get(position))
            .into(holder.mainPhoto)

        holder.view.setOnClickListener {
            val bundle = bundleOf()
            bundle.putString(MessagesFragment.FRIEND_UID, friend.userId)
            navController.navigate(R.id.messages_fragment, bundle)
        }

        viewModel.listenForNewMessage(friend.userId, holder)
    }
    override fun getItemCount() = chats.size
}