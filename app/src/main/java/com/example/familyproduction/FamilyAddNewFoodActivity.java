package com.example.familyproduction;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.bumptech.glide.Glide;
import com.example.familyproduction.model.Category;
import com.example.familyproduction.utils.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class FamilyAddNewFoodActivity extends AppCompatActivity {

    public static final String TAG = "FAMILY_ADD_NEW_MEAL";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;

    EditText mNameET;
    EditText mDescriptionET;
    EditText mPriceET;
    Spinner mCategoryChooser;
    ImageView mUploadImageBtn;

    Button mSaveBtn;
    Button mCancel;

    ArrayList<Category> categories;

    Category chosenCategory = null;


    private static final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    String filePath = null;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_add_new_food);

        bindViews();

        getCategories();

        mUploadImageBtn.setOnClickListener(v -> requestRead());

        mSaveBtn.setOnClickListener(v -> {
            if (validateUserInput()) {
                mSaveBtn.setEnabled(false);
                addNewFood();
            }
        });

        mCancel.setOnClickListener(v -> {
            finish();
        });


    }



    private void bindViews() {
        mNameET = findViewById(R.id.name);
        mDescriptionET = findViewById(R.id.description);
        mPriceET = findViewById(R.id.price);
        mCategoryChooser = findViewById(R.id.category_chooser);
        mUploadImageBtn = findViewById(R.id.image);
        mSaveBtn = findViewById(R.id.save);
        mCancel = findViewById(R.id.cancel);
    }

    private boolean validateUserInput() {

        //first getting the values
        final String name = mNameET.getText().toString();
        final String description = mDescriptionET.getText().toString();
        final String price = mPriceET.getText().toString();

        //checking if userName is empty
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "please enter a valid meal name!", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        //checking if password is empty
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "please enter a valid description!", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        //checking if username is empty
        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "please enter a valid price !", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        try {
            Double.parseDouble(price);
        }catch (NumberFormatException e){
            Toast.makeText(this, "only numbers allowed in here", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }


        if(chosenCategory == null){
            Toast.makeText(this, "please choose a category first !", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }

        if(filePath == null){
            Toast.makeText(this, "you either didn't choose an image or didn't give us the permission we asked for please try again", Toast.LENGTH_SHORT).show();
            mSaveBtn.setEnabled(true);
            return false;
        }


        return true;

    }


    private void setUpCategoryChooser() {
        String[] categoriesNames = new String[categories.size()];
        for (int i = 0; i < categories.size(); i++){
            categoriesNames[i] = categories.get(i).getName();
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, categoriesNames );
        mCategoryChooser.setAdapter(spinnerArrayAdapter);


        mCategoryChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i < categories.size(); i++){
                    if(categoriesNames[position].equals(categories.get(i).getName())){
                        chosenCategory = categories.get(i);
                        break;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(FamilyAddNewFoodActivity.this, "you need to select on of the Food categories first !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCategories() {
        String url = "test/food_app/public/api/v1/categories";

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        categories = new ArrayList<Category>();

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


                                //getting array of categories from json data response
                                JSONArray array = obj.getJSONArray("data");
                                JSONObject categoryJson;
                                for(int i = 0; i < array.length(); i++){
                                    categoryJson = array.getJSONObject(i);
                                    categories.add(
                                            new Category(
                                                    categoryJson.getInt("id"),
                                                    categoryJson.getString("name"))
                                    );
                                }

                                setUpCategoryChooser();


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

    //..................Methods for File Chooser.................
    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            openFileChooser();
        }
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            Uri picUri = imageUri;
            filePath = getPath(picUri);
            if (filePath != null) {
                bitmap = BitmapFactory.decodeFile(filePath);
                Log.d("filePath", String.valueOf(filePath));
            }
            else
            {
                Toast.makeText(this,"no image selected", Toast.LENGTH_LONG).show();
            }

            mUploadImageBtn.setBackgroundResource(R.color.white);

            Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            Glide.with(this)
                    .load(bitmap)
                    .into(mUploadImageBtn);
        }
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    //..............................................................................


    private void addNewFood() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setMessage("Processing Please wait...");
        pDialog.show();

        String url = "food_app/public/api/v1/family-foods-add";

        File file = new File(filePath);
        String name = mNameET.getText().toString();
        String description = mDescriptionET.getText().toString();
        String price = mPriceET.getText().toString();
        String categoryId = String.valueOf(chosenCategory.getId());

        String token = SharedPrefManager.getInstance(this).getApiToken();

        AndroidNetworking.upload(url)
                .addHeaders("Authorization", "Bearer " + token)
                .addMultipartFile("image", file)
                .addMultipartParameter("name", name)
                .addMultipartParameter("description", description)
                .addMultipartParameter("price", price)
                .addMultipartParameter("category_id", categoryId)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        pDialog.dismiss();
                        mSaveBtn.setEnabled(true);
                        Log.e("Result", response.toString());
                        try {
                            Toast.makeText(FamilyAddNewFoodActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onBackPressed();
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        mSaveBtn.setEnabled(true);
                        Toast.makeText(FamilyAddNewFoodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                });

    }

}