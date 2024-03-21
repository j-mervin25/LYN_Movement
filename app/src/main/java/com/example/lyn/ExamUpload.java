package com.example.lyn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ExamUpload extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private UploadUtility uploadUtility;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private ImageButton b1;
    String retrievedSNo;
    private String examSubjectid;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_upload);
// Retrieve classInfo and medium from DataManager
        retrievedSNo = DataManager.getInstance().getRetrievedSNo();

        // Log the retrieved values to ensure they are correct
        Log.d("Upload", "Sno: " + retrievedSNo);

        examSubjectid = DataManager.getInstance().getExamSubjectid();

        // Log the retrieved values to ensure they are correct

        Log.d("Upload", "ExamId: " + examSubjectid);

        uploadUtility = new UploadUtility(this);

        Button selectPictureButton = findViewById(R.id.button_selectpic);

        Button uploadButton = findViewById(R.id.uploadButton);

        imageView = findViewById(R.id.imageView);

        b1 = findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ExamUpload.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted, request the permission
                    ActivityCompat.requestPermissions(ExamUpload.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                } else {
                    // Permission has already been granted, proceed to open camera
                    openCamera();
                }
            }
        });
        selectPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamUpload.this.onClick(v);
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getData() != null) {
                        // If there is a data URI, it means the image is saved in the gallery
                        selectedImageUri = data.getData();
                        imageView.setImageURI(selectedImageUri);
                    } else {
                        // If there is no data URI, it means the image is captured directly
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");
                            if (imageBitmap != null) {
                                imageView.setImageBitmap(imageBitmap);
                            } else {
                                // Handle case where "data" extra is null or does not contain bitmap
                                Toast.makeText(ExamUpload.this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle case where "extras" bundle is null
                            Toast.makeText(ExamUpload.this, "Failed to retrieve extras", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Handle the selected image URI
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            Toast.makeText(this, "Selected Image Set to ImageView", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".jpg");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            ContentResolver contentResolver = getContentResolver();
            Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(cameraIntent);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your logic
                openCamera();
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void onClick(View v) {
        if (selectedImageUri != null) {
            String selectedImagePath = URIPathHelper.getPath(ExamUpload.this, selectedImageUri);
            String arguments = "uploadtype=OfflineExam&SNo=" + retrievedSNo + "&ExamSubjectID=" + examSubjectid;
            Log.d("Upload1", "Id: " + examSubjectid);
            uploadUtility.uploadFile(selectedImagePath, null, arguments); // Pass null for the fileName for now
        } else {
            Toast.makeText(ExamUpload.this, "Please select an image first", Toast.LENGTH_SHORT).show();
        }
    }
}