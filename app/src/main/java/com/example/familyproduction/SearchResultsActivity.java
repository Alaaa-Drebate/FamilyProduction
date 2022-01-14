package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.adapters.CustomerMealsAdapter;
import com.example.familyproduction.model.Food;
import com.example.familyproduction.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    CustomerMealsAdapter mCustomerMealsAdapter;
    RecyclerView mSearchResultsList;
    ArrayList<Food> foods;

    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        mTitle = findViewById(R.id.search_results_title);
        mSearchResultsList = findViewById(R.id.search_results_list);

        handleIntent(getIntent());
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            String searchMessage = getResources().getString(R.string.search_title);
            mTitle.setText(searchMessage + " " + query);

            getAllFoods(query);

        }
    }

    void getAllFoods(String query){

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        String url = "food_app/public/api/v1/foods" + "?name=" + query;

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
                                    foods.add(food);
                                }

                                mCustomerMealsAdapter = new CustomerMealsAdapter(SearchResultsActivity.this, foods);
                                mSearchResultsList.setAdapter(mCustomerMealsAdapter);

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
}