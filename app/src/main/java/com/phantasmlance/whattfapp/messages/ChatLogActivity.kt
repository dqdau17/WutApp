package com.phantasmlance.whattfapp.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.phantasmlance.whattfapp.MainActivity
import com.phantasmlance.whattfapp.R
import com.phantasmlance.whattfapp.models.Messages
import com.phantasmlance.whattfapp.models.Users
import com.phantasmlance.whattfapp.views.ChatFromItem
import com.phantasmlance.whattfapp.views.ChatToItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerViewChat.adapter = adapter

        toUser = intent.getParcelableExtra(NewMessageActivity.userKey)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        buttonSend.setOnClickListener {
            if (editTextMessage.text.isNotEmpty()) {
                performSendMessage()
            } else {
                Log.d(TAG, "Attempt to send empty message...")
            }
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Messages::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        MainActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }

                recyclerViewChat.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = editTextMessage.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<Users>(NewMessageActivity.userKey)
        val toId = user?.uid

        if (fromId == null) return

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage =
            Messages(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                editTextMessage.text.clear()
                recyclerViewChat.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}
