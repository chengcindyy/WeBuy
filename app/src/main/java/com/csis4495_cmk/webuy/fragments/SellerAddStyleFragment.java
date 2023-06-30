package com.csis4495_cmk.webuy.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class SellerAddStyleFragment extends BottomSheetDialogFragment {

    private static final String ARG_STYLE_NAME = "styleName";
    private static final String ARG_STYLE_PRICE = "stylePrice";
    private static final String ARG_STYLE_IMG = "styleImg";
    private static final String ARG_STYLE_INDEX = "styleIdx";
    onSellerAddStyleFragmentListener mSellerAddStyleFragmentListener;

    private TextInputEditText textInputStyleName;
    private CurrencyEditText textInputStylePrice;
    private Button btnAddStyleToList;
    private ImageButton imgBtnAddImg;

    private ActivityResultLauncher<Intent> styleImgFilePicker;
    private String styleName;
    private Double stylePrice;
    private Uri styleImg;
    private Uri editStyleImg;
    private int styleIdx = -1; // -1 means new style

    public SellerAddStyleFragment() {
        // Required empty public constructor
    }

    public static SellerAddStyleFragment newInstance() {
        SellerAddStyleFragment fragment = new SellerAddStyleFragment();
        return fragment;
    }
    public static SellerAddStyleFragment newInstance(String styleName, Double stylePrice, String styleImg, int styleIdx) {
        SellerAddStyleFragment fragment = new SellerAddStyleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STYLE_NAME, styleName);
        args.putDouble(ARG_STYLE_PRICE, stylePrice);
        args.putString(ARG_STYLE_IMG, styleImg);
        args.putInt(ARG_STYLE_INDEX, styleIdx);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnSellerAddStyleFragmentListener(onSellerAddStyleFragmentListener mListener) {
        this.mSellerAddStyleFragmentListener = mListener;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get Arguments passed by newInstance
        if (getArguments() != null) {
            styleName = getArguments().getString(ARG_STYLE_NAME);
            stylePrice = getArguments().getDouble(ARG_STYLE_PRICE);
            styleImg = Uri.parse(getArguments().getString(ARG_STYLE_IMG));
            styleIdx = getArguments().getInt(ARG_STYLE_INDEX);
        }

        //add style img launcher
        styleImgFilePicker = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // do your operation from here....
                        if (data != null && data.getData() != null) {
                            styleImg = data.getData();
                            imgBtnAddImg.setImageURI(styleImg);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_seller_add_style, container, false);
        imgBtnAddImg = rootView.findViewById(R.id.imgBtn_add_style_img);
        textInputStyleName = rootView.findViewById(R.id.text_input_style_name);
        textInputStylePrice = rootView.findViewById(R.id.text_input_style_price);
        btnAddStyleToList = rootView.findViewById(R.id.btn_add_style_to_list);

        // update (edit) style
        if (getArguments() != null) {
            btnAddStyleToList.setText("Update");
            //set the data to corresponded box
            textInputStyleName.setText(styleName);
            textInputStylePrice.setText(stylePrice.toString());
            Picasso.get().load(styleImg).into(imgBtnAddImg);
            //imgBtnAddImg.setImageURI(styleImg);
            //Update Clicked
            btnAddOrUpdateClicked();
        }

        // select style img
        imgBtnAddImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            styleImgFilePicker.launch(intent);
            //get downloaded edit img
            if (styleImg!=null && styleImg.toString().contains("https://")) {
                editStyleImg = styleImg;
                Log.d("Test edit style Uri", editStyleImg.toString());
            }
        });

        // add a new style
        btnAddOrUpdateClicked();

        return rootView;
    }

    void btnAddOrUpdateClicked() {
        btnAddStyleToList.setOnClickListener(v -> {
            styleName = textInputStyleName.getText().toString();
            stylePrice = textInputStylePrice.getNumericValue();
            //styleName, price, img must be not null
            if (TextUtils.isEmpty(styleName)) {
                Toast.makeText(getActivity(),
                        "Please enter the style name.", Toast.LENGTH_SHORT).show();
                textInputStyleName.setError("Style name is required.");
                textInputStyleName.requestFocus();
            } else if (stylePrice == 0) {
                Toast.makeText(getActivity(),
                        "Please enter the style price.", Toast.LENGTH_SHORT).show();
                textInputStylePrice.setError("Style price is required.");
                textInputStylePrice.requestFocus();
            } else if (styleImg == null) {
                Toast.makeText(getActivity(),
                        "Please select a style image.", Toast.LENGTH_SHORT).show();
            } else {
                if (mSellerAddStyleFragmentListener != null) {
                    mSellerAddStyleFragmentListener.onAddStyleToList(styleName, stylePrice, styleImg.toString(), styleIdx);
                }
                //
                if (editStyleImg != null && styleImg != editStyleImg) {
                    mSellerAddStyleFragmentListener.onStyleImgDelete(editStyleImg);
                    Log.d("Test stylePic delete", editStyleImg.toString());
                }
                    onDismiss(getDialog());
            }

        });
    }

    public interface onSellerAddStyleFragmentListener{
        void onAddStyleToList(String styleName, Double price, String imgUri, int idx);
        void onStyleImgDelete(Uri deletedUri);
    }
}