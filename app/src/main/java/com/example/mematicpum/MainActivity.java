package com.example.mematicpum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button mUploadButton;

    Button mShowImagesButton;

    Button button;
    ImageView mImage;
    TextView mUploadText;
    ProgressBar mUploadProgressBar;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity2();
            }
        });

        mImage = findViewById(R.id.uploadImageContainer);
        mUploadText = findViewById(R.id.uploadTextView);
        mUploadProgressBar = findViewById(R.id.uploadProgressBar);
        mUploadButton = findViewById(R.id.uploadButton);
        mUploadButton.setOnClickListener(uploadImageOnClickHandler);
        mShowImagesButton = findViewById(R.id.showImagesButton);
        mShowImagesButton.setOnClickListener(showImagesOnClickHandler);
    }
    public void openActivity2() {
        Intent intent = new Intent(this, Share.class);
        startActivity(intent);
    }

    View.OnClickListener uploadImageOnClickHandler = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Bitmap capture = Bitmap.createBitmap(
                    mImage.getWidth(),
                    mImage.getHeight(),
                    Bitmap.Config.ARGB_8888
            );
            Canvas captureCanvas = new Canvas(capture);
            mImage.draw(captureCanvas);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] data = outputStream.toByteArray();

            String path = "firebase/images/" + UUID.randomUUID() + ".png";
            StorageReference firebaseImageRef = storage.getReference(path);
            StorageMetadata firebaseMetadata = new StorageMetadata.Builder()
                    .setCustomMetadata("someImportantKey", "someImportantValue")
                    .build();
            UploadTask uploadTask = firebaseImageRef.putBytes(data, firebaseMetadata);
            mUploadText.setVisibility(View.VISIBLE);
            mUploadProgressBar.setVisibility(View.VISIBLE);
            mUploadButton.setEnabled(false);

            uploadTask.addOnCompleteListener(MainActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onComplete(@NotNull Task<UploadTask.TaskSnapshot> task){
                    if (task.isSuccessful()){
                        mUploadText.setText(getText(R.string.upload_success));
                    }else{
                        mUploadText.setText(getText(R.string.upload_failure));
                    }
                    Log.i("UploadImage", "upload completed");
                    mUploadProgressBar.setVisibility(View.INVISIBLE);
                    mUploadButton.setEnabled(true);
                }
            });

        }
    };

    View.OnClickListener showImagesOnClickHandler = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

        }
    };

}