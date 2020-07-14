package com.cengcelil.reederapp.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.cengcelil.reederapp.Fragments.LoginFragment;
import com.cengcelil.reederapp.Fragments.MenuFragment;
import com.cengcelil.reederapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseUser firebaseUser;
    private Toolbar toolbar;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Auth durumu sorgulanacak");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Log.d(TAG, "onStart: Kullanıcı tanımlı değil");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment()).commit();
        } else {
            Log.d(TAG, "onStart: Kullanıcı menüye yönlendiriliyor");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MenuFragment()).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
