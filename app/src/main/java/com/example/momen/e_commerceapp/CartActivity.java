package com.example.momen.e_commerceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.momen.e_commerceapp.Adpter.CartViewHolder;
import com.example.momen.e_commerceapp.Model.Cart;
import com.example.momen.e_commerceapp.Prevalent.Prevalent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    RecyclerView cartList;
    TextView totalPrice,msg;
    Button next;
    private int overTotalPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartList = findViewById(R.id.cart_list);
        totalPrice = findViewById(R.id.total_price);
        next = findViewById(R.id.next_btn);
        msg = findViewById(R.id.msg);
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartList.setHasFixedSize(false);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrice.setText("Total Price = "+String.valueOf(overTotalPrice)+" $");

                Intent intent = new Intent(CartActivity.this,ConfirmFinalOrderActivity.class);
                intent.putExtra("final",String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCartState();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(reference.child("User View").child(Prevalent.myAccount.getPhone())
                .child("Products"),Cart.class).build();
        FirebaseRecyclerAdapter<Cart, CartViewHolder> adpter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {
                        holder.cartProductName.setText(model.getPname());
                        holder.cartProductQuantity.setText("Quantity = "+model.getQuantity());
                        holder.cartProductPrice.setText("Price : "+model.getPrice()+"$");
                        int oneProductPrice = Integer.parseInt(model.getQuantity()) + Integer.parseInt(model.getPrice());
                        overTotalPrice += oneProductPrice;
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]{"Edit","Remove"};
                                AlertDialog.Builder builder= new AlertDialog.Builder(CartActivity.this);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            Intent intent = new Intent(CartActivity.this,ProductDetailsActivity.class);
                                            intent.putExtra("pId",model.getPid());
                                            startActivity(intent);
                                        }
                                        else if (which == 1){
                                            reference.child("User View")
                                                    .child(Prevalent.myAccount.getPhone())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(CartActivity.this, "item removed successfully.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                        return new CartViewHolder(view);
                    }
                };
        cartList.setAdapter(adpter);
        adpter.startListening();

    }

    private void checkCartState(){
        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.myAccount.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String state = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    if (state.equals("shipped")){
                        totalPrice.setText("Dear : "+userName+" order is shipped");
                        cartList.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                    } else if (state.equals("not shipped")){
                        totalPrice.setText("order not shipped");
                        cartList.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
