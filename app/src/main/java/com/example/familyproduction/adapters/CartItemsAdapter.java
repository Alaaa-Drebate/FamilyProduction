package com.example.familyproduction.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyproduction.R;
import com.example.familyproduction.model.Order;
import com.example.familyproduction.sqlite.CartItemsDB;
import com.example.familyproduction.sqlite.Myappdatabas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {


    List<CartItemsDB> orders;
    Context context;

    myCartListener itemsListener;

    Myappdatabas myappdatabas;

    public CartItemsAdapter(List<CartItemsDB> orders, Context context, myCartListener listener) {
        this.orders = orders;
        this.context = context;
        itemsListener = listener;
        myappdatabas = Myappdatabas.getDatabase(context);
    }

    public double getTotalPrice(){
        double totalPrice = 0.0;
        for (CartItemsDB o:orders){
            totalPrice += o.getPrice() * o.getQuantity();
        }
        return totalPrice;
    }

    public List<CartItemsDB> getOrders() {
        return orders;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.cart_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItemsDB order = orders.get(position);

        holder.name.setText(order.getName());

        holder.price.setText(String.valueOf(order.getPrice()));

        holder.quantity.setText(String.valueOf(order.getQuantity()));

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater factory = LayoutInflater.from(context);
                final View view = factory.inflate(R.layout.add_food_to_cart_dialog, null);
                final AlertDialog addProductDialog = new AlertDialog.Builder(context).create();
                addProductDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addProductDialog.setView(view);

                ImageButton add = view.findViewById(R.id.add);
                ImageButton sub = view.findViewById(R.id.subtract);
                TextView quantity = view.findViewById(R.id.quantity);
                TextView save = view.findViewById(R.id.save);
                TextView cancel = view.findViewById(R.id.cancel);
                TextView total = view.findViewById(R.id.price);

                //set the previous value first
                quantity.setText(String.valueOf(order.getQuantity()));


                double price = order.getPrice()/order.getQuantity();

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

                        int quantityInt = Integer.parseInt(quantityText);
                        //checking if quantity is empty
                        if (TextUtils.isEmpty(quantityText) || (quantityInt == 0)) {
                            Toast.makeText(context, "please add at least one item!", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        order.setQuantity(quantityInt);

                        updateOrder(position, order);

                        addProductDialog.dismiss();

                        //to tell the user activity
                        itemsListener.onChange();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addProductDialog.dismiss();
                    }
                });
                addProductDialog.show();

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater factory = LayoutInflater.from(context);
                final View view = factory.inflate(R.layout.delete_confirmation_dialog, null);
                final AlertDialog deleteProductDialog = new AlertDialog.Builder(context).create();
                deleteProductDialog.setView(view);

                TextView yes = view.findViewById(R.id.yes_btn);
                TextView no = view.findViewById(R.id.no_btn);


                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //to see changes immediately
                        removeOrder(position);
                        //tell  the user activity
                        itemsListener.onChange();
                        //when done dismiss;
                        deleteProductDialog.dismiss();

                    }
                });

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteProductDialog.dismiss();
                    }
                });
                deleteProductDialog.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public TextView name;
        public TextView price;
        public TextView quantity;
        public ImageView edit;
        public ImageView delete;

        public ViewHolder(View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.price = itemView.findViewById(R.id.price);
            this.quantity = itemView.findViewById(R.id.quantity);
            this.edit = itemView.findViewById(R.id.edit_btn);
            this.delete =itemView.findViewById(R.id.delete_btn);
        }
    }

    private void removeOrder(int index) {
        myappdatabas.myDao().deleteItem(orders.get(index));
        orders.remove(index);
        notifyItemRemoved(index);
    }


    private void updateOrder(int index, CartItemsDB order) {
        myappdatabas.myDao().updateItem(order);
        orders.set(index, order);
        notifyItemChanged(index);
    }



    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        itemsListener =null;
    }

    public interface myCartListener{
        void onChange();
    }



}
