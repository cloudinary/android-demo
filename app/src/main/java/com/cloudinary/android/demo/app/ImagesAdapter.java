package com.cloudinary.android.demo.app;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nitzan Jaitman on 10/04/2018.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private String cloud;
    private List<String> publicIds = new ArrayList<>();
    private onImageChosenListener listener;

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = (ImageView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    public void setImages(List<String> publicIds) {
        this.publicIds = publicIds;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String publicId = publicIds.get(position);
        holder.itemView.setTag(publicId);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onImageChosen((String) v.getTag());
            }
        });

        ImageHelper.loadImageAdapterImages(publicId, cloud, (ImageView) holder.itemView);
    }

    @Override
    public int getItemCount() {
        return publicIds.size();
    }

    public void setListener(onImageChosenListener listener) {
        this.listener = listener;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    interface onImageChosenListener {
        void onImageChosen(String publicId);
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageViewHolder(ImageView view) {
            super(view);
        }
    }
}
