package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SellerAddGroupStylesAdapter extends RecyclerView.Adapter<SellerAddGroupStylesAdapter.ViewHolder> {
    private Context context;
    private List<ProductStyle> styles = new ArrayList<>();

    private OnImgBtnDeleteStyleListener onImgBtnDeleteStyleListener;

    private OnStyleChangedListner onStyleChangedListner;

    public void setOnStyleChangedListner (OnStyleChangedListner listner){
        this.onStyleChangedListner = listner;
    }

    public void setOnImgBtnDeleteStyleListener(OnImgBtnDeleteStyleListener listener){
        this.onImgBtnDeleteStyleListener = listener;
    }


    public SellerAddGroupStylesAdapter() {
    }

    public SellerAddGroupStylesAdapter(List<ProductStyle> styles) {
        this.styles = styles;
    }

    public SellerAddGroupStylesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_new_group_style,parent,false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerAddGroupStylesAdapter.ViewHolder holder, int position) {
        ProductStyle style = styles.get(position);
        holder.bindStyles(style);

        //load style image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImages");
//        StorageReference imageReference = storageReference.child(styles.get(position).getStylePic());
        StorageReference imageReference = storageReference.child(style.getStylePic());

        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.styleImg);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle errors, if image doesn't exist, show a default image
                holder.styleImg.setImageResource(R.drawable.default_image);
            }
        });

        holder.deleteStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onImgBtnDeleteStyleListener != null){
                    onImgBtnDeleteStyleListener.onDeleteClick(position);
                }
            }
        });
    }

    public void updateStyleData(List<ProductStyle> ps){
        styles.clear();
        styles.addAll(ps);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return styles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView styleImg;
        public TextInputEditText styleName, stylePrice, styleQty ;
        public ImageButton deleteStyle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            styleImg = itemView.findViewById(R.id.img_group_style);
            styleName = itemView.findViewById(R.id.edit_group_style_name);
            stylePrice = itemView.findViewById(R.id.edit_group_style_price);
            styleQty = itemView.findViewById(R.id.edit_group_style_qty);
            deleteStyle = itemView.findViewById(R.id.imgBtn_delete_group_style);
            styleName.setEnabled(false);

            stylePrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ProductStyle currentStyle = styles.get(getAdapterPosition());
                    String newInfo = s.toString();
                    if(!newInfo.isEmpty()){
                        try {
                            double newPrice = Double.parseDouble(s.toString());
                            if( newPrice <= 0){
                                Toast.makeText(context, "The price must be greater than 0", Toast.LENGTH_SHORT).show();
                                stylePrice.setText("");
                                stylePrice.requestFocus();
                            }else{
                                currentStyle.setStylePrice(newPrice);
                                onStyleChangedListner.onStyleChange(getAdapterPosition(), currentStyle);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            styleQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ProductStyle currentStyle = styles.get(getAdapterPosition());
                    String newInfo = s.toString();
                    if(!newInfo.isEmpty()){
                        try {
                            int qty = Integer.parseInt(s.toString());
                            if (qty < -1){
                                Toast.makeText(context, "The minimum quantity is -1 for unlimited quantity order", Toast.LENGTH_SHORT).show();
                                styleQty.setText("");
                                styleQty.requestFocus();
                            }else if(qty == 0){
                                Toast.makeText(context, "The quantity cannot be 0", Toast.LENGTH_SHORT).show();
                                styleQty.setText("");
                                styleQty.requestFocus();
                            }
                            else {
                                currentStyle.setStyleQty(qty);
                                onStyleChangedListner.onStyleChange(getAdapterPosition(), currentStyle);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {



                }
            });

        }

        public void bindStyles(ProductStyle s){
            styleName.setText(s.getStyleName());
            stylePrice.setText(Double.toString(s.getStylePrice()));
            styleQty.setText("");
        }

    }

    public interface OnImgBtnDeleteStyleListener{
        void onDeleteClick(int position);
    }

    public interface OnStyleChangedListner{
        void onStyleChange(int position, ProductStyle style);
    }


}