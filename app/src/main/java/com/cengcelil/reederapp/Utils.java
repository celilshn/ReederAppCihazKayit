package com.cengcelil.reederapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;

import com.cengcelil.reederapp.Activities.CheckSamplePreviewActivity;
import com.cengcelil.reederapp.Activities.SamplePreviewActivity;
import com.cengcelil.reederapp.Modal.DeviceInformation;
import com.cengcelil.reederapp.Modal.UserClient;
import com.cengcelil.reederapp.Modal.UserInformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Utils {
    private static final String TAG = "Utils";
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String WRITE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public static final int WRITE_PERMISSION_REQUEST_CODE = 799;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 800;
    public static final int IMAGE_1_CODE = 801;
    public static final int IMAGE_2_CODE = 802;
    public static final int IMAGE_3_CODE = 803;
    public static final int IMAGE_4_CODE = 804;
    public static final int IMAGE_5_CODE = 805;
    public static final int IMAGE_6_CODE = 806;
    public static final int IMAGE_EXTRA_CODE = 807;

    public static File createImageFile(Activity activity) throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    public static void uiOff(ProgressBar progressBar, ViewGroup uiLayout) {
        progressBar.setVisibility(View.VISIBLE);
        uiLayout.setAlpha((float) 0.5);
        uiLayout.setClickable(false);
    }

    public static void uiOn(ProgressBar progressBar, ViewGroup uiLayout) {
        progressBar.setVisibility(View.GONE);
        uiLayout.setAlpha(1);
        uiLayout.setClickable(true);
    }

    public static void openFileChooser(final int PICK_IMAGE_REQUEST, final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(activity, WRITE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePicture.resolveActivity(activity.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        File file = createImageFile(activity);
                        photoFile = file;
                        SamplePreviewActivity.path = file.getAbsolutePath();
                        CheckSamplePreviewActivity.path = file.getAbsolutePath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(activity,
                                "com.cengcelil.reederapp.fileprovider",
                                photoFile);
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        activity.startActivityForResult(takePicture, PICK_IMAGE_REQUEST);
                    }
                }
            } else
                ActivityCompat.requestPermissions(activity, new String[]{WRITE_PERMISSION}, WRITE_PERMISSION_REQUEST_CODE);
        } else
            ActivityCompat.requestPermissions(activity, new String[]{CAMERA_PERMISSION}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    public static CollectionReference getCollectionFirstPreview(Activity activity) {
        return FirebaseFirestore.getInstance().collection(activity.getString(R.string.firstPreviews));
    }

    public static CollectionReference getCollectionLastPreview(Activity activity) {
        return FirebaseFirestore.getInstance().collection(activity.getString(R.string.lastPreviews));
    }

    public static UserInformation getUserInformation(Activity activity) {
        return ((UserClient) (activity.getApplicationContext())).getUserInformation();
    }

    public static void makeAlertDialog(Activity activity, final TextView textView) {
        final EditText issuesEdittext = new EditText(activity);
        issuesEdittext.setText(textView.getText().toString().trim());
        issuesEdittext.setLines(10);
        issuesEdittext.setMaxLines(15);
        issuesEdittext.setMinLines(5);
        issuesEdittext.setGravity(GravityCompat.START | Gravity.TOP);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.issues))
                .setView(issuesEdittext)
                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String issuesS = issuesEdittext.getText().toString().trim();
                        textView.setText(issuesS);
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create();
        builder.show();
    }

    public static AlertDialog.Builder getBackPressedBuilder(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage("Değişiklikler kaydedilmeden çıkılacak !")
                .setNegativeButton("Geri Dön", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Sayfadan Çık", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();

                    }
                });
        return builder;
    }

}
