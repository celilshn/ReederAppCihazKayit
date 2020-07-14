package com.cengcelil.reederapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.cengcelil.reederapp.Utils;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private Button loginButton;
    private EditText mailEdittext, pwdEdittext;
    private ConstraintLayout uiLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Log.d(TAG, "onCreateView: oluşturuldu.");
        loginButton = view.findViewById(R.id.loginButton);
        mailEdittext = view.findViewById(R.id.mailEditText);
        pwdEdittext = view.findViewById(R.id.pwdEditText);
        uiLayout = view.findViewById(R.id.uiLayout);
        progressBar = view.findViewById(R.id.progressBar);
        loginButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == loginButton.getId()) {
            Log.d(TAG, "onClick: Giriş yapılıyor..");
            String sMail = mailEdittext.getText().toString().trim();
            String sPwd = pwdEdittext.getText().toString().trim();

            if (sMail.equals("")) {
                Log.d(TAG, "onClick: Mail alanı boş.");
                Toast.makeText(getActivity(), "Bir mail adresi giriniz.", Toast.LENGTH_SHORT).show();
            } else if (sPwd.equals("")) {
                Log.d(TAG, "onClick: Şifre alanı boş");
                Toast.makeText(getActivity(), "Bir şifre giriniz", Toast.LENGTH_SHORT).show();
            } else {
                Utils.uiOff(progressBar, uiLayout);
                Log.d(TAG, "onClick: Sunucuyla bağlantı kuruluyor.; '");
                FirebaseAuth.getInstance().signInWithEmailAndPassword(sMail, sPwd)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: Giriş başarılı. Menüye yönlendiriliyor.");
                                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Giriş başarısız.");
                        Utils.uiOn(progressBar, uiLayout);
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            Log.d(TAG, "onFailure: FirebaseAuthInvalidUserException");
                            Toast.makeText(getActivity(), "Kullanıcı bulunamadı. Lütfen sağlayıcınzıla iletişime geçiniz..q", Toast.LENGTH_SHORT).show();
                        }
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.d(TAG, "onFailure: FirebaseAuthInvalidCredentialsException");
                            Toast.makeText(getActivity(), "Şifre geçersiz. Lütfen geçerli bir şifre giriniz..", Toast.LENGTH_SHORT).show();
                        }
                        if (e instanceof FirebaseNetworkException) {
                            Log.d(TAG, "onFailure: FirebaseNetworkException");
                            Toast.makeText(getActivity(), "İnternet bağlantınızı kontrol ediniz. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
