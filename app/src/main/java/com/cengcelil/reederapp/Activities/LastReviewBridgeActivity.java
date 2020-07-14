package com.cengcelil.reederapp.Activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cengcelil.reederapp.Adapter.LastPreviewsRecyclerAdapter;
import com.cengcelil.reederapp.Modal.DeviceInformation;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.cengcelil.reederapp.Utils.getCollectionFirstPreview;
import static com.cengcelil.reederapp.Utils.getCollectionLastPreview;
import static com.cengcelil.reederapp.Utils.getUserInformation;

public class LastReviewBridgeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "LastReviewBridgeActivit";
    private ArrayList<Integer> items;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private LastPreviewsRecyclerAdapter lastPreviewsRecyclerAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_review_bridge);
        Log.d(TAG, "onCreate: Aktvite Başladı .Görünümler Ayarlanıyor..");
        toolbar = findViewById(R.id.toolbar);
        toolbarSettings();
        recyclerView = findViewById(R.id.recycler);
        initializations();
    }
    private void toolbarSettings() {
        Log.d(TAG, "toolbarSettings: Toolbar ayarlanıyor..");
        toolbar.setTitle(R.string.lastPreviewsText);
        String subtitle = getUserInformation(this).getPersonalName() + " " + getUserInformation(this).getPersonalSurname();
        toolbar.setSubtitle("Merhaba " + subtitle);
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Veriler yeniden yüklenecek..");
        progressDialog.show();
        getLastPreviews();
    }

    private void getLastPreviews() {
        Log.d(TAG, "getLastPreviews: Veriler yükleniyor..");
        items.clear();
        getCollectionFirstPreview(this).whereEqualTo("uId",getUserInformation(this).getuId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Log.d(TAG, "onSuccess: Ön incelemesi mevcut dökümanların listesi alınıyor..");
                    DeviceInformation deviceInformation = snapshot.toObject(DeviceInformation.class);
                    items.add(deviceInformation.getServiceId());
                }
                getCollectionLastPreview(LastReviewBridgeActivity.this).whereEqualTo("uId",getUserInformation(LastReviewBridgeActivity.this).getuId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Log.d(TAG, "onSuccess: Son incelemesi mevcut dökümanların listesi alınıyor..");
                            DeviceInformation deviceInformation = snapshot.toObject(DeviceInformation.class);
                            if (items.contains(deviceInformation.getServiceId()))
                                items.remove(Integer.valueOf(deviceInformation.getServiceId()));
                        }
                        lastPreviewsRecyclerAdapter.setItems(items);
                        Log.d(TAG, "onSuccess: Değişiklikler uygulanacak..");
                        lastPreviewsRecyclerAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Toolbar özellikleri ayarlanıyor..");
        getMenuInflater().inflate(R.menu.toolbar_menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint(getString(R.string.action_search));
        TextView et = searchView.findViewById(R.id.search_src_text);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
        et.setTextColor(Color.WHITE);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializations() {
        Log.d(TAG, "initializations: İtemler ayarlanıyor..");
        items = new ArrayList<>();
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Veriler Güncelleniyor..");
        progressDialog.show();
        lastPreviewsRecyclerAdapter = new LastPreviewsRecyclerAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(lastPreviewsRecyclerAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: Arama Sözcüğü değişti.Sözcük : "+newText);
        ArrayList<Integer> newItems = new ArrayList<>();

        for (Integer item : items) {
            if (String.valueOf(item).contains(newText))
                newItems.add(item);
        }
        lastPreviewsRecyclerAdapter.setItems(newItems);

        lastPreviewsRecyclerAdapter.notifyDataSetChanged();
        return true;
    }

}
