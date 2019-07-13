package com.example.momen.e_commerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.momen.e_commerceapp.Model.Users;
import com.example.momen.e_commerceapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button joinNow,login;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        String userPhone = Paper.book().read(Prevalent.userPhone);
        String userPassword = Paper.book().read(Prevalent.userPassword);

        if (!TextUtils.isEmpty(userPhone)&&!TextUtils.isEmpty(userPassword)&&userPhone != ""&&userPassword!=""){
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("please wait....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(userPhone,userPassword);
        }

        joinNow = findViewById(R.id.main_join_now_btn);
        login = findViewById(R.id.main_login_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        joinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void AllowAccessToAccount(final String phone, final String pass) {


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    Users users = dataSnapshot.child("Users").child(phone).getValue(Users.class);
                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(pass)){
                            Toast.makeText(MainActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.myAccount = users;
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Account with this number don't exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
