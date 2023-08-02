package com.example.detectorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;

import java.io.IOException;
import java.util.List;

public class barcode extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    TextView res;
    Bitmap selectedImageBitmap;
    String uy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        Button b = findViewById(R.id.textrecog);
        Button b1 = findViewById(R.id.url);
        b1.setVisibility(View.INVISIBLE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urls = ""+uy;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls));
                intent.setPackage("com.android.chrome");
                startActivity(intent);
                if (intent.resolveActivity(getPackageManager()) != null) {

                } else {
                }
            }
        });

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Handle the selected image here
            // You can get the URI of the selected image using:
            // Uri selectedImageUri = data.getData();
            // Then, you can perform actions like displaying the image or uploading it.
            FirebaseVisionImage image;
            Uri selectedImageUri = data.getData();

            ImageView IVPreviewImage;
            IVPreviewImage = findViewById(R.id.imageView);
                try {
                    selectedImageBitmap
                            = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            selectedImageUri);
                    image = FirebaseVisionImage.fromFilePath(getApplicationContext(), data.getData());
                    FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                            .getVisionBarcodeDetector();
                    Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                            .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                                @Override
                                public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                                    // Task completed successfully
                                    // ...
                                    for (FirebaseVisionBarcode barcode: barcodes) {
                                        Rect bounds = barcode.getBoundingBox();
                                        Point[] corners = barcode.getCornerPoints();

                                        String rawValue = barcode.getRawValue();

                                        int valueType = barcode.getValueType();
                                        // See API reference for complete list of supported types
                                        res = findViewById(R.id.textView);
                                        res.setText("");
                                         Log.d("hhhh" , valueType+"");
                                         Button button = findViewById(R.id.url);

                                        switch (valueType) {


                                            case FirebaseVisionBarcode.TYPE_WIFI:
                                               // Log.d("jjj ", String.valueOf(barcode.getEmail()));
                                                String ssid = barcode.getWifi().getSsid();
                                                String password = barcode.getWifi().getPassword();
                                                int type = barcode.getWifi().getEncryptionType();
                                                res.append("Wifi name - "+ssid +"\n"+"Password - "+password);
                                                copyToClipboard(res.getText().toString());



                                                break;
                                            case FirebaseVisionBarcode.TYPE_URL:
                                                String title = barcode.getUrl().getTitle();
                                                String url = barcode.getUrl().getUrl();
                                                uy =url;
                                                res.append(url);
                                                if(barcode.getUrl()!=null){
                                                    button.setVisibility(View.VISIBLE);
                                                }
                                                copyToClipboard(res.getText().toString());

                                                //Log.d("jjjj" , title+url);
                                              //  Toast.makeText(barcode.this, "hiiii", Toast.LENGTH_SHORT).show();
                                                break;

                                            default:
                                                String datas = barcode.getDisplayValue();
                                                Log.d("ssss" , barcode.getDisplayValue()+"");
                                                if(barcode.getUrl()!=null){
                                                    button.setVisibility(View.VISIBLE);
                                                    uy =barcode.getUrl().getUrl();
                                                }
                                                res.setText(datas);
                                                copyToClipboard(res.getText().toString());

                                        }
                                    }

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    Toast.makeText(barcode.this, "kaiii", Toast.LENGTH_SHORT).show();

                                    // ...

                                    res = findViewById(R.id.textView);
                                    res.setText("failed");
                                }
                            });




                } catch (IOException e) {
                    e.printStackTrace();
                }


// Or, to specify the formats to recognize:
// BarcodeScanner scanner = BarcodeScanning.getClient(options);


        } else {
            Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show();
        }
    }
       private void copyToClipboard(String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
        clipboard.setPrimaryClip(clip);
           Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

}