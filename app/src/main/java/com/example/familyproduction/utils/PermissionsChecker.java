package com.example.familyproduction.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class PermissionsChecker {

    private static final int PERMISSIONS_REQUEST_CODE = 500;


    public static void firstTimeRequestPermissions(Activity activity, Context context, String[] permissions) {

        ArrayList<String> noPermission = new ArrayList<>();

        for(String permission : permissions){
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                noPermission.add(permission);
        }

        if(noPermission.isEmpty()){
            return ;
        }else {
            ActivityCompat.requestPermissions(activity, permissions, PERMISSIONS_REQUEST_CODE);
        }
    }
}
