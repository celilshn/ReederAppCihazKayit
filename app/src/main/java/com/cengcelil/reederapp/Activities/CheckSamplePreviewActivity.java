package com.cengcelil.reederapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.cengcelil.reederapp.Modal.DeviceInformation;
import com.cengcelil.reederapp.Utils;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.cengcelil.reederapp.Utils.IMAGE_EXTRA_CODE;
import static com.cengcelil.reederapp.Utils.getBackPressedBuilder;
import static com.cengcelil.reederapp.Utils.getCollectionFirstPreview;
import static com.cengcelil.reederapp.Utils.getCollectionLastPreview;
import static com.cengcelil.reederapp.Utils.getUserInformation;
import static com.cengcelil.reederapp.Utils.makeAlertDialog;
import static com.cengcelil.reederapp.Utils.storageReference;
import static com.cengcelil.reederapp.Utils.uiOff;
import static com.cengcelil.reederapp.Utils.uiOn;

public class CheckSamplePreviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CheckFirstReviewActivit";
    private int serviceId;
    private Toolbar toolbar;
    private EditText detectsEdittext;
    private Button noIssueButton, addIssueButton, addExtraPhotoButton;
    private TextView issuestextView, serviceIdTextview;
    private ScrollView scrollView;
    private LinearLayout extraPhotoLayout;
    private ProgressBar progressBar;
    private ImageView image1View, image2View, image3View, image4View, image5View, image6View;
    private ArrayList<String> extraPathList, extra2PathList;
    private ArrayList<Uri> extra2UriList;
    private String documentId;
    private DeviceInformation deviceInformation;
    public static String path;
    private int count;
    private boolean isFirst;
    private ProgressDialog progressDialog;
    private CircularProgressDrawable circularProgressDrawable;

    @Override
    public void onBackPressed() {
        getBackPressedBuilder(this).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_sample_preview);
        if (getIntent() != null) {
            isFirst = getIntent().getBooleanExtra("isFirst", true);
            serviceId = getIntent().getIntExtra("serviceId", 0);
            if (isFirst)
                Log.d(TAG, "onCreate: Ön İnceleme Kontrol Sayfası Başladı. Servis ID: " + serviceId);
            else
                Log.d(TAG, "onCreate: Son İnceleme Kontrol Sayfası Başladı. Servis ID: " + serviceId);
        }

        viewSettings();
        uiOff(progressBar, scrollView);
        init();
        toolbarSettings();
        listenerSettings();
        getDeviceInformation();
    }

    private void init() {
        extra2PathList = new ArrayList<>();
        extra2UriList = new ArrayList<>();
        circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();
    }

    private void getDeviceInformation() {
        Query query;
        if (isFirst)
            query = Utils.getCollectionFirstPreview(this).whereEqualTo("serviceId", serviceId).whereEqualTo("uId", getUserInformation(this).getuId());
        else
            query = Utils.getCollectionLastPreview(this).whereEqualTo("serviceId", serviceId).whereEqualTo("uId", getUserInformation(this).getuId());
        Log.d(TAG, "getDeviceInformation: Cihaz bilgisi alınıyor..");
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    documentId = snapshot.getId();
                    Log.d(TAG, "getDeviceInformation: Cihaz bilgisi alındı. Döküman Numarası: " + documentId);
                    deviceInformation = snapshot.toObject(DeviceInformation.class);
                    serviceIdTextview.setText(String.valueOf(serviceId));
                    detectsEdittext.setText(deviceInformation.getDetections());
                    issuestextView.setText(deviceInformation.getIssues());
                    Log.d(TAG, "getDeviceInformation: Cihazın fotoğrafları sunucudan indiriliyor.. ");
                    downloadImages(deviceInformation.getFrontSide(), image1View);
                    downloadImages(deviceInformation.getBackSide(), image2View);
                    downloadImages(deviceInformation.getBottomSide(), image3View);
                    downloadImages(deviceInformation.getTopSide(), image4View);
                    downloadImages(deviceInformation.getLeftSide(), image5View);
                    downloadImages(deviceInformation.getRightSide(), image6View);
                    extraPathList = deviceInformation.getExtraPhotos();
                    for (String path : extraPathList) {
                        extraPhotoLayout.addView(addLayout(null, path, null), 0);
                    }
                    uiOn(progressBar, scrollView);
                }
            }
        });
    }


    private void toolbarSettings() {
        Log.d(TAG, "toolbarSettings: Toolbar ayarlanıyor..");
        if (isFirst)
            toolbar.setTitle(getString(R.string.reportFirstPreviewControl));
        else
            toolbar.setTitle(getString(R.string.reportLastPreviewControl));
        String subtitle = getUserInformation(this).getPersonalName() + " " + getUserInformation(this).getPersonalSurname();
        toolbar.setSubtitle("Merhaba " + subtitle);
        setSupportActionBar(toolbar);
    }


    private void listenerSettings() {
        Log.d(TAG, "listenerSettings: Dinleyiciler ayarlanıyor..");
        addExtraPhotoButton.setOnClickListener(this);
        addIssueButton.setOnClickListener(this);
        noIssueButton.setOnClickListener(this);
    }

    private LinearLayout addLayout(final Bitmap photo, final String path, final Uri uri) {
        Log.d(TAG, "addLayout: Ekstra fotoğraf bölgesi ayrılıyor..");
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        layoutParams.setMargins(0,5,0,0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setWeightSum(4);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        if (photo == null)
            downloadImages(path, imageView);
        else
            imageView.setImageBitmap(photo);
        linearLayout.addView(imageView);

        MaterialButton button = new MaterialButton(this, null, R.attr.materialButtonStyle);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
        button.setText("Kaldır");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraPhotoLayout.removeView(linearLayout);
                if (photo == null)
                    extraPathList.remove(path);
                else {
                    extra2PathList.remove(extra2UriList.indexOf(uri));
                    extra2UriList.remove(uri);
                }
            }
        });
        linearLayout.addView(button);
        return linearLayout;
    }

    private void downloadImages(String path, final ImageView imageView) {
        Log.d(TAG, "downloadImages: " + path + "indirilecek..");
        storageReference.child("uploads/" + path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(String.valueOf(uri))
                        .placeholder(circularProgressDrawable)
                        .into(imageView);
            }
        });
    }

    private void viewSettings() {
        Log.d(TAG, "viewSettings: Görünümler ayarlanıyor..");
        toolbar = findViewById(R.id.toolbar2);
        serviceIdTextview = findViewById(R.id.serviceIdTextview);
        detectsEdittext = findViewById(R.id.detectsEdittext);
        addIssueButton = findViewById(R.id.addIssueButton);
        noIssueButton = findViewById(R.id.noIssueButton);
        issuestextView = findViewById(R.id.issuesTextView);
        scrollView = findViewById(R.id.scrollView);
        extraPhotoLayout = findViewById(R.id.extraPhotoLayout);
        addExtraPhotoButton = findViewById(R.id.addExtraPhotoButton);
        progressBar = findViewById(R.id.progressBar4);
        image1View = findViewById(R.id.image1);
        image2View = findViewById(R.id.image2);
        image3View = findViewById(R.id.image3);
        image4View = findViewById(R.id.image4);
        image5View = findViewById(R.id.image5);
        image6View = findViewById(R.id.image6);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == addExtraPhotoButton.getId()) {
            Log.d(TAG, "onClick: Ekstra Fotoğraf Eklenecek..");
            Utils.openFileChooser(IMAGE_EXTRA_CODE, this);
        } else if (view.getId() == addIssueButton.getId()) {
            Log.d(TAG, "onClick: Sorunların Belirtileceği Diyalog Penceresi açıacak..");
            makeAlertDialog(this, issuestextView);
        } else if (view.getId() == noIssueButton.getId()) {
            Log.d(TAG, "onClick: Sorun eklenmedi..");
            issuestextView.setText("Sorun Yok");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Fotoğraf Dönüşü sağlandı");
        if (resultCode == RESULT_OK) {
            Bitmap photo = null;
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            Log.d(TAG, "onActivityResult: Fotoğrafın yolu " + path);
            try {
                photo = Bitmap.createScaledBitmap(MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), uri), image1View.getMeasuredWidth(), image1View.getMeasuredHeight(), false);

            } catch (IOException e) {
                Log.d(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
            if (IMAGE_EXTRA_CODE == requestCode)
                if (photo != null) {
                    extra2UriList.add(uri);
                    extra2PathList.add(path);
                    extraPhotoLayout.addView(addLayout(photo, null, uri), 0);

                }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Menü layoutu ayarlanıyor..");
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String detectsS = detectsEdittext.getText().toString().trim();
        String issuesS = issuestextView.getText().toString().trim();

        if (issuesS.equals("")) {
            Log.d(TAG, "onOptionsItemSelected: Cihaz sorunu belirtilmedi..");
            Toast.makeText(this, "Cihaz sorunu belirtiniz", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onOptionsItemSelected: Veriler Sunucuya aktarılacak..");
            Utils.uiOff(progressBar, scrollView);
            deviceInformation.setDetections(detectsS);
            deviceInformation.setIssues(issuesS);
            extraPathList.addAll(extra2PathList);
            deviceInformation.setExtraPhotos(extraPathList);
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Değişiklikler yapılıyor..");
            progressDialog.setCancelable(false);
            progressDialog.show();
            count = 0;
            if (extra2PathList.size() != 0)
                for (int i = 0; i < extra2UriList.size(); i++)
                    uploadImages(extra2PathList.get(i), extra2UriList.get(i));
            else {
                saveDocument(documentId,deviceInformation);
            }
        }
        return true;
    }

    private void saveDocument(String documentId, DeviceInformation deviceInformation) {
        if (isFirst) {
            getCollectionFirstPreview(CheckSamplePreviewActivity.this).document(documentId).set(deviceInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CheckSamplePreviewActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Utils.uiOn(progressBar, scrollView);
                        Toast.makeText(CheckSamplePreviewActivity.this, "Kayıt Başarısız. " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        } else {
            getCollectionLastPreview(CheckSamplePreviewActivity.this).document(documentId).set(deviceInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CheckSamplePreviewActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Utils.uiOn(progressBar, scrollView);
                        Toast.makeText(CheckSamplePreviewActivity.this, "Kayıt Başarısız. " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    private void uploadImages(String path, Uri uri) {
        storageReference.child("uploads/" + path).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "onSuccess: " + count + " veri sunucuya aktarıldı.");
                progressDialog.setProgress(count * 100 / (extra2UriList.size()));
                progressDialog.setMessage(count + " / " + (extra2UriList.size()) + " Sunucuya Yüklendi");
                count++;
                if (count == extra2UriList.size()) {
                    saveDocument(documentId,deviceInformation);
                }

            }
        });
    }
}

