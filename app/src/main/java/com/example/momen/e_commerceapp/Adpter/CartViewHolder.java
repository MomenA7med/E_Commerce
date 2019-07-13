package com.example.momen.e_commerceapp.Adpter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.momen.e_commerceapp.ItemClickListner;
import com.example.momen.e_commerceapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cartProductName, cartProductQuantity, cartProductPrice;
    public ItemClickListner listner;


    public CartViewHolder(View itemView)
    {
        super(itemView);

        cartProductName =  itemView.findViewById(R.id.cart_product_name);
        cartProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        cartProductPrice = itemView.findViewById(R.id.cart_product_price);
    }

    public void setItemClickListner(ItemClickListner listner)
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View view)
    {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
