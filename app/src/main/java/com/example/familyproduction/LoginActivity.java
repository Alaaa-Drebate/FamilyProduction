package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.familyproduction.api.Constants;
import com.example.familyproduction.model.User;
import com.example.familyproduction.utils.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText mUserNameEt;
    EditText mPassEt;

    Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        bindViews();

        mLoginBtn.setOnClickListener(v -> {
            if(validateUserInput()){
                mLoginBtn.setEnabled(false);
                userLogin();
            }
        });

    }


    private void userLogin() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        //first getting the values
        final String password = mPassEt.getText().toString();
        final String userName = mUserNameEt.getText().toString();

        String url = "food_app/public/api/v1/user/login";

        AndroidNetworking.post(url)
                .addBodyParameter("username", userName)
                .addBodyParameter("password", password)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();
                        mLoginBtn.setEnabled(true);

                        try {
                            //converting response to json object
                            JSONObject obj = response;

                            int status = obj.getInt("status");

                            //if no error in response
                            if (status == 1) {

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                                JSONObject userJson = obj.getJSONObject("data");
                                User user;
                                user = new User(
                                        userJson.getString("username"),
                                        userJson.getString("full_name"),
                                        userJson.getString("email"),
                                        userJson.getString("mobile")
                                );

                                //storing the user in shared preferences
                                prefManager.userLogin(user, userJson.getString("api_token"));
                                finish();

                                int roleId = Integer.parseInt(userJson.getString("role_id"));

                                if(roleId == Constants.USER_TYPE_CUSTOMER){
                                    SharedPrefManager.getInstance(getApplicationContext()).setUserType(Constants.USER_TYPE_CUSTOMER);
                                    goToCustomerMainActivity();
                                    finish();
                                }else if(roleId == Constants.USER_TYPE_FAMILY){
                                    SharedPrefManager.getInstance(getApplicationContext()).setUserType(Constants.USER_TYPE_FAMILY);
                                    goToFamilyMainActivity();
                                    finish();
                                }

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
                        mLoginBtn.setEnabled(true);
                    }
                });

    }

    private void goToFamilyMainActivity() {
        startActivity(new Intent(this, FamilyMainActivity.class));
    }

    private void goToCustomerMainActivity() {
        startActivity(new Intent(this, CustomerMainActivity.class));
    }

    void bindViews(){
        mUserNameEt = findViewById(R.id.user_name);
        mPassEt = findViewById(R.id.password);

        mLoginBtn = findViewById(R.id.login_btn);
    }

    private boolean validateUserInput() {

        //first getting the values
        final String userName = mUserNameEt.getText().toString();
        final String pass = mPassEt.getText().toString();

        //checking if userName is empty
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "please enter a valid username!", Toast.LENGTH_SHORT).show();
            mLoginBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please enter your password!", Toast.LENGTH_SHORT).show();
            mLoginBtn.setEnabled(true);
            return false;
        }

        return true;

    }
}