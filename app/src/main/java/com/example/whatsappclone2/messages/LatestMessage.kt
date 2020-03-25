package com.example.whatsappclone2.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration

import com.example.whatsappclone2.R
import com.example.whatsappclone2.loginregister.MainActivity
import com.example.whatsappclone2.modals.ChatMessage
import com.example.whatsappclone2.modals.LatestMessageItem
import com.example.whatsappclone2.modals.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.activity_latest_message.view.*
import kotlinx.android.synthetic.main.activity_latest_message_row.view.*

class LatestMessage : AppCompatActivity() {


    companion object {
        var currentuser: User? = null
    }
     var chatpartenerid: ChatMessage?=null
    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        val uid = FirebaseAuth.getInstance().uid

        isUserLoggedIn(uid)
        fetchCurrentUser()
        adapter.setOnItemClickListener { item, view ->
            val intent=Intent(this,ChatLogActivity::class.java)
            val row=item as LatestMessageItem
            intent.putExtra(NewMessageActivity.key,row.chatparteneruser)
            startActivity(intent)
        }
        recycler_view_latest_message.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recycler_view_latest_message.adapter = adapter

        ListenForLatestMessages()
    }

    private fun ListenForLatestMessages() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest_messages/$uid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val child = p0.getValue(ChatMessage::class.java)
                adapter.add(LatestMessageItem(child))
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentuser = p0.getValue(User::class.java)
            }

        })
    }





    private fun isUserLoggedIn(uid: String?) {
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Signout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            R.id.newmessage -> {
                val intent = Intent(
                    this,
                    NewMessageActivity::class.java
                )
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
