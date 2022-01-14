package com.example.familyproduction.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyproduction.FoodByCategoryActivity;
import com.example.familyproduction.R;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>{

    Context context;
    private ArrayList<String> categories;
    private final static String KEY_CATEGORY = "category";

    public CategoriesAdapter(Context context, ArrayList<String> categories) {
        this.context = context;
        this.categories = categories;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.category_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String category = categories.get(position);

        holder.name.setText(category);

        holder.itemView.setOnClickListener(v -> {
            Intent foodByCategory = new Intent(context, FoodByCategoryActivity.class);
            foodByCategory.putExtra(KEY_CATEGORY, category);
            context.startActivity(foodByCategory);
        });


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;


        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}
