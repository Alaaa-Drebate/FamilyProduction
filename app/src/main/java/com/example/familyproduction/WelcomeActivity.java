package com.example.familyproduction;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.familyproduction.api.Constants;
import com.example.familyproduction.utils.SharedPrefManager;

public class WelcomeActivity extends AppCompatActivity {

    Button mLoginBtn, mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        if(prefManager.isLoggedIn()){
            switch (prefManager.getUserType()){
                case Constants.USER_TYPE_CUSTOMER:
                    goToCustomerMainActivity();
                    finish();
                    break;
                case Constants.USER_TYPE_FAMILY:
                    goToFamilyMainActivity();
                    finish();
                    break;
            }
        }



        mLoginBtn = findViewById(R.id.login_btn);
        mRegisterBtn = findViewById(R.id.register_btn);

        mLoginBtn.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        mRegisterBtn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));


    }

    private void goToFamilyMainActivity() {
        startActivity(new Intent(this, FamilyMainActivity.class));
    }

    private void goToCustomerMainActivity() {
        startActivity(new Intent(this, CustomerMainActivity.class));
    }

}