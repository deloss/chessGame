package com.example.ajedrez;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ajedrez.Logica.RecyclerViewItemOnClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class GameRoomActivity extends AppCompatActivity {
    private FirebaseUser user; //here its assumed that user is not null
    private DatabaseReference mDatabase;
    private TextView usersCount;
    private RecyclerView recyclerView;
    private UserGameRoomAdapter mAdapter;
    private ArrayList<String> userList;
    private DatabaseReference gamesDb;
    private String matchName;
    private boolean invitacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_room);
        userList = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("uid-username");
        recyclerView = findViewById(R.id.my_recycler_view);
        gamesDb = FirebaseDatabase.getInstance().getReference().child("Games");
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // specify an adapter (see also next example)
        mAdapter = new UserGameRoomAdapter(userList, new RecyclerViewItemOnClickListener() {
            @Override
            public void onClick(View v, int position) {
                matchName = user.getUid() + "-" + userList.get(position);

                gamesDb.child(matchName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long valueMatch;
                        if (dataSnapshot.getValue() == null) { // i made the invite
                            valueMatch = 0L;
                            invitacion = true;
                        }
                        else
                            valueMatch = (Long)dataSnapshot.getValue();
                        if (valueMatch == 0L)
                            gamesDb.child(matchName).setValue(1L);
                        else
                            gamesDb.child(matchName).setValue(2L);
                        gamesDb.child(matchName).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) { //probar en la consola da problemas
                                        System.out.println(dataSnapshot.getValue());
                                        if ((Long) dataSnapshot.getValue() == 2L) {
                                            System.out.println("los dos hicieron click");
                                            if (invitacion) {
                                                gamesDb.child(matchName).removeValue();
                                                invitacion = false;
                                            }
                                            iniciarPartida();
                                        }
                                    }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                System.out.println("Click");
            }
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        getUsersOnline();
    }

    private void getUsersOnline() {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userList.clear();
                    for (DataSnapshot userData: dataSnapshot.getChildren()) {
                        if (!userData.getKey().equals(user.getUid())) {
                            userList.add(userData.getValue().toString());
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    //usersCount.setText(String.valueOf(dataSnapshot.getChildrenCount())); // just testing
                    //mostrarPantalla(); aca hay que cargar los datos
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }



    private void iniciarPartida() {
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);
    }
}
