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

import com.cengcelil.reederapp.Adapter.MyPreviewsRecyclerAdapter;
import com.cengcelil.reederapp.Modal.DeviceInformation;
import com.cengcelil.reederapp.Modal.MyPreviewsRecyclerItem;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.cengcelil.reederapp.Utils.getCollectionFirstPreview;
import static com.cengcelil.reederapp.Utils.getCollectionLastPreview;
import static com.cengcelil.reederapp.Utils.getUserInformation;

public class MyPreviewsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MyPreviewsActivity";
    private ArrayList<DeviceInformation> firstPreviews, lastPreviews;
    private ArrayList<MyPreviewsRecyclerItem> items;
    private MyPreviewsRecyclerAdapter myPreviewsRecyclerAdapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_previews);
        Log.d(TAG, "onCreate: Aktvite Başladı .Görünümler Ayarlanıyor..");

        toolbar = findViewById(R.id.toolbar3);
        toolbarSettings();
        recyclerView = findViewById(R.id.recycler);
        initializations();
    }

    private void toolbarSettings() {
        Log.d(TAG, "toolbarSettings: Toolbar ayarlanıyor..");
        toolbar.setTitle(R.string.myPreviewsText);
        String subtitle = getUserInformation(this).getPersonalName() + " " + getUserInformation(this).getPersonalSurname();
        toolbar.setSubtitle("Merhaba " + subtitle);
        setSupportActionBar(toolbar);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Veriler yeniden yüklenecek..");
        getPreviews();
    }

    private void getPreviews() {
        Log.d(TAG, "getPreviews: Veriler yükleniyor..");
        firstPreviews.clear();
        lastPreviews.clear();
        items.clear();
        getCollectionFirstPreview(this).whereEqualTo("uId", getUserInformation(this).getuId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Log.d(TAG, "onSuccess: Ön incelemesi mevcut dökümanların listesi alınıyor..");
                    DeviceInformation deviceInformation = snapshot.toObject(DeviceInformation.class);
                    firstPreviews.add(deviceInformation);
                }
                getCollectionLastPreview(MyPreviewsActivity.this).whereEqualTo("uId", getUserInformation(MyPreviewsActivity.this).getuId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: Son incelemesi mevcut dökümanların listesi alınıyor..");
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            DeviceInformation deviceInformation = snapshot.toObject(DeviceInformation.class);
                            lastPreviews.add(deviceInformation);
                        }
                        for (DeviceInformation dIFirstPreview : firstPreviews) {
                            if (lastPreviews.size() != 0) {
                                for (int i = 0; i < lastPreviews.size(); i++) {
                                    DeviceInformation dILastPreview = lastPreviews.get(i);
                                    if (dIFirstPreview.getServiceId() == dILastPreview.getServiceId()) {
                                        items.add(new MyPreviewsRecyclerItem(dIFirstPreview.getServiceId(), dIFirstPreview, dILastPreview));
                                        break;
                                    } else if (dILastPreview == lastPreviews.get(lastPreviews.size() - 1)) {
                                        items.add(new MyPreviewsRecyclerItem(dIFirstPreview.getServiceId(), dIFirstPreview, null));
                                    }

                                }
                            } else
                                items.add(new MyPreviewsRecyclerItem(dIFirstPreview.getServiceId(), dIFirstPreview, null));

                        }
                        Log.d(TAG, "onSuccess: Değişiklikler Uygulanacak..");
                        myPreviewsRecyclerAdapter.notifyDataSetChanged();
                        progressDialog.hide();
                    }
                });
            }
        });
    }

    private void initializations() {
        Log.d(TAG, "initializations: İtemler ayarlanıyor..");

        setSupportActionBar(toolbar);
        firstPreviews = new ArrayList<>();
        lastPreviews = new ArrayList<>();
        items = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Veriler Güncelleniyor..");
        progressDialog.show();
        myPreviewsRecyclerAdapter = new MyPreviewsRecyclerAdapter(this, items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myPreviewsRecyclerAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange: Arama Sözcüğü değişti.Sözcük : " + newText);

        ArrayList<MyPreviewsRecyclerItem> newItems = new ArrayList<>();
        for (MyPreviewsRecyclerItem item : items) {
            if (String.valueOf(item.getServiceId()).contains(newText))
                newItems.add(item);
        }
        myPreviewsRecyclerAdapter.setMyPreviewsRecyclerItems(newItems);
        myPreviewsRecyclerAdapter.notifyDataSetChanged();
        return true;
    }
}
