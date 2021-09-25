package com.vamsigutha.saranghae;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class Login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    int AUTHUI_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
//            handleLoginSignup();
        }
    }

    public void initiateLogin(View view){
        handleLoginSignup();
//        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
    }


    public void handleLoginSignup(){
        List<AuthUI.IdpConfig> provider = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(provider)
                .setIsSmartLockEnabled(false)
                .setLogo(R.drawable.cat)
                .build();

        startActivityForResult(intent, AUTHUI_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AUTHUI_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }else{
                IdpResponse response = IdpResponse.fromResultIntent(data);

                if(response == null){
                    Log.d(TAG,"user cancelled the sign in request");

                }else{
                    Log.d(TAG,""+response.getError());
                }
                finish();
            }
        }
    }
}