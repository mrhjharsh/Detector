package com.example.detectorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class text_recognize extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView res;
    Button capture;
    Bitmap selectedImageBitmap;



    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Set the MIME type to image/* to filter only images
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void onCaptureFromCameraClick(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            // Handle the case where the camera app is not available
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Handle image selected from gallery

                    processbyGallery(data);
                   // Bitmap selectedImageBitmap = uriToBitmap(selectedImageUri);
                    //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    // Process the selected image
                }
            } else if (requestCode == CAMERA_REQUEST_CODE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    // Handle captured image from camera
                    Bitmap photoBitmap = (Bitmap) extras.get("data");
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageBitmap(photoBitmap);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("imageUri", data.getData()); // Assuming "data" is your Intent data



                    Toast.makeText(this, ""+data.getExtras(), Toast.LENGTH_SHORT).show();

                    // Process the captured photo
                }
            }
        }
        else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognize);
        Button b = findViewById(R.id.textrecog);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              openImagePicker();
            }
        });


    }



    private void processbyGallery(Intent data) {
        Uri selectedImageUri = data.getData();

        ImageView IVPreviewImage;
        IVPreviewImage = findViewById(R.id.imageView);
        try {
            selectedImageBitmap
                    = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(),
                    selectedImageUri);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
        IVPreviewImage.setImageBitmap(selectedImageBitmap);
        FirebaseVisionImage image;
        try {
            image = FirebaseVisionImage.fromFilePath(getApplicationContext() , data.getData());

            FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                    .getOnDeviceTextRecognizer();

            textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText result) {
                            // Task completed successfully
                            // ...
                            res = findViewById(R.id.textView);
                            res.setText(result.getText());
                            String textToCopy = result.getText();
                            copyToClipboard(textToCopy);
                            Toast.makeText(text_recognize.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                    res.setText("failed");

                                    Log.d("kkkk" , e+"");

                                }
                            });




        } catch (IOException e) {
            Log.d("kkkk" , e+"");

            throw new RuntimeException(e);
        }
    }


    private void copyToClipboard(String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
        clipboard.setPrimaryClip(clip);
    }


}