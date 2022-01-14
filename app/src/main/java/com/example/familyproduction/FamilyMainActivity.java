package com.example.familyproduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.adapters.CustomerMealsAdapter;
import com.example.familyproduction.adapters.FamilyOrdersAdapter;
import com.example.familyproduction.api.Constants;
import com.example.familyproduction.model.Food;
import com.example.familyproduction.model.Order;
import com.example.familyproduction.model.OrderItem;
import com.example.familyproduction.utils.SharedPrefManager;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FamilyMainActivity extends AppCompatActivity {

    Toolbar mToolBar;

    FamilyOrdersAdapter mAdapter;
    ArrayList<Order> orders;
    RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_main);


        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle(getResources().getString(R.string.food_orders));

        mList = findViewById(R.id.orders_list);

        getNewOrders();


    }

    private void getNewOrders() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        String url = "/food_app/public/api/v1/family-carts";

        orders = new ArrayList<Order>();

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.get(url)
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

                                //getting array of categories from json data response
                                JSONArray array1 = obj.getJSONArray("data");
                                Order order;
                                String orderDetails = "";
                                for(int i = 0; i < array1.length(); i++){
                                    JSONObject orderJson = array1.getJSONObject(i);
                                    JSONArray array2 = orderJson.getJSONArray("items");
                                    for(int j = 0; j < array2.length(); j++){
                                        JSONObject item = array2.getJSONObject(j);
                                        orderDetails += item.getString("food_name") + "\t\t\t\t\t\t\t" + item.getInt("qty") + "\t\t\t\t\t\t\t" + item.getDouble("total_price") + "\n";
                                    }
                                    order = new Order(
                                            orderJson.getInt("id"),
                                            orderJson.getDouble("lat"),
                                            orderJson.getDouble("lng"),
                                            orderJson.getInt("status"),
                                            orderJson.getInt("user_id"),
                                            orderJson.getString("user_name"),
                                            orderJson.getDouble("total_price"),
                                            orderDetails
                                    );
                                    if(order.getStatus() == Constants.ORDER_STATUS_NEW)
                                        orders.add(order);
                                    orderDetails = "";
                                }

                                mAdapter = new FamilyOrdersAdapter(FamilyMainActivity.this, orders);
                                mList.setAdapter(mAdapter);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.family_menu, menu);


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                logOut();
                break;
            case R.id.available_meals:
                startActivity(new Intent(this, FamilyAvailableMealsActivity.class));
                break;
            case R.id.record:
                startActivity(new Intent(this, FamilyAcceptedOrdersActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void logOut(){
        SharedPrefManager.getInstance(this).logout();
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        assert intent != null;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }

}