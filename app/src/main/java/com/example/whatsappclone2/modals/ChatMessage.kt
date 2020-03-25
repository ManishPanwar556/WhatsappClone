package com.example.whatsappclone2.modals

data class ChatMessage(val id:String,val toid:String,val fromid:String,val text:String,val timestamp:Long){
    constructor():this("","","","",-1)
}
