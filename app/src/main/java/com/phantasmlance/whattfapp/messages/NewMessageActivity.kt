package com.phantasmlance.whattfapp.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.phantasmlance.whattfapp.AddContactActivity
import com.phantasmlance.whattfapp.R
import com.phantasmlance.whattfapp.models.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_newmessage.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "New message"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fetchUsers()
    }

    companion object {
        const val userKey = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                snapshot.children.forEach {
                    Log.d("New", it.toString())

                    val user = it.getValue(Users::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(userKey, userItem.users)
                    startActivity(intent)

                    finish()
                }

                recyclerViewNewMessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_contact -> {
                val intent = Intent(this, AddContactActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

class UserItem(val users: Users) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        Picasso.get().load(users.profileImageUrl).into(viewHolder.itemView.imageAvatar)
        viewHolder.itemView.textUsername.text = users.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_newmessage
    }
}