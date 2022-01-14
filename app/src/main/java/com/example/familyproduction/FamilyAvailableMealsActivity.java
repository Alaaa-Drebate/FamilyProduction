package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.adapters.CustomerMealsAdapter;
import com.example.familyproduction.adapters.FamilyAvailableMealsAdapter;
import com.example.familyproduction.model.Food;
import com.example.familyproduction.utils.SharedPrefManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FamilyAvailableMealsActivity extends AppCompatActivity {

    Toolbar mToolBar;

    FamilyAvailableMealsAdapter mAdapter;
    RecyclerView mMealsList;
    ArrayList<Food> foods;

    FloatingActionButton addFoodBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_available_meals);

        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle(getResources().getString(R.string.our_meals));
        mMealsList = findViewById(R.id.available_meals_list);

        addFoodBtn = findViewById(R.id.add_meal_btn);

        addFoodBtn.setOnClickListener(v -> {
            startActivity(new Intent(FamilyAvailableMealsActivity.this, FamilyAddNewFoodActivity.class));
        });

        getAvailableMeals();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAvailableMeals();
    }

    private void getAvailableMeals() {

        String url = "food_app/public/api/v1/family-foods";

        foods = new ArrayList<Food>();

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
                                    foods.add(food);
                                }

                                mAdapter = new FamilyAvailableMealsAdapter(FamilyAvailableMealsActivity.this, foods);
                                mMealsList.setAdapter(mAdapter);

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
}