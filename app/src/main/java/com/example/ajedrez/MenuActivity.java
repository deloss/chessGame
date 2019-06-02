package com.example.ajedrez;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void nuevaPartidaOffline(View v) {
        Intent intent = new Intent(this, OfflineGameActivity.class);
        startActivity(intent);
    }

    public void cerrarSesion(View v) {
        FirebaseAuth.getInstance().signOut();
        user = null;
        setUserNameDisplay(user);
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
                setUserNameDisplay(user);

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    public void setUserNameDisplay(FirebaseUser user) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView userDisplay = findViewById(R.id.user_name);
        if (user == null) {
            userDisplay.setText("");
            userDisplay.setVisibility(View.GONE);
        }else{
            userDisplay.setText(user.getDisplayName());
            userDisplay.setVisibility(View.VISIBLE);
        }
    }
}
