package com.example.momen.e_commerceapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.momen.e_commerceapp.Model.Products;
import com.example.momen.e_commerceapp.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    ImageView productImg;
    Button addToCartBtn;
    ElegantNumberButton numberBtn;
    TextView productName, productDescription, productPrice;
    DatabaseReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImg = findViewById(R.id.product_image_details);
        addToCartBtn = findViewById(R.id.pd_add_to_cart_button);
        numberBtn = findViewById(R.id.number_btn);
        productName = findViewById(R.id.product_name_details);
        productDescription = findViewById(R.id.product_description_details);
        productPrice = findViewById(R.id.product_price_details);

        final String pId = getIntent().getStringExtra("pId");

        ref = FirebaseDatabase.getInstance().getReference().child("Products").child(pId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);
                    Picasso.with(ProductDetailsActivity.this).load(products.getImage()).into(productImg);
                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    productPrice.setText(products.getPrice());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String saveCurrentTime,saveCurrendDate;
                Calendar calForDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
                saveCurrendDate = currentDate.format(calForDate.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
                saveCurrentTime = currentTime.format(calForDate.getTime());

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart List");

                final HashMap<String,Object> cartMap = new HashMap<>();
                cartMap.put("pid",pId);
                cartMap.put("pname",productName.getText().toString());
                cartMap.put("price",productPrice.getText().toString());
                cartMap.put("date",saveCurrendDate);
                cartMap.put("time",saveCurrentTime);
                cartMap.put("quantity",numberBtn.getNumber());
                cartMap.put("discount","");

                reference.child("User View").child(Prevalent.myAccount.getPhone())
                        .child("Products").child(pId)
                        .updateChildren(cartMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    reference.child("Admin View").child(Prevalent.myAccount.getPhone())
                                            .child("Products").child(pId)
                                            .updateChildren(cartMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ProductDetailsActivity.this, "add to cart list ..", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });
    }
}