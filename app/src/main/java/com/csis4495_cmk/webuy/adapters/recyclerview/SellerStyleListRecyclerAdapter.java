package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.tools.ItemMoveCallback;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class SellerStyleListRecyclerAdapter extends RecyclerView.Adapter<SellerStyleListRecyclerAdapter.StyleListViewHolder>
                                    implements ItemMoveCallback.ItemTouchHelperContract<SellerStyleListRecyclerAdapter.StyleListViewHolder>{

    private List<ProductStyle> styles;

    onStyleListItemChanged mStyleListItemChangedListener;

    Context context;

    public SellerStyleListRecyclerAdapter(Context context, List<ProductStyle> styles) {
        this.styles = styles;
        this.context = context;
    }

    public void setmStyleListChangedListener(onStyleListItemChanged mListener) {
        this.mStyleListItemChangedListener = mListener;
    }

    @NonNull
    @Override
    public StyleListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
       View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_seller_style_with_delete,viewGroup,false);
       StyleListViewHolder viewHolder = new StyleListViewHolder(itemView);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StyleListViewHolder holder, int position) {

        Log.d("Test StylePicName", styles.get(position).getStylePicName());
        Picasso.get().load(styles.get(position).getStylePicName()).into(holder.imvStyleImg);

        holder.tvStyleName.setText(styles.get(position).getStyleName());
        holder.tvStylePrice.setText("CA$ " + styles.get(position).getStylePrice());

        // item click -> enable editing (position to fragment, fragment to activity to adapter)
        holder.view.setOnClickListener(v -> {
            mStyleListItemChangedListener.onStyleEdit(position);
        });

        // delete btn click -> remove from list
        holder.imgBtnDeleteStyle.setOnClickListener(v -> {
            mStyleListItemChangedListener.onStyleDelete(styles.get(position).getStylePicName());
            styles.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
            // pass back the updated stylist
            mStyleListItemChangedListener.onStyleListChanged(styles);
        });
    }

    @Override
    public int getItemCount() {
        return styles.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && fromPosition < styles.size() &&
                toPosition >= 0 && toPosition < styles.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Log.d("swap", fromPosition+" "+toPosition+" "+ styles.size() + " " + getItemCount());
                    Collections.swap(styles, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(styles, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            //notifyDataSetChanged();
        }

    }

    @Override
    public void onRowSelected(StyleListViewHolder myViewHolder) {
        //myViewHolder.view.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(StyleListViewHolder myViewHolder) {
        //myViewHolder.view.setBackgroundColor(Color.WHITE);
    }

    public class StyleListViewHolder extends RecyclerView.ViewHolder{
        ImageButton imgBtnDeleteStyle;
        TextView tvStyleName, tvStylePrice;
        ImageView imvStyleImg;
        View view;

        public StyleListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBtnDeleteStyle = itemView.findViewById(R.id.imgBtn_delete_style);
            tvStyleName = itemView.findViewById(R.id.tv_style_name);
            tvStylePrice = itemView.findViewById(R.id.tv_style_price);
            imvStyleImg = itemView.findViewById(R.id.imv_style_img);
            view = itemView;
        }
    }

    public interface onStyleListItemChanged {
        void onStyleEdit(int position);
        void onStyleListChanged(List<ProductStyle> newStyles);
        void onStyleDelete(String deletedStylePicUrl);
    }

}
