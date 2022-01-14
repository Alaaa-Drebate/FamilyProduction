package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RegisterActivity extends AppCompatActivity {

    EditText mUserNameEt;
    EditText mNameEt;
    EditText mPassEt;
    EditText mEmailEt;
    EditText mPhoneEt;

    RadioGroup mAccountTypeChooser;

    int chosenAccountType = -1;

    Button mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        bindViews();

        mAccountTypeChooser.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.customer_option:
                    chosenAccountType = Constants.USER_TYPE_CUSTOMER;
                    break;
                case R.id.family_option:
                    chosenAccountType = Constants.USER_TYPE_FAMILY;
                    break;
                default: chosenAccountType = -1;
            }
        });



        mRegisterBtn.setOnClickListener(v -> {
            if(validateUserInput()){
                mRegisterBtn.setEnabled(false);
                userRegister();
            }
        });



    }

    private void userRegister() {

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        //first getting the values
        final String email = mEmailEt.getText().toString();
        final String password = mPassEt.getText().toString();
        final String name = mNameEt.getText().toString();
        final String phone = mPhoneEt.getText().toString();
        final String userName = mUserNameEt.getText().toString();

        String url = "food_app/public/api/v1/user/register";

        AndroidNetworking.post(url)
                .addBodyParameter("username", userName)
                .addBodyParameter("full_name", name)
                .addBodyParameter("password", password)
                .addBodyParameter("email", email)
                .addBodyParameter("mobile", phone)
                .addBodyParameter("type", String.valueOf(chosenAccountType))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();
                        mRegisterBtn.setEnabled(true);

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
                                    prefManager.setUserType(Constants.USER_TYPE_CUSTOMER);
                                    goToCustomerMainActivity();
                                    finish();
                                }else if(roleId == Constants.USER_TYPE_FAMILY){
                                    prefManager.setUserType(Constants.USER_TYPE_FAMILY);
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
                        mRegisterBtn.setEnabled(true);
                    }
                });



    }

    private void goToFamilyMainActivity() {
        startActivity(new Intent(this, FamilyMainActivity.class));
    }

    private void goToCustomerMainActivity() {
        startActivity(new Intent(this, CustomerMainActivity.class));
    }

    private boolean validateUserInput() {

        //first getting the values
        final String userName = mUserNameEt.getText().toString();
        final String pass = mPassEt.getText().toString();
        final String name = mNameEt.getText().toString();
        final String email = mEmailEt.getText().toString();
        final String phone = mPhoneEt.getText().toString();

        //checking if userName is empty
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "please enter a valid username!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "please enter your password!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if username is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please enter your full name !", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        //checking if email is empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "please enter your email address!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }


        //checking if password is empty
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "please enter your phone number!", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }

        if(chosenAccountType == -1){
            Toast.makeText(this, "please choose your account type first !", Toast.LENGTH_SHORT).show();
            mRegisterBtn.setEnabled(true);
            return false;
        }


        return true;

    }

    void bindViews(){
        mUserNameEt = findViewById(R.id.user_name);
        mNameEt = findViewById(R.id.name);
        mPassEt = findViewById(R.id.password);
        mEmailEt = findViewById(R.id.email);
        mPhoneEt = findViewById(R.id.phone);
        mAccountTypeChooser = findViewById(R.id.account_type_chooser);
        mRegisterBtn = findViewById(R.id.register_btn);
    }

}