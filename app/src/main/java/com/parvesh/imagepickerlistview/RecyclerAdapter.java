package com.parvesh.imagepickerlistview;


import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Uri> uriArraylist;

    public RecyclerAdapter(ArrayList<Uri> uriArraylist)
    {
        this.uriArraylist = uriArraylist;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.custom_single_image,parent,false);



        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {
        holder.imageview.setImageURI(uriArraylist.get(position));
    }

    @Override
    public int getItemCount() {
        return uriArraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;

        public ViewHolder(View itemView)
        {
            super(itemView);
            imageview = itemView.findViewById(R.id.image);

        }

    }
}
