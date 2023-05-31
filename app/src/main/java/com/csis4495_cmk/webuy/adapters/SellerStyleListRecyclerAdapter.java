package com.csis4495_cmk.webuy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class SellerStyleListRecyclerAdapter extends RecyclerView.Adapter<SellerStyleListRecyclerAdapter.ViewHolder> {

    private List<ProductStyle> styles;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
       View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seller_edit_style_card_layout,viewGroup,false);
       SellerStyleListRecyclerAdapter.ViewHolder viewHolder = new SellerStyleListRecyclerAdapter.ViewHolder(v);
       return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return styles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        Button styleImg, clear;
        TextInputLayout inputLayout_style, inputLayout_price, inputLayout_qty;
        TextInputEditText editText_style, editText_price, editText_qty;
        RadioGroup taxGp;
        RadioButton noTax, gst, gstPst;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            styleImg = itemView.findViewById(R.id.btn_img_style_list);
            inputLayout_style = itemView.findViewById(R.id.editLayout_style_style_list);
            editText_style = itemView.findViewById(R.id.edit_style_style_list);

            inputLayout_price = itemView.findViewById(R.id.editLayout_price_style_list);
            editText_price = itemView.findViewById(R.id.edit_price_style_list);

            inputLayout_qty = itemView.findViewById(R.id.editLayout_qty_style_list);
            editText_qty = itemView.findViewById(R.id.edit_qty_style_list);

            taxGp = itemView.findViewById(R.id.rdBtnGp_style_list);
            noTax = itemView.findViewById(R.id.rdBtn_no_tax_style_list);
            gst = itemView.findViewById(R.id.rdBtn_gst_style_list);
            gstPst = itemView.findViewById(R.id.rdBtn_gst_pst_style_list);
            clear = itemView.findViewById(R.id.btn_clear_style_list);
        }
    }
}
