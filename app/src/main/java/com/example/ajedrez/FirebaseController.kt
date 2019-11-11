package com.example.ajedrez

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

object FirebaseController {

    private val UID_USERNAME_DB = "uid-username"
    private val MATCH_GAMES_DB = "game_match"
    private val GAMES_DB = "games"

    private var mDatabase: DatabaseReference? = null
    private var matchGamesDb: DatabaseReference? = null
    private var gamesDb: DatabaseReference? = null
    private var user: FirebaseUser? = null
    private var myTurn : Int = -1
    private var matchName : String = ""
    private var myTurnAux : Int = -1

    init {
        user = FirebaseAuth.getInstance().currentUser
        mDatabase = FirebaseDatabase.getInstance().reference.child(UID_USERNAME_DB)
        matchGamesDb = FirebaseDatabase.getInstance().reference.child(MATCH_GAMES_DB)
        gamesDb = FirebaseDatabase.getInstance().reference.child(GAMES_DB)
    }

    fun getUsersOnline(activity: GameRoomActivity, userList: ArrayList<String>) {
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

    fun invitePlayer(activity: GameRoomActivity, username: String) {
        matchName = user!!.getUid() + "-" + username

        matchGamesDb!!.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posibleMatchName = username + "-" + user!!.getUid()
                val fuiInvitado = dataSnapshot.child(posibleMatchName).value != null
                myTurn = if (fuiInvitado)
                    2
                else
                    1
                // BORRAR DSP
                myTurnAux = myTurn
                ///
                if (fuiInvitado)
                    matchName = posibleMatchName
                val game = matchGamesDb!!.child(matchName)
                if (fuiInvitado) {
                    game.setValue(2L)
                    activity.iniciarPartida(myTurn)
                } else {
                    game.setValue(1L)
                    game.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.value != null) { //por testear en consola
                                if (dataSnapshot.value as Long? == 2L) {
                                    gamesDb!!.child(matchName).child("turn").setValue(myTurn)
                                    activity.iniciarPartida(myTurn)
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {

                        }
                    })
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        println("Click")
    }

    fun turnListener(activity : OnlineGameActivity) {
        val gameDb = gamesDb!!.child(matchName)
        gameDb.child("turn").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.value == myTurn.toLong()) {
                    activity.setItsMyTurn(true)
                    println("es mi turno")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun terminarTurno(activity : OnlineGameActivity) {
        gamesDb!!.child(matchName).child("turn").setValue(getTurnoOpuestoAux())
        activity.setItsMyTurn(false)
        println("ya no es mi turno")
    }

    private fun getTurnoOpuestoAux() : Int {
        if (myTurnAux == 1)
            myTurnAux = 2
        else
           myTurnAux = 1
        return myTurnAux
    }

    private fun getTurnoOpuesto() : Int {
        return if (myTurn == 1)
            2
        else
            1
    }

}


