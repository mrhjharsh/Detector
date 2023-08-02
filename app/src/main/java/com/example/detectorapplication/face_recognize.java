package com.example.detectorapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class face_recognize extends AppCompatActivity {
    Canvas c;
    public ImageView IVPreviewImage;
    String imageUriString;
    Bitmap selectedImageBitmap;
    public static final String PRODUCT_PHOTO = "photo";
    String str_bitmap;
    private  Bitmap bitmap;
    private ImageView  imageView_photo;
    public static Bitmap product_image;
    TextView numbers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IVPreviewImage = findViewById(R.id.imageView);
       // p.setVisibility(View.INVISIBLE);
    super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognization);
        Button b1 = findViewById(R.id.button);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

    }
    private void imageChooser() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {

                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();

                        try {
                            IVPreviewImage = findViewById(R.id.imageView);
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                            IVPreviewImage.setImageBitmap(selectedImageBitmap);
//---------set the image to bitmap
                            product_image= selectedImageBitmap;
//____________convert image to string
                            String str_bitmap = BitMapToString(product_image);
                            FirebaseVisionImage image;
                            try {
                                Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                                Bitmap m = bmp.copy(Bitmap.Config.RGB_565,true);
                                c = new Canvas(m);
                                image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                                FirebaseVisionFaceDetectorOptions options =
                                        new FirebaseVisionFaceDetectorOptions.Builder()
                                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)

                                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                                .build();
                                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                                        .getVisionFaceDetector(options);
                                Task<List<FirebaseVisionFace>> results =
                                        detector.detectInImage(image)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                                                            @Override
                                                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                                                // Task completed successfully

                                                                // ...
                                                                for (FirebaseVisionFace face : faces) {
                                                                    Rect bounds = face.getBoundingBox();
                                                                    float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                                                    float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                                                    Paint p = new Paint();
                                                                    p.setColor(Color.GREEN);
                                                                    p.setStrokeWidth(3);
                                                                    p.setStyle(Paint.Style.STROKE);
                                                                    c.drawRect(bounds , p);
                                                                    IVPreviewImage.setImageDrawable(new BitmapDrawable(getResources(), m));
                                                                    // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                                                    // nose available):
                                                                    FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
                                                                    if (leftEar != null) {
                                                                        FirebaseVisionPoint leftEarPos = leftEar.getPosition();
                                                                    }

                                                                    // If contour detection was enabled:
                                                                    List<FirebaseVisionPoint> leftEyeContour =
                                                                            face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
                                                                    List<FirebaseVisionPoint> upperLipBottomContour =
                                                                            face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();

                                                                    // If classification was enabled:
                                                                    if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                                        float smileProb = face.getSmilingProbability();
                                                                    }
                                                                    if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                                                                        float rightEyeOpenProb = face.getRightEyeOpenProbability();
                                                                    }

                                                                    // If face tracking was enabled:
                                                                    if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                                                                        int id = face.getTrackingId();
                                                                    }
                                                                }
                                                                ////

                                                                numbers= findViewById(R.id.numbers);
                                                                numbers.setText(faces.size() +" Face Detected");

                                                            }
                                                        })
                                                .addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                // Task failed with an exception
                                                                // ...
                                                            }
                                                        });






                            } catch (IOException e) {
                                e.printStackTrace();
                            }





                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    public static String BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte[] arr = baos.toByteArray();
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0,   decodedByte.length);
    }
}