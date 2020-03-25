package com.example.whatsappclone2.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.whatsappclone2.R
import com.example.whatsappclone2.modals.ChatMessage
import com.example.whatsappclone2.modals.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_log_fromuser.view.*
import kotlinx.android.synthetic.main.activity_chat_log_touser.view.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()
    lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        user = intent.getParcelableExtra<User>(NewMessageActivity.key)


        supportActionBar?.title = user.username


        listenformessages()
        send_button.setOnClickListener {
            performSendOperation()
        }
    }

    private fun listenformessages() {
        val fromid = FirebaseAuth.getInstance().uid
        val toid = user.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user_message/$toid/$fromid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                val getchild = p0.getValue(ChatMessage::class.java)
                val user = intent.getParcelableExtra<User>(NewMessageActivity.key)
                if (getchild?.fromid == FirebaseAuth.getInstance().uid) {
                    adapter.add(ChatFromItem(getchild!!.text, LatestMessage.currentuser!!))
                } else {
                    adapter.add(ChattoItem(getchild!!.text, user))
                }
                chatrecyclerview.adapter = adapter
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    private fun performSendOperation() {
        val text = message_block.text.toString()
        val user = intent.getParcelableExtra<User>(NewMessageActivity.key)
        val toid = user.uid

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val fromid = FirebaseAuth.getInstance().uid
        val reference =
            FirebaseDatabase.getInstance().getReference("/user_message/$toid/$fromid").push()
        val toreference =
            FirebaseDatabase.getInstance().getReference("/user_message/$fromid/$toid").push()

        if (fromid != null) {
            val chatMessage =
                ChatMessage(
                    reference.key!!,
                    toid,
                    fromid,
                    text,
                    System.currentTimeMillis() / 1000
                )
            reference.setValue(chatMessage).addOnSuccessListener {
                message_block.text.clear()
                chatrecyclerview.scrollToPosition(adapter.itemCount - 1)

            }
            toreference.setValue(chatMessage)
            val latestMessage =
                FirebaseDatabase.getInstance().getReference("/latest_messages/$fromid/$toid")
            latestMessage.setValue(chatMessage)
            val latesttomessage =
                FirebaseDatabase.getInstance().getReference("/latest_messages/$toid/$fromid")
            latesttomessage.setValue(chatMessage)
        }

    }

    class ChattoItem(val text: String, val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.activity_chat_log_touser
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.usertochat.text = text
            Picasso.get().load(user.profileimgurl).into(viewHolder.itemView.imageView2)
        }

    }

    class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.activity_chat_log_fromuser
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.userfromtextView.text = text
            Picasso.get().load(user.profileimgurl).into(viewHolder.itemView.fromuserimage)
        }

    }
}
