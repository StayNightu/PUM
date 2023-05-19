package com.example.mematicpum;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MemeListAdapter extends RecyclerView.Adapter<MemeListAdapter.ViewHolder> {
    private ArrayList<MemeListItem> localDataSet;
    ImageView uploadImageContainer;

    public MemeListAdapter(ImageView uploadImageContainer) {
        localDataSet = new ArrayList<MemeListItem>();
        this.uploadImageContainer = uploadImageContainer;
    }

    public MemeListAdapter(ArrayList<MemeListItem> localDataSet, ImageView uploadImageContainer) {
        this.localDataSet = localDataSet;
        this.uploadImageContainer = uploadImageContainer;
    }

    public void setData(ArrayList<MemeListItem> dataSet){
        localDataSet = dataSet;
    }

    public void addItem(MemeListItem item){
        localDataSet.add(item);
    }

    @NonNull
    @Override
    public MemeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.meme_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemeListAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(localDataSet.get(position).getName());
        if (localDataSet.get(position).getImage() != null) {
            MemeListItem image = localDataSet.get(position);
            if (image != null){
                holder.getImageView().setImageBitmap(image.getImage());
                holder.getImageView().setOnClickListener(uploadImageToContainer);
            }
        }
    }

    View.OnClickListener uploadImageToContainer = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (uploadImageContainer != null){
                Bitmap bm=((BitmapDrawable)((ImageView)view).getDrawable()).getBitmap();
                uploadImageContainer.setImageBitmap(bm);
            }
        }
    };

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

        public TextView getTextView() {
            return textView;
        }
        public ImageView getImageView() {
            return imageView;
        }
    }
}
