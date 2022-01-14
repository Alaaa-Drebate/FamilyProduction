package com.example.familyproduction.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familyproduction.R;
import com.example.familyproduction.model.Order;

import java.util.ArrayList;

public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.ViewHolder>{

    Context context;
    private ArrayList<Order> items;


    public CustomerOrdersAdapter(Context context, ArrayList<Order> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.customer_order_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order item = items.get(position);

        holder.orderId.setText(String.valueOf(item.getId()));

        holder.totalPrice.setText("" + item.getTotalPrice() + " " + context.getResources().getString(R.string.price_unit));

        switch (item.getStatus()){
            case 0:
                holder.status.setTextColor(context.getResources().getColor(R.color.status_new));
                holder.status.setText(context.getResources().getString(R.string.status_new));
                break;
            case 1:
                holder.status.setTextColor(context.getResources().getColor(R.color.status_processing));
                holder.status.setText(context.getResources().getString(R.string.status_processing));
                break;
            case 2:
                holder.status.setTextColor(context.getResources().getColor(R.color.status_rejected));
                holder.status.setText(context.getResources().getString(R.string.status_rejected));
                break;
            case 3:
                holder.status.setTextColor(context.getResources().getColor(R.color.status_completed));
                holder.status.setText(context.getResources().getString(R.string.status_completed));
                break;
        }


//        holder.itemView.setOnClickListener(v -> {
//
//            showDescriptionBill(item);
//
//        });

    }

//    private void showDescriptionBill(Order item) {
//        Intent orderDetails = new Intent(context, OrderDetailsActivity.class);
//        orderDetails.putExtra("order_id", item.getId());
//        orderDetails.putExtra("orderItems", item.getOrderDetails());
//        orderDetails.putExtra("total", item.getTotalPrice());
//        orderDetails.putExtra("lat", item.getLat());
//        orderDetails.putExtra("lon", item.getLon());
//        context.startActivity(orderDetails);
//    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView orderId;
        public TextView totalPrice;
        public TextView status;

        public ViewHolder(View itemView) {
            super(itemView);
            this.orderId = itemView.findViewById(R.id.order_id);
            this.totalPrice = itemView.findViewById(R.id.total_price);
            this.status = itemView.findViewById(R.id.status);
        }
    }
}
