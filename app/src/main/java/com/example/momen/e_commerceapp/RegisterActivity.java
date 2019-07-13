package com.example.momen.e_commerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userName,password,mobilePhone;
    Button registerBtn;
    ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.register_userName_input);
        password = findViewById(R.id.register_password_input);
        mobilePhone = findViewById(R.id.register_phone_number_input);
        registerBtn = findViewById(R.id.register_btn);

        loadingBar = new ProgressDialog(this);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userName.getText().toString();
                String phone = mobilePhone.getText().toString();
                String pass = password.getText().toString();
                if (TextUtils.isEmpty(name)){
                    userName.setError("can't be Empty");
                }else if (TextUtils.isEmpty(phone)){
                    mobilePhone.setError("can't be Empty");
                }else if (TextUtils.isEmpty(pass)){
                    password.setError("can't be Empty");
                }else{
                    loadingBar.setTitle("create Account");
                    loadingBar.setMessage("please wait, while we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    validatePhoneNumber(name,phone,pass);
                }

            }
        });

    }

    private void validatePhoneNumber(final String name, final String phone, final String pass) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
             if (!(dataSnapshot.child("Users").child(phone).exists())){
                 HashMap<String,Object> user = new HashMap<>();
                 user.put("phone",phone);
                 user.put("password",pass);
                 user.put("name",name);
                 rootRef.child("Users").child(phone).updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isComplete()) {
                             Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created.", Toast.LENGTH_SHORT).show();
                             loadingBar.dismiss();
                             Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                             startActivity(intent);
                         }
                         else {
                             loadingBar.dismiss();
                             Toast.makeText(RegisterActivity.this, "Error while registration, try again", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }
             else {
                 mobilePhone.setError("this "+phone+" already exist");
                 loadingBar.dismiss();
             }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingBar.dismiss();
    }
}