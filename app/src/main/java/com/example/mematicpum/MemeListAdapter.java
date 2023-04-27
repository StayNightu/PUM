package com.example.mematicpum;

import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

public class MemeListAdapter extends RecyclerView.Adapter<MemeListAdapter.ViewHolder> {
    private MemeListItem[] localDataSet;

    public MemeListAdapter() {
        localDataSet = new MemeListItem[]{};
    }

    public void setData(MemeListItem[] dataSet){
        localDataSet = dataSet;
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
        holder.getTextView().setText(localDataSet[position].getName());
        holder.getImageView().setImageBitmap(localDataSet[position].getImage());
//        Canvas captureCanvas = new Canvas(localDataSet[position].getImage());
//        holder.getImageView().draw(captureCanvas);
    }

    @Override
    public int getItemCount() {
        return localDataSet.length;
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
