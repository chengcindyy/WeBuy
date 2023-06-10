package com.csis4495_cmk.webuy;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.csis4495_cmk.webuy.adapters.ImagesAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerAddProductActivity extends AppCompatActivity implements ImagesAdapter.onImgClick{

    private Button btn_add_style;

    private final int REQUEST_PERMISSION_CODE = 10;
    private StorageReference productReference;
    private DatabaseReference firebaseDatabase;
    private FirebaseDatabase firebaseInstance;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> imgFilePicker;

    private RecyclerView recyclerViewImgs;
    private List<Uri> uriUploadImgs;
    private int uploadImgCount = 0;
    private byte[] imageBytes;
    private String _IMG;
    private List<String> imgsList;
    private String productId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);

        btn_add_style = findViewById(R.id.btn_add_style);


        //set images recycler view with default to add image
        uriUploadImgs = new ArrayList<>();
        GridLayoutManager gm = new GridLayoutManager(this, 4);
        recyclerViewImgs = findViewById(R.id.rv_add_product_img);
        recyclerViewImgs.setLayoutManager(gm);
        setAdapter(); // empty imgsList

        //add images when add img in rv clicked
        imgFilePicker = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                uriUploadImgs = result;
                setAdapter();
            }
        });

        //add style
        btn_add_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserPermission();

                //startActivity(new Intent(SellerAddProductActivity.this, SellerAddStyleActivity.class));
            }
        });
    }

    private void checkUserPermission() {
        if (ContextCompat.checkSelfPermission(SellerAddProductActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SellerAddProductActivity.this, new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_PERMISSION_CODE);
            Toast.makeText(this,"permission asked",Toast.LENGTH_SHORT).show();
        } else {
            pickImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImages();
            } else {
                Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImages() {
        imgFilePicker.launch("image/*");
        if (uriUploadImgs != null) {
            uriUploadImgs.clear();
        }
    }

    private void setAdapter() {
        ImagesAdapter imagesAdapter = new ImagesAdapter(this, uriUploadImgs);
        recyclerViewImgs.setAdapter(imagesAdapter);
        imagesAdapter.setmListenr(SellerAddProductActivity.this);
    }

    private void compressImages(List<Uri> uriUploadImgs) {
        imgsList = new ArrayList<>();
        for (int i = 0; i < uriUploadImgs.size(); i++) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriUploadImgs.get(i));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                imageBytes = stream.toByteArray();
                _IMG = productId + "_" + i + ".jpg";
                imgsList.add(_IMG);
                uploadImagesToFireStorage(imageBytes, uriUploadImgs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImagesToFireStorage(byte[] imageBytes, List<Uri> uriUploadImgs) {
        productReference = FirebaseStorage.getInstance().getReference().child("ReviewPhotos");
        StorageReference imgReference = productReference.child(_IMG);

        imgReference.putBytes(imageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadImgCount++;
                if(uploadImgCount == uriUploadImgs.size()) {
                    Log.d("upload", "uploaded done");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerAddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void addNewImg() {
        checkUserPermission();
    }
}