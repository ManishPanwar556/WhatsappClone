package com.example.whatsappclone2.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone2.R
import com.example.whatsappclone2.modals.User

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
//import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_user_new_message.*
import kotlinx.android.synthetic.main.row_user.view.*

class NewMessageActivity : AppCompatActivity() {
companion object{
    val key="APP_KEY"
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_new_message)
        supportActionBar?.title = "Select user"
        val adapter = GroupAdapter<ViewHolder>()

//        rev.adapter=adapter
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(
                            UserItem(
                                user
                            )
                        )
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem =item as UserItem
                    val intent= Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(key,userItem.user)
                    startActivity(intent)
                    finish()
                }
                rev.adapter = adapter
            }

        })
    }

    class UserItem(val user: User) : Item<ViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.row_user
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.username.text = user.username
            Picasso.get().load(user.profileimgurl).into(viewHolder.itemView.imageView)
        }

    }
}

