package com.melvin.quickalert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaCodec;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.Authenticator;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity  {
    public TextView tv_registration;
    public EditText et_firstName, et_secondName, et_userName, et_phoneNumber,et_address, et_password;
    public Button btn_register, btn_login;
    ProgressDialog progressDialog;

    
    //creating variable for authentication
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        tv_registration = findViewById(R.id.tv_registration_id);
        et_firstName = findViewById(R.id.et_first_name_id);
        et_secondName = findViewById(R.id.et_second_name_id);
        et_userName = findViewById(R.id.et_User_name_id);
        et_phoneNumber = findViewById(R.id.et_phone_number_id);
        et_address = findViewById(R.id.et_email_address_id);
        btn_register = findViewById(R.id.btn_register_id);
        et_password = findViewById(R.id.password);
        btn_login = findViewById(R.id.btn_login1);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);






        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { 
                registerUser();

                

            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });


    }

    private void registerUser() 
    {
        String firstName = et_firstName.getText().toString().trim();
        String secondName = et_secondName.getText().toString().trim();
        String userName = et_userName.getText().toString().trim();
        String phoneNumber = et_phoneNumber.getText().toString().trim();
        String emailAddress = et_address.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        
        if(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(secondName)
                && TextUtils.isEmpty(userName) && TextUtils.isEmpty(phoneNumber) && TextUtils.isEmpty(emailAddress)
                && TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Fill all spaces", Toast.LENGTH_SHORT).show();
        }
        else
        {
            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            //Register user
            mAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        UserDetails userDetails = new UserDetails(firstName, secondName,userName,phoneNumber,emailAddress, password);
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        String userId = task.getResult().getUser().getUid();
                        databaseReference.child("Users").child(userId).setValue(userDetails);
                        progressDialog.dismiss();

                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));


                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });


        }


    }
        
    


}