package com.example.familyproduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
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
import com.example.familyproduction.adapters.CategoriesAdapter;
import com.example.familyproduction.adapters.CustomerMealsAdapter;
import com.example.familyproduction.model.Food;
import com.example.familyproduction.utils.PermissionsChecker;
import com.example.familyproduction.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomerMainActivity extends AppCompatActivity {

    Toolbar mToolBar;

    CustomerMealsAdapter mRecentCustomerMealsAdapter;
    CustomerMealsAdapter mAvailableMealsAdapter;
    CategoriesAdapter mCategoriesAdapter;

    RecyclerView mRecentFoodList;
    RecyclerView mAvailableMealsList;
    RecyclerView mCategoriesList;

    //data
    ArrayList<Food> allFoods;
    ArrayList<String> categories;
    ArrayList<Food> recentFoods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        mRecentFoodList = findViewById(R.id.recent_foods_list);
        mAvailableMealsList = findViewById(R.id.available_meals_list);
        mCategoriesList = findViewById(R.id.categories_list);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle(getResources().getString(R.string.food_orders));


        getAllCategories();
        getAllFoods("");
        getRecentFoods();

        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                                };

        PermissionsChecker.firstTimeRequestPermissions(this, this, permissions);




    }



    void getAllCategories(){

        String url = "food_app/public/api/v1/categories";

        categories = new ArrayList<String>();

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            int status = obj.getInt("status");

                            //if no error in response
                            if (status == 1) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting array of categories from json data response
                                JSONArray array = obj.getJSONArray("data");
                                for(int i = 0; i < array.length(); i++){
                                    categories.add(array.getJSONObject(i).getString("name"));
                                }

                                mCategoriesAdapter = new CategoriesAdapter(CustomerMainActivity.this, categories);
                                mCategoriesList.setAdapter(mCategoriesAdapter);

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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void getAllFoods(String query){

        String url = "food_app/public/api/v1/foods" + "?name=" + query;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        allFoods = new ArrayList<Food>();

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
                                JSONArray array = obj.getJSONArray("data");
                                Food food;
                                for(int i = 0; i < array.length(); i++){
                                    JSONObject foodJson = array.getJSONObject(i);
                                    food = new Food(
                                            Integer.parseInt(foodJson.getString("id")),
                                            foodJson.getString("name"),
                                            foodJson.getString("description"),
                                            Double.parseDouble(foodJson.getString("price")),
                                            Integer.parseInt(foodJson.getString("user_id")),
                                            foodJson.getString("user_name"),
                                            foodJson.getString("category_name"),
                                            foodJson.getString("image")
                                    );
                                    allFoods.add(food);
                                }

                                mAvailableMealsAdapter = new CustomerMealsAdapter(CustomerMainActivity.this, allFoods);
                                mAvailableMealsList.setAdapter(mAvailableMealsAdapter);

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

    void getRecentFoods(){

        String url = "food_app/public/api/v1/foods-recent";

        recentFoods = new ArrayList<Food>();

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.get(url)
                .addHeaders("Authorization", "Bearer " + token)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            int status = obj.getInt("status");

                            //if no error in response
                            if (status == 1) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting array of categories from json data response
                                JSONArray array = obj.getJSONArray("data");
                                Food food;
                                for(int i = 0; i < array.length(); i++){
                                    JSONObject foodJson = array.getJSONObject(i);
                                    food = new Food(
                                            Integer.parseInt(foodJson.getString("id")),
                                            foodJson.getString("name"),
                                            foodJson.getString("description"),
                                            Double.parseDouble(foodJson.getString("price")),
                                            Integer.parseInt(foodJson.getString("user_id")),
                                            foodJson.getString("user_name"),
                                            foodJson.getString("category_name"),
                                            foodJson.getString("image")
                                    );
                                    recentFoods.add(food);
                                }

                                mRecentCustomerMealsAdapter = new CustomerMealsAdapter(CustomerMainActivity.this, recentFoods);
                                mRecentFoodList.setAdapter(mRecentCustomerMealsAdapter);

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
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchIntent = new Intent(CustomerMainActivity.this, SearchResultsActivity.class);
                searchIntent.setAction(Intent.ACTION_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, query);
                startActivity(searchIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cart:
                startActivity(new Intent(CustomerMainActivity.this, MyCartActivity.class));
                break;
            case R.id.orders:
                startActivity(new Intent(CustomerMainActivity.this, CustomerOrders.class));
                break;
            case R.id.logout:
                logOut();
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