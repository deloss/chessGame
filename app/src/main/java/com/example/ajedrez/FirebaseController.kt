package com.example.ajedrez

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

object FirebaseController {

    private val UID_USERNAME_DB = "uid-username"
    private val GAMES_DB = "game_match"

    private var mDatabase: DatabaseReference? = null
    private var user: FirebaseUser? = null

    init {
        user = FirebaseAuth.getInstance().currentUser
        mDatabase = FirebaseDatabase.getInstance().reference.child(UID_USERNAME_DB)
    }

    fun getUsersOnline(activity : GameRoomActivity, userList : ArrayList<String>) {
        mDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userList.clear()
                for (userData in dataSnapshot.children) {
                    if (userData.key != user?.getUid()) {
                        userList.add(userData.value!!.toString())
                    }
                }
                activity.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun invitePlayer(activity : GameRoomActivity, username : String) {
        var matchName = user!!.getUid() + "-" + username
        val gamesDb = FirebaseDatabase.getInstance().reference.child(GAMES_DB)
        gamesDb.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot : DataSnapshot) {
        val posibleMatchName = username + "-" + user!!.getUid()
        val fuiInvitado = dataSnapshot.child(posibleMatchName).getValue() != null
        if (fuiInvitado)
            matchName = posibleMatchName
        val game = gamesDb.child(matchName)
        if (fuiInvitado) {
            game.setValue(2L)
            activity.iniciarPartida()
        } else {
            game.setValue(1L)
            game.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.value != null) { //por testear en consola
                        if (dataSnapshot.value as Long? == 2L) {
                            activity.iniciarPartida()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

    }

            override fun onCancelled(databaseError: DatabaseError) {}
        });
        println("Click")
    }
}


