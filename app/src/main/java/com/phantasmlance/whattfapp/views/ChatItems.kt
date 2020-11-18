package com.phantasmlance.whattfapp.views

import com.phantasmlance.whattfapp.R
import com.phantasmlance.whattfapp.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, private val user: Users) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textChatFrom.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageAvatarChat
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatFromItem(val text: String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textChatTo.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}