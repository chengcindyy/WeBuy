package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.tools.ItemMoveCallback;
import com.csis4495_cmk.webuy.R;

import java.util.Collections;
import java.util.List;

public class SellerAddProductImagesAdapter extends RecyclerView.Adapter<SellerAddProductImagesAdapter.AddImagesViewHolder> implements ItemMoveCallback.ItemTouchHelperContract<SellerAddProductImagesAdapter.AddImagesViewHolder>{

    onProductImagesListener mProductImagesListener;
    Context context;
    List<Uri> uriUploadImgs;

    public void setOnProductImagesListener(onProductImagesListener mListener) {
        this.mProductImagesListener = mListener;
    }

    public SellerAddProductImagesAdapter(Context context, List<Uri> uriUploadImgs) {
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

        if (holder.getAdapterPosition() == uriUploadImgs.size() || getItemCount() == 1) { //last img or only one
            holder.btnDelete.setVisibility(View.GONE);
            holder.tvCoverImg.setVisibility(View.GONE);
            holder.imgViewSingleImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            if(holder.getAdapterPosition() == 0) {
                holder.tvCoverImg.setVisibility(View.VISIBLE);
            }
            holder.imgViewSingleImg.setImageURI(uriUploadImgs.get(position));
        }

        // last one clicked
        holder.imgViewSingleImg.setOnClickListener(v -> {
            if (holder.getAdapterPosition() == getItemCount()-1) {
                //add new img
                mProductImagesListener.onAddNewProductImg();
                Toast.makeText(context, holder.getAdapterPosition()+"position", Toast.LENGTH_SHORT).show();
            } else {
                //edit img
            }
        });

        // delete - need to update the new list
        holder.btnDelete.setOnClickListener(v -> {
            uriUploadImgs.remove(holder.getAdapterPosition());
            //notifyItemRemoved(holder.getAdapterPosition()); //it won't make the first one cover img
            notifyDataSetChanged();
        });

        // long click - change position

    }

    @Override
    public int getItemCount() {
        return uriUploadImgs.size() + 1;
    }

    // ItemMoveCallback.ItemTouchHelperContract
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition != uriUploadImgs.size() && toPosition != uriUploadImgs.size() &&
                uriUploadImgs.size() != 0) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Log.d("swap", fromPosition+" "+toPosition+" "+uriUploadImgs.size() + " " + getItemCount());
                    Collections.swap(uriUploadImgs, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(uriUploadImgs, i, i - 1);
                }
            }
            //notifyItemMoved(fromPosition, toPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onRowSelected(AddImagesViewHolder myViewHolder) {
        myViewHolder.view.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(AddImagesViewHolder myViewHolder) {
        myViewHolder.view.setBackgroundColor(Color.WHITE);
    }

    public static class AddImagesViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewSingleImg;
        Button btnDelete;
        TextView tvCoverImg;
        View view;

        public AddImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewSingleImg = itemView.findViewById(R.id.imv_single_product_img);
            btnDelete = itemView.findViewById(R.id.btn_delete_img);
            tvCoverImg = itemView.findViewById(R.id.tv_cover_img);
            view = itemView;
        }
    }

    public interface onProductImagesListener {
        void onAddNewProductImg();
    }
}
