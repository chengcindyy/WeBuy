package com.csis4495_cmk.webuy.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerAddGroupStylesAdapter extends RecyclerView.Adapter<SellerAddGroupStylesAdapter.ViewHolder> {
    private Context context;
    private List<ProductStyle> styles = new ArrayList<>();

    private boolean isNewGroup = true;

    private Map<String, Integer> groupQtyMap;


    String productId;


    public Map<String, Integer> getGroupQtyMap() {
        return groupQtyMap;
    }

    public void setGroupQtyMap(Map<String, Integer> groupQtyMap) {
        this.groupQtyMap = groupQtyMap;
    }

    private OnImgBtnDeleteStyleListener onImgBtnDeleteStyleListener;

    private OnStyleChangedListner onStyleChangedListner;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ProductStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<ProductStyle> styles) {
        this.styles = styles;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public OnImgBtnDeleteStyleListener getOnImgBtnDeleteStyleListener() {
        return onImgBtnDeleteStyleListener;
    }

    public OnStyleChangedListner getOnStyleChangedListner() {
        return onStyleChangedListner;
    }

    public void setOnStyleChangedListner(OnStyleChangedListner listner) {
        this.onStyleChangedListner = listner;
    }

    public void setOnImgBtnDeleteStyleListener(OnImgBtnDeleteStyleListener listener) {
        this.onImgBtnDeleteStyleListener = listener;
    }

    public boolean isNewGroup() {
        return isNewGroup;
    }

    public void setNewGroup(boolean newGroup) {
        isNewGroup = newGroup;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_new_group_style, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerAddGroupStylesAdapter.ViewHolder holder, int position) {

            ProductStyle style = styles.get(position);
            holder.bindStyles(style);
            String styleImg = style.getStylePicName();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
            StorageReference imageReference = storageReference.child(productId + "/" + styleImg);
            imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Got the download URL and pass it to Picasso to download, show in ImageView and caching
                Picasso.get().load(uri.toString()).into(holder.styleImg);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle errors, if image doesn't exist, show a default image
                    holder.styleImg.setImageResource(R.drawable.default_image);
                    Log.d(TAG, "load style image error " + productId + "/" + styleImg);

                }
            });

            holder.deleteStyle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImgBtnDeleteStyleListener != null) {
                        onImgBtnDeleteStyleListener.onDeleteClick(position);
                    }
                }
            });

            if (!isNewGroup) {
                holder.deleteStyle.setVisibility(View.GONE);
                holder.deleteStyle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

//                String mapKey = "s___" + style.getStyleId();
//
//                if (getGroupQtyMap().containsKey(mapKey)) {
//                    Integer qty = getGroupQtyMap().get(mapKey);
//                    holder.styleQty.setText(Integer.toString(qty));
//
//                }
            }

            if (groupQtyMap != null) {
                String mapKey = "s___" + style.getStyleId();
                if (groupQtyMap.containsKey(mapKey) && groupQtyMap.get(mapKey) != null) {
                    Integer qty = groupQtyMap.get(mapKey);
                    holder.styleQty.setText(Integer.toString(qty));
                }
            }

    }

    public void updateStyleQty(Map<String, Integer> groupQtyMap) {
        this.groupQtyMap = groupQtyMap;
        Log.d(TAG, "updateStyleQty: " + groupQtyMap);
        notifyDataSetChanged();
    }

    public void updateStyleData2(String productId, List<ProductStyle> ps) {
        this.productId = productId;
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
        public TextInputEditText styleName, stylePrice, styleQty;
        public ImageButton deleteStyle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            styleImg = itemView.findViewById(R.id.img_new_group_style);
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
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ProductStyle currentStyle = styles.get(getAdapterPosition());
                    String newInfo = s.toString();
                    if (!newInfo.isEmpty()) {
                        Log.d("TextWatcher", "new price input");
//                        if(newInfo.length()>4){
                        try {
//                                newInfo = newInfo.substring(4);
                            double newPrice = Double.parseDouble(newInfo.trim().replace("CA$", ""));
                            Log.d("TextWatcher", "new price = " + newPrice);
                            if (newPrice < 0) {
                                stylePrice.setError("The price must be greater than 0");
                                Log.d("TextWatcher", "new price <= 0");
                                stylePrice.setText("CA$ ");
                                stylePrice.requestFocus();

                            } else if (newPrice == 0 && !newInfo.trim().endsWith(".")) {
                                stylePrice.setError("The price must be greater than 0");
                                Log.d("TextWatcher", "new price <= 0");
                                stylePrice.setText("CA$ ");
                                stylePrice.requestFocus();
                            } else {
                                currentStyle.setStylePrice(newPrice);
                                Log.d("TextWatcher", "new price = " + newPrice);
                                onStyleChangedListner.onStyleChange(getAdapterPosition(), currentStyle);
                            }
                        } catch (NumberFormatException e) {
                            Log.d("TextWatcher", "new price error " + e.toString());

                        }
                    }
                }
            });

            styleQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ProductStyle currentStyle = styles.get(getAdapterPosition());
                    String newInfo = s.toString().trim();
                    if (!newInfo.isEmpty()) {
                        try {
                            int qty = Integer.parseInt(s.toString());
                            if (qty < -1) {
//                                Toast.makeText(context, "The minimum quantity is -1 for unlimited quantity order", Toast.LENGTH_SHORT).show();
                                styleQty.setError("The minimum quantity is -1 for unlimited preorder group");
                                styleQty.setText("");
                                styleQty.requestFocus();
                            } else if (qty == 0 && isNewGroup) {
//                                Toast.makeText(context, "The quantity cannot be 0", Toast.LENGTH_SHORT).show();
                                styleQty.setError("The quantity cannot be 0 for a new group");
                                styleQty.setText("");
                                styleQty.requestFocus();
                            } else if (newInfo.length() > 1 && newInfo.startsWith("0")) {
                                styleQty.setError("The quantity cannot start with 0");
                                styleQty.setText("");
                                styleQty.requestFocus();
                            } else {
                                onStyleChangedListner.onStyleChangeQty(getAdapterPosition(), currentStyle, qty);
                            }
                        } catch (NumberFormatException e) {
                            Log.d("TextWatcher", "new qty error " + e.toString());
                        }
                    }

                }
            });
        }


        public boolean isAnyFieldEmpty() {
            return styleQty.getText().toString().trim().isEmpty()
                    || stylePrice.getText().toString().trim().isEmpty();
        }

        public void bindStyles(ProductStyle s) {
            styleName.setText(s.getStyleName());
            stylePrice.setText(Double.toString(s.getStylePrice()));
            styleQty.setText("");
        }

    }

    public interface OnImgBtnDeleteStyleListener {
        void onDeleteClick(int position);
    }

    public interface OnStyleChangedListner {
        void onStyleChangeQty(int position, ProductStyle style, int qty);

        void onStyleChange(int position, ProductStyle style);

    }

}
