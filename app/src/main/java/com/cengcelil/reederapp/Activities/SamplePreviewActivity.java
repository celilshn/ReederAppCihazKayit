package com.cengcelil.reederapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.bumptech.glide.Glide;
import com.cengcelil.reederapp.Modal.DeviceInformation;
import com.cengcelil.reederapp.Modal.UserInformation;
import com.cengcelil.reederapp.R;
import com.cengcelil.reederapp.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.cengcelil.reederapp.Utils.IMAGE_1_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_2_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_3_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_4_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_5_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_6_CODE;
import static com.cengcelil.reederapp.Utils.IMAGE_EXTRA_CODE;
import static com.cengcelil.reederapp.Utils.getBackPressedBuilder;
import static com.cengcelil.reederapp.Utils.getCollectionFirstPreview;
import static com.cengcelil.reederapp.Utils.getCollectionLastPreview;
import static com.cengcelil.reederapp.Utils.getUserInformation;
import static com.cengcelil.reederapp.Utils.makeAlertDialog;
import static com.cengcelil.reederapp.Utils.storageReference;
import static com.cengcelil.reederapp.Utils.uiOff;

public class SamplePreviewActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FirstReview";
    private Toolbar toolbar;
    private EditText serviceIdEdittext, detectsEdittext;
    private Button noIssueButton, addIssueButton, addExtraPhotoButton;
    private TextView issuestextView;
    private UserInformation userInformation;
    private ScrollView scrollView;
    private LinearLayout extraPhotoLayout;
    private ProgressBar progressBar;
    private ImageView image1View, image2View, image3View, image4View, image5View, image6View;
    private Uri[] uriList = new Uri[6];
    private String[] pathList = new String[6];
    private ArrayList<Uri> extraUriList;
    private ArrayList<String> extraPathList;
    private ArrayList<String> serviceIdList;
    public static String path;
    private int count;
    private DeviceInformation deviceInformation;
    private ProgressDialog progressDialog;
    private boolean isFirst;
    private String serviceId;


    @Override
    public void onBackPressed() {
        getBackPressedBuilder(this).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_review);
        if (getIntent() != null) {
            isFirst = getIntent().getBooleanExtra("isFirst", true);
            serviceId = getIntent().getStringExtra("serviceId");
            if (isFirst)
                Log.d(TAG, "onCreate: Ön İnceleme Kontrol Sayfası Başladı.");
            else
                Log.d(TAG, "onCreate: Son İnceleme Kontrol Sayfası Başladı. Servis ID: " + serviceId);
        }
        viewSettings();
        uiOff(progressBar, scrollView);
        userInformation = getUserInformation(this);
        extraUriList = new ArrayList<>();
        extraPathList = new ArrayList<>();
        toolbarSettings();
        listenerSettings();
        setServiceIdList();
    }

    private void setServiceIdList() {
        serviceIdList = new ArrayList<>();
        getCollectionFirstPreview(this).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    DeviceInformation deviceInformation = snapshot.toObject(DeviceInformation.class);
                    serviceIdList.add(String.valueOf(deviceInformation.getServiceId()));
                }
                Log.d(TAG, "onCreate: AAA " + serviceIdList.toString());
            }
        });
    }

    private void viewSettings() {
        Log.d(TAG, "viewSettings: Görünümler ayarlanıyor..");
        toolbar = findViewById(R.id.toolbar2);
        serviceIdEdittext = findViewById(R.id.serviceIdEdittext);
        if (!isFirst) {
            serviceIdEdittext.setText(serviceId);
            serviceIdEdittext.setClickable(false);
            serviceIdEdittext.setFocusable(false);
            serviceIdEdittext.setAlpha((float) 0.5);
        }
        detectsEdittext = findViewById(R.id.detectsEdittext);
        noIssueButton = findViewById(R.id.noIssueButton);
        addIssueButton = findViewById(R.id.addIssueButton);
        addExtraPhotoButton = findViewById(R.id.addExtraPhotoButton);
        extraPhotoLayout = findViewById(R.id.extraPhotoLayout);
        issuestextView = findViewById(R.id.issuesTextView);
        scrollView = findViewById(R.id.scrollView);
        progressBar = findViewById(R.id.progressBar4);
        image1View = findViewById(R.id.image1);
        image2View = findViewById(R.id.image2);
        image3View = findViewById(R.id.image3);
        image4View = findViewById(R.id.image4);
        image5View = findViewById(R.id.image5);
        image6View = findViewById(R.id.image6);
    }

    private void toolbarSettings() {
        Log.d(TAG, "toolbarSettings: Toolbar ayarlanıyor..");

        if (isFirst)
            toolbar.setTitle(getString(R.string.reportFirstPreview));
        else
            toolbar.setTitle(getString(R.string.reportLastPreview));

        String subtitle = userInformation.getPersonalName() + " " + userInformation.getPersonalSurname();
        toolbar.setSubtitle("Merhaba " + subtitle);
        setSupportActionBar(toolbar);
        Utils.uiOn(progressBar, scrollView);
    }

    private void listenerSettings() {
        Log.d(TAG, "listenerSettings: Dinleyiciler ayarlanıyor..");
        addExtraPhotoButton.setOnClickListener(this);
        addIssueButton.setOnClickListener(this);
        noIssueButton.setOnClickListener(this);
        image1View.setOnClickListener(this);
        image2View.setOnClickListener(this);
        image3View.setOnClickListener(this);
        image4View.setOnClickListener(this);
        image5View.setOnClickListener(this);
        image6View.setOnClickListener(this);
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
        } else if (view.getId() == image1View.getId()) {
            Log.d(TAG, "onClick: Ön Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_1_CODE, this);
        } else if (view.getId() == image2View.getId()) {
            Log.d(TAG, "onClick: Arka Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_2_CODE, this);
        } else if (view.getId() == image3View.getId()) {
            Log.d(TAG, "onClick: Alt Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_3_CODE, this);
        } else if (view.getId() == image4View.getId()) {
            Log.d(TAG, "onClick: Üst Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_4_CODE, this);
        } else if (view.getId() == image5View.getId()) {
            Log.d(TAG, "onClick: Sol Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_5_CODE, this);
        } else if (view.getId() == image6View.getId()) {
            Log.d(TAG, "onClick: Sağ Taraf Fotoğrafı Eklenecek..");
            Utils.openFileChooser(IMAGE_6_CODE, this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Fotoğraf Dönüşü sağlandı");
        if (resultCode == RESULT_OK) {
            Bitmap photo = null;
            Log.d(TAG, "onActivityResult: Fotoğrafın yolu " + path);
            File file = new File(path);
            String fbStoragePath = file.getName();
            Uri uri = Uri.fromFile(file);
            try {
                photo = Bitmap.createScaledBitmap(MediaStore.Images.Media
                        .getBitmap(this.getContentResolver(), uri), image1View.getMeasuredWidth(), image1View.getMeasuredHeight(), false);
                Log.d(TAG, "onActivityResult: " + photo.getByteCount());

            } catch (IOException e) {
                Log.d(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
            if (requestCode == IMAGE_1_CODE) {
                uriList[0] = uri;
                pathList[0] = fbStoragePath;
                Glide.with(this).load(photo).into(image1View);
            }
            if (requestCode == IMAGE_2_CODE) {
                uriList[1] = uri;
                pathList[1] = fbStoragePath;

                Glide.with(this).load(photo).into(image2View);
            }
            if (requestCode == IMAGE_3_CODE) {
                uriList[2] = uri;
                pathList[2] = fbStoragePath;

                Glide.with(this).load(photo).into(image3View);
            }
            if (requestCode == IMAGE_4_CODE) {
                uriList[3] = uri;
                pathList[3] = fbStoragePath;

                Glide.with(this).load(photo).into(image4View);
            }
            if (requestCode == IMAGE_5_CODE) {
                uriList[4] = uri;
                pathList[4] = fbStoragePath;

                Glide.with(this).load(photo).into(image5View);
            }
            if (requestCode == IMAGE_6_CODE) {
                uriList[5] = uri;
                pathList[5] = fbStoragePath;

                Glide.with(this).load(photo).into(image6View);
            }
            if (requestCode == IMAGE_EXTRA_CODE) {
                extraUriList.add(uri);
                extraPathList.add(fbStoragePath);
                extraPhotoLayout.addView(addLayout(photo, uri), 0);
            }


        }

    }

    private LinearLayout addLayout(final Bitmap photo, final Uri uri) {
        Log.d(TAG, "addLayout: Ekstra fotoğraf bölgesi ayrılıyor..");
        final LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500);
        layoutParams.setMargins(0, 5, 0, 0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setWeightSum(4);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        Glide.with(this).load(photo).into(imageView);
        linearLayout.addView(imageView);

        MaterialButton button = new MaterialButton(this, null, R.attr.materialButtonStyle);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 3));
        button.setText("Kaldır");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraPhotoLayout.removeView(linearLayout);
                extraPathList.remove(extraUriList.indexOf(uri));
                extraUriList.remove(uri);
                Log.d(TAG, "onClick: " + extraUriList.size());
            }
        });
        linearLayout.addView(button);
        return linearLayout;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Cevap alındı");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Ekleme işlemine devam ediniz..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Menü layoutu ayarlanıyor..");
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean b = false;
        boolean c = false;
        String serviceIdS = serviceIdEdittext.getText().toString().trim();
        String detectsS = detectsEdittext.getText().toString().trim();
        String issuesS = issuestextView.getText().toString().trim();
        for (String s : serviceIdList) {
            c = serviceIdS.equals(s);
            break;
        }

        for (Uri uri : uriList) {
            if (uri == null) {
                b = true;
                break;
            }
        }
        if (serviceIdS.equals("")) {
            Log.d(TAG, "onOptionsItemSelected: Cihaz id belirtilmedi..");
            serviceIdEdittext.setError("Boş bırakılamaz");
            Toast.makeText(this, "Servis Id boş bırakılamaz", Toast.LENGTH_SHORT).show();
        } else if (c) {
            serviceIdEdittext.setError("Bu Id daha önce kullanılmış.");
            Toast.makeText(this, "Bu Id daha önce kullanılmış", Toast.LENGTH_SHORT).show();
        } else if (issuesS.equals("")) {
            Log.d(TAG, "onOptionsItemSelected: Cihaz sorunu belirtilmedi..");
            Toast.makeText(this, "Cihaz sorunu belirtiniz", Toast.LENGTH_SHORT).show();
        } else if (b) {
            Log.d(TAG, "onOptionsItemSelected: Eksik fotoğraf girişi..");
            Toast.makeText(this, "Tüm fotoğrafları girdiğinize emin olun.", Toast.LENGTH_SHORT).show();

        } else {
            Log.d(TAG, "onOptionsItemSelected: Veriler Sunucuya aktarılacak..");
            deviceInformation = new DeviceInformation(Integer.parseInt(serviceIdS), detectsS, issuesS, userInformation.getuId());
            Utils.uiOff(progressBar, scrollView);
            deviceInformation.setFrontSide(pathList[0]);
            deviceInformation.setBackSide(pathList[1]);
            deviceInformation.setBottomSide(pathList[2]);
            deviceInformation.setTopSide(pathList[3]);
            deviceInformation.setLeftSide(pathList[4]);
            deviceInformation.setRightSide(pathList[5]);
            deviceInformation.setExtraPhotos(extraPathList);
            count = 0;
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Değşiklikler yapılıyor..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            for (int i = 0; i < pathList.length; i++)
                uploadImages(pathList[i], uriList[i]);
            for (int i = 0; i < extraUriList.size(); i++)
                uploadImages(extraPathList.get(i), extraUriList.get(i));


        }
        return true;
    }

    private void uploadImages(String path, Uri uri) {
        storageReference.child("uploads/" + path).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.setProgress(count * 100 / (pathList.length + extraPathList.size()));
                progressDialog.setMessage(count + " / " + (pathList.length + extraPathList.size()) + " Sunucuya Yüklendi");
                Log.d(TAG, "onSuccess: " + count + " veri sunucuya aktarıldı.");

                count++;
                if (count == pathList.length + extraUriList.size()) {
                    if (isFirst) {
                        getCollectionFirstPreview(SamplePreviewActivity.this).document().set(deviceInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SamplePreviewActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Utils.uiOn(progressBar, scrollView);
                                    Toast.makeText(SamplePreviewActivity.this, "Kayıt Başarısız. " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    } else {
                        getCollectionLastPreview(SamplePreviewActivity.this).document().set(deviceInformation).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SamplePreviewActivity.this, "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Utils.uiOn(progressBar, scrollView);
                                    Toast.makeText(SamplePreviewActivity.this, "Kayıt Başarısız. " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                }
            }
        });
    }
}

