package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.api.Constants;
import com.example.familyproduction.model.Order;
import com.example.familyproduction.model.OrderItem;
import com.example.familyproduction.utils.SharedPrefManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {


    public final String TAG = this.getClass().getSimpleName();

    Toolbar mToolBar;

    Button mAcceptBtn, mRejectBtn;
    TextView mTotalPrice, mOrderDetails;

    CardView mOrderCard;
    LinearLayout mDetailsLayout;
    ImageView mOrderExpand;
    boolean orderExpanded = false;

    //from sender Intent
    int orderId;
    double lat, lon;
    String orderDetails;
    Double totalPrice;


    //for google maps
    private static final float NORMAL_ZOOM = 15f;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Intent sender = getIntent();
        if(sender != null){
            orderId = sender.getIntExtra("order_id", -1);
            orderDetails = sender.getStringExtra("orderItems");
            totalPrice = sender.getDoubleExtra("total", -1);
            lat = sender.getDoubleExtra("lat", -1);
            lon = sender.getDoubleExtra("lon", -1);
        }

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mAcceptBtn = findViewById(R.id.accept_btn);
        mRejectBtn = findViewById(R.id.reject_btn);
        mTotalPrice = findViewById(R.id.total_price);
        mOrderDetails = findViewById(R.id.order_details);

        mOrderCard = findViewById(R.id.order_card);
        mDetailsLayout = findViewById(R.id.order_details_layout);
        mOrderExpand = findViewById(R.id.order_expand_btn);

        mToolBar.setTitle(getResources().getString(R.string.order_id) + " : #" + orderId);



        mAcceptBtn.setOnClickListener(v -> {
            updateOrder(orderId, Constants.ORDER_STATUS_REJECTED);
        });

        mRejectBtn.setOnClickListener(v -> {
            updateOrder(orderId, Constants.ORDER_STATUS_REJECTED);
        });


        mOrderCard.setOnClickListener(v -> {
            if(orderExpanded){
                hideOrderDetails();
                mOrderExpand.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
                mOrderExpand.setZ(10);
                orderExpanded = false;
            }else {
                showOrderDetails();
                mOrderExpand.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
                mOrderExpand.setZ(10);
                orderExpanded = true;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .mapType(GoogleMap.MAP_TYPE_NORMAL);

    }

    private void showOrderDetails() {

        mTotalPrice.setText(totalPrice + " SAR");
        mOrderDetails.setText(orderDetails);

        mDetailsLayout.setVisibility(View.VISIBLE);
    }

    private void hideOrderDetails() {
        mDetailsLayout.setVisibility(View.GONE);
    }



    void updateOrder(int id, int status){
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();


        String url = "food_app/public/api/v1/carts/update";

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.post(url)
                .addBodyParameter("cart_id", String.valueOf(id))
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
                                //todo ...

                            } else {
                                Toast.makeText(OrderDetailsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        pDialog.dismiss();
                        Toast.makeText(OrderDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker for the car location and move the camera
        LatLng orderLocation = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions()
                .position(orderLocation)
                .title(getResources().getString(R.string.order_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(orderLocation, NORMAL_ZOOM));
    }
}