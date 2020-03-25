package com.example.whatsappclone2.modals

import com.example.whatsappclone2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_message_row.view.*

class LatestMessageItem(val child: ChatMessage?) : Item<ViewHolder>() {


           var chatparteneruser:User?=null
           var chatpartenerid:String?=null



    override fun getLayout(): Int {
        return R.layout.activity_latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latestmessage.text = child!!.text

        if (child!!.fromid == FirebaseAuth.getInstance().uid) {

              chatpartenerid=child?.toid
        } else {
             chatpartenerid= child?.fromid
        }
        val ref = FirebaseDatabase.getInstance().getReference("/user/$chatpartenerid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                 chatparteneruser = p0.getValue(User::class.java)
                viewHolder.itemView.latestusername.text = chatparteneruser?.username
                Picasso.get().load(chatparteneruser?.profileimgurl).into(viewHolder.itemView.userimage)
            }

        })
    }
}