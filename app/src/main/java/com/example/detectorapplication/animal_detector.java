package com.example.detectorapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.detectorapplication.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class animal_detector extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    Bitmap selectedImageBitmap;
    TextView res;
    Bitmap bitmap;
    String[] labels = new String[1001];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detector);
        int cnt = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("labels_mobilenet_quant_v1_224.txt")));
            String line = bufferedReader.readLine();
            while (line!=null){
                labels[cnt] = line;
                cnt++;
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    @SuppressLint("SetTextI18n")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            ImageView IVPreviewImage;
            IVPreviewImage = findViewById(R.id.imageView);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                selectedImageBitmap
                        = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(),
                        selectedImageUri);
                IVPreviewImage.setImageBitmap(selectedImageBitmap);

                try {
                    MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(animal_detector.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);

                    bitmap = Bitmap.createScaledBitmap(bitmap , 224,224,true);
                    inputFeature0.loadBuffer(TensorImage.fromBitmap(bitmap).getBuffer());

                    // Runs model inference and gets result.
                    MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    res = findViewById(R.id.textView);
                    res.setText(labels[getMax(outputFeature0.getFloatArray())]+"");

                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }











            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            IVPreviewImage.setImageBitmap(selectedImageBitmap);


        }
        else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }
    int getMax(float[]arr){
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]>arr[max])max = i;
        }
        return max;
    }

}