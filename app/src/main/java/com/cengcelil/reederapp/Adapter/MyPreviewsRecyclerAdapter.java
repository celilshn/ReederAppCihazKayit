package com.cengcelil.reederapp.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cengcelil.reederapp.Activities.CheckSamplePreviewActivity;
import com.cengcelil.reederapp.Modal.MyPreviewsRecyclerItem;
import com.cengcelil.reederapp.R;

import java.util.ArrayList;

public class MyPreviewsRecyclerAdapter extends RecyclerView.Adapter<MyPreviewsRecyclerAdapter.MyHolder> {
    private Activity activity;

    public MyPreviewsRecyclerAdapter(Activity activity, ArrayList<MyPreviewsRecyclerItem> myPreviewsRecyclerItems) {
        this.activity = activity;
        this.myPreviewsRecyclerItems = myPreviewsRecyclerItems;
    }

    private ArrayList<MyPreviewsRecyclerItem> myPreviewsRecyclerItems;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_mypreview_item, parent, false);
        return new MyHolder(view);
    }

    public void setMyPreviewsRecyclerItems(ArrayList<MyPreviewsRecyclerItem> myPreviewsRecyclerItems) {
        this.myPreviewsRecyclerItems = myPreviewsRecyclerItems;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        final MyPreviewsRecyclerItem item = myPreviewsRecyclerItems.get(position);
        holder.serviceIdTextview.setText(String.valueOf(item.getServiceId()));
        final Intent intent = new Intent(activity, CheckSamplePreviewActivity.class);
        intent.putExtra("serviceId", item.getServiceId());

        holder.firstPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("isFirst",true);
                activity.startActivity(intent);
            }
        });
        if (item.getDeviceInformationLastPreview() != null) {
            holder.lastPreviewButton.setAlpha(1);
            holder.lastPreviewButton.setClickable(true);
            holder.lastPreviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.putExtra("isFirst",false);
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return myPreviewsRecyclerItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView serviceIdTextview;
        Button firstPreviewButton, lastPreviewButton;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            serviceIdTextview = itemView.findViewById(R.id.serviceIdTextview);
            firstPreviewButton = itemView.findViewById(R.id.firstPreviewButton);
            lastPreviewButton = itemView.findViewById(R.id.lastPreviewButton);
        }
    }
}
