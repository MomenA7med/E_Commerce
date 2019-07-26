package com.example.momen.e_commerceapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.momen.e_commerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    EditText nameEt,phoneEt,addressEt,cityEt;
    Button confirmBtn;
    String finalPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        nameEt = findViewById(R.id.shipment_name);
        phoneEt = findViewById(R.id.shipment_phone);
        addressEt = findViewById(R.id.shipment_address);
        cityEt = findViewById(R.id.shipment_city);

        confirmBtn = findViewById(R.id.confirm_btn);

        finalPrice = getIntent().getStringExtra("final");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(nameEt.getText().toString())||TextUtils.isEmpty(phoneEt.getText().toString())||
                        TextUtils.isEmpty(addressEt.getText().toString())||TextUtils.isEmpty(cityEt.getText().toString())){
                    if (TextUtils.isEmpty(nameEt.getText().toString()))
                        nameEt.setError("can't be empty");
                    if (TextUtils.isEmpty(phoneEt.getText().toString()))
                        phoneEt.setError("can't be empty");
                    if (TextUtils.isEmpty(addressEt.getText().toString()))
                        addressEt.setError("can't be empty");
                    if (TextUtils.isEmpty(cityEt.getText().toString()))
                        cityEt.setError("can't be empty");
                }else {
                    String saveCurrentTime,saveCurrendDate;
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
                    saveCurrendDate = currentDate.format(calForDate.getTime());

                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
                    saveCurrentTime = currentTime.format(calForDate.getTime());

                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                            .child("Orders")
                            .child(Prevalent.myAccount.getPhone());

                    final HashMap<String,Object> orderMap = new HashMap<>();
                    orderMap.put("totalPrice",finalPrice);
                    orderMap.put("name",nameEt.getText().toString());
                    orderMap.put("phone",phoneEt.getText().toString());
                    orderMap.put("date",saveCurrendDate);
                    orderMap.put("time",saveCurrentTime);
                    orderMap.put("address",addressEt.getText().toString());
                    orderMap.put("city",cityEt.getText().toString());
                    orderMap.put("state","not shipped");


                    reference.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("Cart List")
                                        .child("User View")
                                        .child(Prevalent.myAccount.getPhone())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ConfirmFinalOrderActivity.this, "done", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });
    }
}
