package com.example.momen.e_commerceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momen.e_commerceapp.Model.Users;
import com.example.momen.e_commerceapp.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    CheckBox rememberMe;
    EditText mobilePhone,password;
    TextView forgetPass,admin,notAdmin;
    Button loginBtn;
    ProgressDialog loadingBar;
    String parentChild = "Users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobilePhone = findViewById(R.id.login_phone_number_input);
        password = findViewById(R.id.login_password_input);

        forgetPass = findViewById(R.id.forget_pass);
        admin = findViewById(R.id.admin);
        notAdmin = findViewById(R.id.not_admin);

        loginBtn = findViewById(R.id.login_btn);

        rememberMe = findViewById(R.id.remember_me_chkb);

        Paper.init(this);

        loadingBar = new ProgressDialog(this);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login Admin");
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentChild = "Admins";
            }
        });

        notAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                admin.setVisibility(View.VISIBLE);
                notAdmin.setVisibility(View.INVISIBLE);
                parentChild = "Users";
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mobilePhone.getText().toString();
                String pass = password.getText().toString();
                if (TextUtils.isEmpty(phone)){
                    mobilePhone.setError("can't be Empty");
                }else if (TextUtils.isEmpty(pass)){
                    password.setError("can't be Empty");
                }else{
                    loadingBar.setTitle("Login Account");
                    loadingBar.setMessage("please wait, while we are checking the credentials.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    AllowAccessToAccount(phone,pass);
                }
            }
        });

    }

    private void AllowAccessToAccount(final String phone, final String pass) {

        if (rememberMe.isChecked()){
            Paper.book().write(Prevalent.userPhone,phone);
            Paper.book().write(Prevalent.userPassword,pass);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(parentChild).child(phone).exists()){
                   Users users = dataSnapshot.child(parentChild).child(phone).getValue(Users.class);

                   if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(pass)){
                            if (parentChild.equals("Users")) {
                                Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                                Prevalent.myAccount = users;
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Account with this number don't exist", Toast.LENGTH_SHORT).show();
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
