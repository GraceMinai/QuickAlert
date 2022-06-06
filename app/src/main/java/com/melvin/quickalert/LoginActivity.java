package com.melvin.quickalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Authenticator;

public class LoginActivity extends AppCompatActivity {
    public TextView tv_signIn, tv_signUp, tv_orSignWith;
    public EditText et_emailAddress, et_password;
    public MaterialButton mb_loggIn;
    public ImageView iv_google, iv_facebook, iv_twitter;

    //Creating variables for the Database reference
    DatabaseReference myDatabaseReference;

    //creating variable for authentication
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        tv_signIn = findViewById(R.id.tv_sign_in);
        tv_signUp = findViewById(R.id.tv_signup);
        tv_orSignWith = findViewById(R.id.tv_other);
        et_emailAddress = findViewById(R.id._et_email);
        et_password = findViewById(R.id.et_password);
        mb_loggIn = findViewById(R.id.btn_login);
        iv_google = findViewById(R.id.google);
        iv_facebook = findViewById(R.id.fb);
        iv_twitter = findViewById(R.id.twitter);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);


        mb_loggIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               loginUser();


            }
        });
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = (new Intent(LoginActivity.this, RegistrationActivity.class));
                startActivity(intent);
            }
        });

        if (mAuth.getCurrentUser()!= null)
        {
            startActivity(new Intent(LoginActivity.this, MapActivity.class));
        }
    }

    private void loginUser() {

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loging in...");
        progressDialog.show();
        String emailAddress = et_emailAddress.getText().toString();
        String password = et_password.getText().toString();

        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful())
                {
                    startActivity(new Intent(LoginActivity.this, MapActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}
