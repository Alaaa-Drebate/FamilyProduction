package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.adapters.CartItemsAdapter;
import com.example.familyproduction.adapters.FamilyAcceptedOrdersAdapter;
import com.example.familyproduction.api.Constants;
import com.example.familyproduction.model.Order;
import com.example.familyproduction.model.User;
import com.example.familyproduction.sqlite.CartItemsDB;
import com.example.familyproduction.sqlite.Myappdatabas;
import com.example.familyproduction.utils.GpsTracker;
import com.example.familyproduction.utils.SharedPrefManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCartActivity extends AppCompatActivity implements CartItemsAdapter.myCartListener{


    RecyclerView mItemsList;
    CartItemsAdapter mItemsAdapter;
    List<CartItemsDB> items;

    TextView mTotalPriceText;
    Button mConfirmOrderBtn;

    BroadcastReceiver bReceiver;

    double mMyLatitude = 150, mMyLongitude = 150;

    Myappdatabas myappdatabas;

    GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cart);

        mItemsList = findViewById(R.id.cart_items_list);
        mTotalPriceText = findViewById(R.id.total_price_text_view);
        mConfirmOrderBtn = findViewById(R.id.confirm_order_btn);

        myappdatabas = Myappdatabas.getDatabase(this);

        items = myappdatabas.myDao().getItems();

        mItemsAdapter = new CartItemsAdapter(items, this, this);

        mItemsList.setAdapter(mItemsAdapter);

        getLocation();

        //calculate and set total price
        updateTotalPrice();

        mConfirmOrderBtn.setOnClickListener(v -> {
            if(items.isEmpty()){
                Toast.makeText(this, "your cart is empty !", Toast.LENGTH_SHORT).show();
            }else
                checkout();
        });

        bReceiver = new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    mMyLatitude = intent.getDoubleExtra("latitude", mMyLatitude);
                    mMyLongitude = intent.getDoubleExtra("longitude", mMyLongitude);
                }

            }
        };
    }

    private JSONArray ordersToSend(){

        JSONArray jsonArray = new JSONArray();
        List<CartItemsDB> orders = mItemsAdapter.getOrders();

        for (CartItemsDB item : orders){
            JSONObject order = new JSONObject();
            try {
                order.put("food_id", String.valueOf(item.getId()));
                order.put("quantity", String.valueOf(item.getQuantity()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(order);
        }

        return jsonArray;
    }

    private void checkout() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();


        String url = "/public/api/v1/checkout";

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.post(url)
                .addBodyParameter("lat", String.valueOf(mMyLatitude))
                .addBodyParameter("lng", String.valueOf(mMyLongitude))
                .addBodyParameter("cart_items", ordersToSend().toString())
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

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                myappdatabas.myDao().deleteAll(items);
                                mItemsAdapter = new CartItemsAdapter(new ArrayList<CartItemsDB>(), MyCartActivity.this, MyCartActivity.this);
                                mItemsList.setAdapter(mItemsAdapter);
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("location"));
        //just making assume that dialogs can get fragments into onStop() state
        updateTotalPrice();
    }

    void updateTotalPrice(){
        mTotalPriceText.setText(getResources().getString(R.string.total) + mItemsAdapter.getTotalPrice());
    }


    @Override
    public void onChange() {
        updateTotalPrice();
    }


    public void getLocation(){
        gpsTracker = new GpsTracker(this);
        if(gpsTracker.canGetLocation()){
            while (gpsTracker.getLatitude() == 0 || gpsTracker.getLongitude() == 0){
                gpsTracker.getLocation();
            }
            mMyLatitude = gpsTracker.getLatitude();
            mMyLongitude = gpsTracker.getLongitude();
        }else{
            gpsTracker.showSettingsAlert();
        }
    }
}