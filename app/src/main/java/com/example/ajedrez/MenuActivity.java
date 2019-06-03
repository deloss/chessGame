package com.example.ajedrez;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MenuActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("uid-username");
        if (user != null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName = dataSnapshot.child(user.getUid()).getValue().toString();
                    mostrarPantalla();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void nuevaPartidaOffline(View v) {
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);
    }

    public void cerrarSesion(View v) {
        FirebaseAuth.getInstance().signOut();
        user = null;
        mostrarPantalla();
    }


    public void nuevaPartidaOnline(View v) {
        if (user == null) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MenuActivity.this);
            alert.setTitle("No hay sesión iniciada");
            alert.setMessage("Debes iniciar sesión para jugar online");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createSignInIntent();
                }
            });
            alert.setNegativeButton("Cancel", null);
            alert.show();
        }else{
            Intent intent = new Intent(this, OfflineGameActivity.class);
            startActivity(intent);
        }
    }

    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.hasChild(user.getUid())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                            final EditText input = new EditText(MenuActivity.this);
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            builder.setView(input);
                            builder.setTitle("Ingresa un nombre de usuario");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    userName = input.getText().toString();
                                    mDatabase.child(user.getUid()).setValue(userName);
                                    mostrarPantalla();
                                }
                            });
                            builder.show();
                        }else
                            mostrarPantalla();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void setUserNameDisplay() {
        TextView userDisplay = findViewById(R.id.user_name);
        if (user == null) {
            userDisplay.setText("");
            userDisplay.setVisibility(View.GONE);
        }else{
            userDisplay.setText("Hello, \n" + userName + "!");
            userDisplay.setVisibility(View.VISIBLE);
        }
    }

    public void mostrarPantalla() {
        setUserNameDisplay();
        Button logOut = findViewById(R.id.logOut);
        if (user == null)
            logOut.setVisibility(View.GONE);
        else
            logOut.setVisibility(View.VISIBLE);
    }
}
