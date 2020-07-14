package com.cengcelil.reederapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cengcelil.reederapp.Activities.SamplePreviewActivity;
import com.cengcelil.reederapp.Activities.LastReviewBridgeActivity;
import com.cengcelil.reederapp.Activities.MyPreviewsActivity;
import com.cengcelil.reederapp.Activities.MainActivity;
import com.cengcelil.reederapp.Modal.UserClient;
import com.cengcelil.reederapp.Modal.UserInformation;
import com.cengcelil.reederapp.Utils;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MenuFragment";
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private Button logoutButton, newFrontPreviewButton, newLastPreviewButton, allPreviewsButton;
    private ProgressBar progressBar;
    private LinearLayout uiLayout;
    private ActionBar actionBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Menü Başladı");
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.uiOff(progressBar, uiLayout);
        final Context context = getActivity().getApplicationContext();
        if (firebaseUser != null) {
            Log.d(TAG, "onActivityCreated: Kullanıcı bilgisi alınıyor");
            DocumentReference userRef = firebaseFirestore.collection(getString(R.string.userCollection)).document(firebaseUser.getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            Log.d(TAG, "onComplete: Sorgu cevabı başarıyla anlındı");
                            UserInformation userInformation = task.getResult().toObject(UserInformation.class);
                            if (userInformation != null) {
                                ((UserClient) (context)).setUserInformation(userInformation);
                                Log.d(TAG, "onComplete: Veri UserInformation sınıfına dönüştürüldü");
                                actionBar.setTitle(R.string.productReview);
                                actionBar.setSubtitle("Merhaba " + userInformation.getPersonalName() + " " + userInformation.getPersonalSurname());
                                Log.d(TAG, "onComplete: Toolbar başlığı eklendi.");
                                Utils.uiOn(progressBar, uiLayout);

                            }
                        }
                    } else Log.d(TAG, "onComplete: Sorgu başarısız");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        Log.d(TAG, "onCreateView: Görünümler ayarlanıyor..");
        logoutButton = view.findViewById(R.id.logoutButton);
        newFrontPreviewButton = view.findViewById(R.id.newFrontPreviewButton);
        newLastPreviewButton = view.findViewById(R.id.newLastPreviewButton);
        allPreviewsButton = view.findViewById(R.id.allPreviewsButton);

        progressBar = view.findViewById(R.id.progressBar2);
        uiLayout = view.findViewById(R.id.uiLayout2);


        logoutButton.setOnClickListener(this);
        newFrontPreviewButton.setOnClickListener(this);
        newLastPreviewButton.setOnClickListener(this);
        allPreviewsButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == logoutButton.getId()) {
            Log.d(TAG, "onClick: Çıkış yapılacak..");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        } else if (id == newFrontPreviewButton.getId()) {
            Log.d(TAG, "onClick: Ön İnceleme Sayfasına Yönlendiriliyor..");
            Intent i = new Intent(getActivity(), SamplePreviewActivity.class);
            i.putExtra("isFirst",true);
            startActivity(i);
        } else if (id == newLastPreviewButton.getId()) {
            Log.d(TAG, "onClick: Son İnceleme Seçim Sayfasına Yönlendiriliyor..");

            startActivity(new Intent(getActivity(), LastReviewBridgeActivity.class));
        } else if (id == allPreviewsButton.getId()) {
            Log.d(TAG, "onClick: Tüm incelemelerim Sayfasına Yönlendiriliyor..");
            startActivity(new Intent(getActivity(), MyPreviewsActivity.class));
        }
    }
}
