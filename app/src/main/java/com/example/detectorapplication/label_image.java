package com.example.detectorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;


import java.io.IOException;
import java.util.List;

public class label_image extends AppCompatActivity {
// ...
    private static final int PICK_IMAGE_REQUEST = 1;
    Bitmap selectedImageBitmap;



    TextView res;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_image);
        Button b = findViewById(R.id.textrecog);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();

            }
        });
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); // Set the MIME type to image/* to filter only images
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle the selected image here
            // You can get the URI of the selected image using:
            // Uri selectedImageUri = data.getData();
            // Then, you can perform actions like displaying the image or uploading it.

// ...
            // To use default options:
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
            InputImage image;
            try {
                image = InputImage.fromFilePath(getApplicationContext(), data.getData());
                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {
                                // Task completed successfully
                                res = findViewById(R.id.textView);
                                res.setText("");
                                // ...
                                for (ImageLabel label : labels) {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();
                                    res.append(text + " - "+confidence+"\n");
                                }

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                                res = findViewById(R.id.textView);
                                res.setText("failed");
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }








        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }



}