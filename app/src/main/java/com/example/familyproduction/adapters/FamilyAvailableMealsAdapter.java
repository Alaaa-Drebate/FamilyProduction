package com.example.familyproduction.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.familyproduction.FoodDetailsActivity;
import com.example.familyproduction.R;
import com.example.familyproduction.model.Food;

import java.util.ArrayList;

public class FamilyAvailableMealsAdapter extends RecyclerView.Adapter<FamilyAvailableMealsAdapter.ViewHolder>{

    Context context;
    private ArrayList<Food> items;


    public FamilyAvailableMealsAdapter(Context context, ArrayList<Food> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.food_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Food item = items.get(position);

        String imageUrl = item.getImage();

        if(imageUrl == null){
            Glide.with(context)
                    .load(context.getResources().getDrawable(R.drawable.food2))
                    .into(holder.image);
        }else{
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.image);
        }

        holder.name.setText(item.getName());

        holder.price.setText("" + item.getPrice() + " " + context.getResources().getString(R.string.price_unit));

        holder.description.setText(item.getDescription());


        holder.itemView.setOnClickListener(v -> {

            Intent foodDetails = new Intent(context, FoodDetailsActivity.class);
            foodDetails.putExtra("id", item.getId());
            foodDetails.putExtra("name", item.getName());
            foodDetails.putExtra("price", item.getPrice());
            foodDetails.putExtra("description", item.getDescription());
            foodDetails.putExtra("image", item.getImage());
            foodDetails.putExtra("category", item.getCategory());
            foodDetails.putExtra("ownerName", item.getOwnerName());
            context.startActivity(foodDetails);

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
        public TextView description;
        public TextView price;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.image);
            this.name = itemView.findViewById(R.id.name);
            this.description = itemView.findViewById(R.id.description);
            this.price = itemView.findViewById(R.id.price);
        }
    }
}
