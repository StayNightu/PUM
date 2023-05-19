package com.example.mematicpum;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button mUploadButton;

    Button mShareButton;
    Button mSelectPictureButton;
    Button mShowImagesButton;
    ImageView mImage;
    TextView mUploadText;
    ProgressBar mUploadProgressBar;
    MemeListAdapter memeListAdapter;
    TextView mShowListText;
    ProgressBar mShowListProgressBar;
    RecyclerView recyclerView;
    boolean isRecyclerViewListShown = false;
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final int REQUEST_IMAGE_PICKER = 1;

    private Uri getImageUriFromDrawable(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mImage = findViewById(R.id.uploadImageContainer);
        mUploadText = findViewById(R.id.uploadTextView);
        mUploadProgressBar = findViewById(R.id.uploadProgressBar);
        mShowListText = findViewById(R.id.showListTextView);
        mShowListProgressBar = findViewById(R.id.showListProgressBar);
        mUploadButton = findViewById(R.id.uploadButton);
        mUploadButton.setOnClickListener(uploadImageOnClickHandler);
        mShowImagesButton = findViewById(R.id.showImagesButton);
        mShowImagesButton.setOnClickListener(showImagesOnClickHandler);
        mSelectPictureButton = findViewById(R.id.SelectPicture);
        mSelectPictureButton.setOnClickListener(SelectPictureOnClickHandler);
        mShareButton = findViewById(R.id.ShareImagesButton);
        mShareButton.setOnClickListener(ShareImagesOnClickHandler);
        recyclerView = findViewById(R.id.recyclerViewMemeList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        ArrayList<MemeListItem> items = new ArrayList<MemeListItem>();
        memeListAdapter = new MemeListAdapter(items, mImage);
        recyclerView.setAdapter(memeListAdapter);

        Button editImageButton = findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(editImageOnClickHandler);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            mImage.setImageURI(selectedImageUri);
        }
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
            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            if (isRecyclerViewListShown){
                //hide
                params.height = (int)(1 * scale + 0.5f);
                recyclerView.setLayoutParams(params);
                isRecyclerViewListShown = false;
                mShowImagesButton.setText("Show images");
                return;
            }
            StorageReference listRef = storage.getReference().child("firebase/images");

            Task<ListResult> getListTask = listRef.listAll();
            mShowListText.setVisibility(View.VISIBLE);
            mShowListProgressBar.setVisibility(View.VISIBLE);
            mShowImagesButton.setEnabled(false);
            params.height = (int)(200 * scale + 0.5f);
            recyclerView.setLayoutParams(params);
            isRecyclerViewListShown = true;
            mShowImagesButton.setText("Hide images");
            getListTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    mShowListText.setText(String.format("Załadowano %s elementów", listResult.getItems().size()));
//                    mShowListText.setVisibility(View.INVISIBLE);
                    mShowListProgressBar.setVisibility(View.INVISIBLE);
                    mShowImagesButton.setEnabled(true);
//                            for (StorageReference prefix : listResult.getPrefixes()) {
//                                // All the prefixes under listRef.
//                                // You may call listAll() recursively on them.
//                            }

                    for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                        try{
                            File localFile = File.createTempFile("tempFile", ".png");
                            item.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            MemeListItem memeItem = new MemeListItem(localFile.getName(), bitmap);
                                            memeListAdapter.addItem(memeItem);
                                            memeListAdapter.notifyItemInserted(memeListAdapter.getItemCount());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("MemeListAdapter", "onSuccess: Got Failture when getting file");
                                        }
                                    });
                        }catch (IOException e){
                            Log.d("MemeListAdapter", "onSuccess: Got Exception");
                            mShowListText.setText("Nie można załadować.");
                            mShowListText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mShowListText.setText("Nie można załadować.");
                    mShowListProgressBar.setVisibility(View.INVISIBLE);
                    mShowImagesButton.setEnabled(true);
                }
            });
        }
    };

    View.OnClickListener ShareImagesOnClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Drawable imageDrawable = mImage.getDrawable();
            if (imageDrawable != null) {
                Uri selectedImageUri = getImageUriFromDrawable(imageDrawable);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, selectedImageUri);

                startActivity(Intent.createChooser(shareIntent, "Share Image"));
            } else {
            }
        }
    };
    View.OnClickListener SelectPictureOnClickHandler = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_PICKER);
        }
    };
    View.OnClickListener editImageOnClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Drawable imageDrawable = mImage.getDrawable();
            if (imageDrawable == null) {
                return;
            }

            Uri selectedImageUri = getImageUriFromDrawable(imageDrawable);
            Intent editIntent = new Intent(Intent.ACTION_EDIT);
            editIntent.setDataAndType(selectedImageUri, "image/*");
            editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(editIntent, "Edit Image"));
        }
    };

}