package com.phantasmlance.whattfapp.views

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.phantasmlance.whattfapp.R
import com.phantasmlance.whattfapp.models.Messages
import com.phantasmlance.whattfapp.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(private val messages: Messages) : Item<ViewHolder>() {
    var chatPartnerUser: Users? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatPartnerId: String = if (messages.fromId == FirebaseAuth.getInstance().uid) {
            messages.toId
        } else {
            messages.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(Users::class.java)
                Picasso.get().load(chatPartnerUser?.profileImageUrl)
                    .into(viewHolder.itemView.imageViewMain)
                viewHolder.itemView.textUsernameMain.text = chatPartnerUser?.username
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        viewHolder.itemView.textMessageMain.text = messages.text
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

}