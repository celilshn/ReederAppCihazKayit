package com.cengcelil.reederapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cengcelil.reederapp.Activities.SamplePreviewActivity;
import com.cengcelil.reederapp.Utils;
import com.cengcelil.reederapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LastPreviewsRecyclerAdapter extends RecyclerView.Adapter<LastPreviewsRecyclerAdapter.MyViewHolder> {
    private Activity activity;

    public LastPreviewsRecyclerAdapter(Activity activity, ArrayList<Integer> items) {
        this.activity = activity;
        this.items = items;
    }

    public void setItems(ArrayList<Integer> items) {
        this.items = items;
    }

    private ArrayList<Integer> items;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_lastpreview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final int serviceId = items.get(position);
        holder.lpTextview.setText("Servis Numarası: "+serviceId);
        holder.lpTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, SamplePreviewActivity.class);
                i.putExtra("isFirst", false);
                i.putExtra("serviceId", String.valueOf(serviceId));
                activity.startActivity(i);

            }
        });
        holder.lpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showDialog(serviceId);
            }
        });
    }

    private void showDialog(final int serviceId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setMessage("Silinecek Servis Numarası: "+serviceId)
                .setTitle("Ön İnceleme Raporu Silme İşlemi")
                .setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.getCollectionFirstPreview(activity).whereEqualTo("serviceId", serviceId)
                                .whereEqualTo("uId", Utils.getUserInformation(activity).getuId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                    snapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            items.remove(Integer.valueOf(serviceId));
                                            notifyDataSetChanged();
                                        }
                                    });
                            }
                        });
                    }
                })
                .setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lpTextview;
        Button lpButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lpTextview = itemView.findViewById(R.id.lptextview);
            lpButton = itemView.findViewById(R.id.lpbutton);
        }
    }
}
