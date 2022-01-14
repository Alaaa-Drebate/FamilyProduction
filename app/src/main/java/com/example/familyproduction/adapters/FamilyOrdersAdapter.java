package com.example.familyproduction.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.FoodDetailsActivity;
import com.example.familyproduction.OrderDetailsActivity;
import com.example.familyproduction.R;
import com.example.familyproduction.api.Constants;
import com.example.familyproduction.model.Order;
import com.example.familyproduction.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.CompletableOnSubscribe;

public class FamilyOrdersAdapter extends RecyclerView.Adapter<FamilyOrdersAdapter.ViewHolder>{

    Context context;
    private ArrayList<Order> items;


    public FamilyOrdersAdapter(Context context, ArrayList<Order> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.full_order_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order item = items.get(position);

        holder.orderId.setText(String.valueOf(item.getId()));

        holder.totalPrice.setText("" + item.getTotalPrice() + " " + context.getResources().getString(R.string.price_unit));

        holder.infoBtn.setOnClickListener(v -> {
            showDescriptionBill(item);
        });

        holder.acceptBtn.setOnClickListener(v -> {
            updateOrder(item.getId(), position, Constants.ORDER_STATUS_ACCEPTED);
        });

        holder.rejectBtn.setOnClickListener(v -> {
            updateOrder(item.getId(), position, Constants.ORDER_STATUS_REJECTED);
        });


        holder.itemView.setOnClickListener(v -> {

            showDescriptionBill(item);

        });

    }

    private void showDescriptionBill(Order item) {
            Intent orderDetails = new Intent(context, OrderDetailsActivity.class);
            orderDetails.putExtra("order_id", item.getId());
            orderDetails.putExtra("orderItems", item.getOrderDetails());
            orderDetails.putExtra("total", item.getTotalPrice());
            orderDetails.putExtra("lat", item.getLat());
            orderDetails.putExtra("lon", item.getLon());
            context.startActivity(orderDetails);
    }

    private void updateOrder(int orderId, int position, int status) {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();


        String url = "http://smarttracks.org/test/food_app/public/api/v1/carts/update";

        String token = SharedPrefManager.getInstance(context).getApiToken();

        AndroidNetworking.post(url)
                .addBodyParameter("cart_id", String.valueOf(orderId))
                .addBodyParameter("status", String.valueOf(status))
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            int status = obj.getInt("status");

                            //if no error in response
                            if (status == 1) {

                                items.remove(position);
                                notifyItemRemoved(position);

                            } else {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pDialog.dismiss();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView orderId;
        public TextView totalPrice;
        public  ImageView infoBtn;
        public ImageView acceptBtn;
        public ImageView rejectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.orderId = itemView.findViewById(R.id.order_id);
            this.totalPrice = itemView.findViewById(R.id.total_price);
            this.infoBtn = itemView.findViewById(R.id.info_btn);
            this.acceptBtn = itemView.findViewById(R.id.accept_btn);
            this.rejectBtn = itemView.findViewById(R.id.reject_btn);
        }
    }
}
