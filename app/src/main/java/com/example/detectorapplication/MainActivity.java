package com.example.detectorapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
//    String[]permissions = {"android.permission.CAMERA"};
//    private static final int CAMERA_PERMISSION_CODE = 101; // You can choose any integer value
//    private static final int CAMERA_REQUEST_CODE = 1;
//
//
//
//    // Helper function to check if all permissions are granted
//    private boolean hasPermissions(Context context, String[] permissions) {
//        for (String permission : permissions) {
//            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Camera permission granted, proceed to open camera
//              // openCamera();
//            } else {
//                // Camera permission denied, handle accordingly
//            }
//        }
//    }
//    private void openCamera() {
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
//        } else {
//            // Handle the case where the camera app is not available
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check for camera permission and request if not granted

// ...

// Check for camera permission and request if not granted
//        if (!hasPermissions(this, permissions)) {
//            ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_CODE);
//        } else {
//            // Permission already granted, proceed to open camera
//        }



        LinearLayout b = findViewById(R.id.textrecog);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , text_recognize.class));
            }
        });
        LinearLayout b2 = findViewById(R.id.facerecog);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , face_recognize.class));
            }
        });
        LinearLayout b3 = findViewById(R.id.labelimage);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , label_image.class));
            }
        });
        LinearLayout b4 = findViewById(R.id.animals);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , animal_detector.class));
            }
        });
        LinearLayout b5 = findViewById(R.id.barcode);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , barcode.class));
            }
        });


    }
}