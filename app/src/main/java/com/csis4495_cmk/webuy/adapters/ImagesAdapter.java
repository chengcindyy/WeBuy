package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.csis4495_cmk.webuy.R;

import java.util.List;
import java.util.Objects;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.AddImagesViewHolder> {

    onImgClick mListener;
    Context context;
    List<Uri> uriUploadImgs;

    public void setmListenr(onImgClick mListener) {
        this.mListener = mListener;
    }

    public ImagesAdapter(Context context, List<Uri> uriUploadImgs) {
        this.context = context;
        this.uriUploadImgs = uriUploadImgs;
    }

    @NonNull
    @Override
    public AddImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_image_with_delete, parent, false);
        return new AddImagesViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AddImagesViewHolder holder, int position) {

        if (position == getItemCount()-1 || getItemCount() == 1) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.tvCoverImg.setVisibility(View.GONE);
            holder.imgViewSingleImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
        } else {
            if(position == 0) {
                holder.tvCoverImg.setVisibility(View.VISIBLE);
            }
            holder.imgViewSingleImg.setImageURI(uriUploadImgs.get(position));
        }

        // last one clicked
        holder.imgViewSingleImg.setOnClickListener(v -> {
            if (holder.getAdapterPosition() == getItemCount()-1) {
                //add new img
                mListener.addNewImg();
                Toast.makeText(context, holder.getAdapterPosition()+"position", Toast.LENGTH_SHORT).show();
            } else {
                //edit img
            }
        });
        // delete - need to update the new list
        holder.btnDelete.setOnClickListener(v -> {
            uriUploadImgs.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            notifyDataSetChanged();
        });

        // long click - change position

    }

    @Override
    public int getItemCount() {
        return uriUploadImgs.size() + 1;
    }

    public static class AddImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewSingleImg;
        Button btnDelete;
        TextView tvCoverImg;

        public AddImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewSingleImg = itemView.findViewById(R.id.imv_single_img);
            btnDelete = itemView.findViewById(R.id.btn_delete_img);
            tvCoverImg = itemView.findViewById(R.id.tv_cover_img);
        }
    }

    public interface onImgClick {
        void addNewImg();
    }
}
