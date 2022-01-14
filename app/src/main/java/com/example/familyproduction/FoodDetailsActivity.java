package com.example.familyproduction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.familyproduction.sqlite.CartItemsDB;
import com.example.familyproduction.sqlite.Myappdatabas;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.DecimalFormat;

import static java.security.AccessController.getContext;

public class FoodDetailsActivity extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    Toolbar toolbar;

    FloatingActionButton mAddToCartBtn;

    String name, image, description, ownerName, category;
    double price;
    int ownerId = -1;
    int id;

    ImageView mImage;
    TextView mCategory, mDescription, mOwnerName, mPrice;

    Myappdatabas myappdatabas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        bindViews();

        myappdatabas = Myappdatabas.getDatabase(this);

        Intent sender = getIntent();
        if(sender != null){
            id = sender.getIntExtra("id", -1);
            name = sender.getStringExtra("name");
            image = sender.getStringExtra("image");
            description = sender.getStringExtra("description");
            ownerName = sender.getStringExtra("ownerName");
            category = sender.getStringExtra("category");
            price = sender.getDoubleExtra("price", -1);
            ownerId = sender.getIntExtra("owner_id", -1);
        }

        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.bright_black));
        setSupportActionBar(toolbar);


        updateUI();

        if(ownerId == -1){
            mAddToCartBtn.setVisibility(View.GONE);
        }else {
            mAddToCartBtn.setVisibility(View.VISIBLE);
        }

        mAddToCartBtn.setOnClickListener(v -> {
            LayoutInflater factory = LayoutInflater.from(this);
            final View view = factory.inflate(R.layout.add_food_to_cart_dialog, null);
            final AlertDialog addProductDialog = new AlertDialog.Builder(this).create();
            addProductDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            addProductDialog.setView(view);
            addProductDialog.setCanceledOnTouchOutside(false);

            ImageButton add = view.findViewById(R.id.add);
            ImageButton sub = view.findViewById(R.id.subtract);
            TextView quantity = view.findViewById(R.id.quantity);
            TextView total = view.findViewById(R.id.price);
            TextView save = view.findViewById(R.id.save);
            TextView cancel = view.findViewById(R.id.cancel);

            add.setOnClickListener(v1 -> {
                String quantityText = quantity.getText().toString();
                int quantityInt = Integer.parseInt(quantityText);
                quantityInt++;
                double totalPrice = price * quantityInt;
                quantity.setText(String.valueOf(quantityInt));
                total.setText(new DecimalFormat("##.##").format(totalPrice));
            });

            sub.setOnClickListener(v1 -> {
                String quantityText = quantity.getText().toString();
                int quantityInt = Integer.parseInt(quantityText);
                if(quantityInt == 0)
                    return;
                quantityInt--;
                double totalPrice = price * quantityInt;
                quantity.setText(String.valueOf(quantityInt));
                total.setText(new DecimalFormat("##.##").format(totalPrice));
            });


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String quantityText = quantity.getText().toString();
                    double totalPrice = Double.parseDouble(total.getText().toString());

                    int quantityInt = Integer.parseInt(quantityText);
                    //checking if quantity is empty
                    if (TextUtils.isEmpty(quantityText) || (quantityInt == 0)) {
                        Toast.makeText(FoodDetailsActivity.this, "please add at least one item!", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    CartItemsDB item = new CartItemsDB();
                    item.setId(id);
                    item.setPrice(totalPrice);
                    item.setQuantity(quantityInt);
                    item.setName(name);
                    item.setOwnerId(ownerId);
                    myappdatabas.myDao().addItem(item);

                    addProductDialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductDialog.dismiss();
                }
            });
            addProductDialog.show();
        });


    }

    private void updateUI() {

        if(image == null){
            Glide.with(this)
                    .load(getResources().getDrawable(R.drawable.food1))
                    .into(mImage);
        }else{
            Glide.with(this)
                    .load(image)
                    .into(mImage);
        }

        collapsingToolbar.setTitle(name);
        mOwnerName.setText(ownerName);
        mCategory.setText(category);
        mDescription.setText(description);
        mPrice.setText(String.valueOf(price));
    }

    private void bindViews() {
        toolbar =   findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.toolbar_layout);
        mAddToCartBtn = findViewById(R.id.add_to_cart);

        mImage = findViewById(R.id.header);
        mOwnerName = findViewById(R.id.owner_name);
        mCategory = findViewById(R.id.category);
        mDescription = findViewById(R.id.description);
        mPrice = findViewById(R.id.price);

    }


}