package com.csis4495_cmk.webuy.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.tools.ItemMoveCallback;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerAddProductImagesAdapter;
import com.csis4495_cmk.webuy.adapters.SellerStyleListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerAddProductFragment extends Fragment
        implements
        SellerAddStyleFragment.onSellerAddStyleFragmentListener,
        SellerAddProductImagesAdapter.onProductImagesListener,
        SellerStyleListRecyclerAdapter.onStyleListItemChanged {

    private final int REQUEST_PERMISSION_CODE = 10;
    private FirebaseAuth auth;
    private StorageReference productImgsReference;
    private DatabaseReference firebaseDatabase;
    private FirebaseDatabase firebaseInstance;
    private FirebaseUser firebaseUser;
    private ActivityResultLauncher<String> productImgFilePicker;
    private RecyclerView recyclerViewProductImgs;
    private List<Uri> uriUploadProductImgs;
    private RecyclerView recyclerViewStyles;
    private List<ProductStyle> styleList;
    private List<ProductStyle> editStyleList;
    private Button btnAddStyle, btnSubmitAddProduct, btnCancelAddProduct;
    private TextInputLayout textLayoutProductPrice, textLayoutStylePriceRange;
    private TextInputEditText textInputProductName, textInputProductDescription, textInputStylePriceRange;
    private CurrencyEditText textInputProductPrice;
    private AutoCompleteTextView textInputProductCategory;
    private RadioGroup radioGroupTax;
    private int taxId;
    private String productId;
    private List<String> strProductImgNames;
    private byte[] imageBytes;
    private String productImgName;
    private int uploadImgCount = 0;
    private String editProductId;
    private ArrayAdapter<CharSequence> productCatAdapter;
    private RadioButton taxTadioButton_1, taxTadioButton_2, taxTadioButton_0;
    private TextView textLayoutTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_add_product, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("Test", "AddProductFrag view created");

        btnAddStyle = view.findViewById(R.id.btn_add_style);
        btnSubmitAddProduct = view.findViewById(R.id.btn_submit_add_product);
        btnCancelAddProduct = view.findViewById(R.id.btn_cancel_add_product);
        textInputProductName = view.findViewById(R.id.text_input_product_name);
        textInputProductDescription = view.findViewById(R.id.text_input_product_description);
        textInputProductCategory = view.findViewById(R.id.text_input_product_category);
        textInputProductPrice = view.findViewById(R.id.text_input_product_price);
        textInputStylePriceRange = view.findViewById(R.id.text_input_style_price_range);
        textLayoutProductPrice = view.findViewById(R.id.text_layout_product_price);
        textLayoutStylePriceRange = view.findViewById(R.id.text_layout_style_price_range);
        radioGroupTax = view.findViewById(R.id.radio_group_tax);
        textLayoutTitle = view.findViewById(R.id.tv_add_product);
        taxTadioButton_0 = view.findViewById(R.id.radio_btn_no_tax);
        taxTadioButton_1 = view.findViewById(R.id.radio_btn_gst);
        taxTadioButton_2 = view.findViewById(R.id.radio_btn_gst_pst);

        //0. set product category
        productCatAdapter = ArrayAdapter.createFromResource(getContext(), R.array.arr_product_category, android.R.layout.simple_list_item_1);
        textInputProductCategory.setAdapter(productCatAdapter);
        textInputProductCategory.setOnItemClickListener((parent, view2, position, id) -> {});

        //1. set images recycler view with default to add image
        uriUploadProductImgs = new ArrayList<>();
        GridLayoutManager gm = new GridLayoutManager(getContext(), 4);
        recyclerViewProductImgs = view.findViewById(R.id.rv_add_product_img);
        recyclerViewProductImgs.setLayoutManager(gm);
        setProductImagesAdapter(); // imgsList is empty at this point

        //add images when add img in rv clicked
        productImgFilePicker = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> result) {
                uriUploadProductImgs = result;
                setProductImagesAdapter();
            }
        });

        //2. add style
        styleList = new ArrayList<>();
        recyclerViewStyles = view.findViewById(R.id.rv_added_style);
        btnAddStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellerAddStyleFragment addStyleFragment = SellerAddStyleFragment.newInstance();

                // Get the fragmentManager
                FragmentManager fragmentManager = getParentFragmentManager();

                // Show the addStyleFragment
                addStyleFragment.show(fragmentManager, "Add Style Frag show");
                // set interface listener
                addStyleFragment.setOnSellerAddStyleFragmentListener(SellerAddProductFragment.this);
            }
        });

        //3. tax radio group
        radioGroupTax.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio_btn_no_tax:
                    taxId = 0;
                    break;
                case R.id.radio_btn_gst:
                    taxId = 1;
                    break;
                case R.id.radio_btn_gst_pst:
                    taxId = 2;
                    break;
                default:
                    taxId = -1;
                    break;
            }

            Log.d("Test", taxId+"!");
        });

        //4. submit added product
        submitAddProduct();

        //5. cancel
        btnCancelAddProduct.setOnClickListener(v->{
            //go back to the frag you came from
            Navigation.findNavController(v).popBackStack();
        });

        // 6. Edit product
        editStyleList = new ArrayList<>();
        Bundle editBundle = getArguments();
        if(editBundle != null){
            editProductId = editBundle.getString("productId");
            onShowCurrentProduct(editProductId);
            textLayoutTitle.setText("Edit a Product");
        }
    }

    private void onShowCurrentProduct(String editProductId) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");
        productRef.child(editProductId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                if(product != null){
                    List<String> _IMAGES = product.getProductImages();
                    String _NAME = product.getProductName();
                    String _DESC = product.getDescription();
                    String _PRICE = product.getProductPrice();
                    String _CATEGORY = product.getCategory();
                    int _TAX = (int) product.getTax();

                    GetImageUriFromStorage(_IMAGES);
                    textInputProductName.setText(_NAME);
                    textInputProductDescription.setText(_DESC);
                    textInputProductPrice.setText(_PRICE);
                    GetProductCategory(_CATEGORY);

                    switch (_TAX){
                        case 0:
                            taxTadioButton_0.setChecked(true);
                            break;
                        case 1:
                            taxTadioButton_1.setChecked(true);
                            break;
                        case 2:
                            taxTadioButton_2.setChecked(true);
                            break;
                    }

                    if(product.getProductStyles() != null){
                        editStyleList = product.getProductStyles();
                        Log.d("Test", editStyleList+"!");
                        setStylesAdapter(editStyleList);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetProductCategory(String selectedCategory) {
        for (int i = 0; i < productCatAdapter.getCount(); i++) {
            if (selectedCategory.equals(productCatAdapter.getItem(i))) {
                textInputProductCategory.setText(productCatAdapter.getItem(i), false);
                break;
            }
        }
    }

    // TODO: image cannot load correctly, still needs to work
    private void GetImageUriFromStorage(List<String> images) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        List<Uri> uriList = new ArrayList<>();
        for (String filename : images) {
            StorageReference imageRef = storageRef.child("ProductImages" + "/" + filename);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                uriList.add(uri);
                if (uriList.size() == images.size()) {
                    uriUploadProductImgs = uriList;
                    setProductImagesAdapter();

                }
            }).addOnFailureListener(exception -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Test", "Download URL: " + uri.toString());
                    Log.e("Test", "Download failed", exception);
                });
            });
        }

    }

    private void checkUserPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_PERMISSION_CODE);
            Toast.makeText(requireContext(), "Permission asked", Toast.LENGTH_SHORT).show();

        } else {
            pickProductImages();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                pickProductImages();
//            } else {
//                Toast.makeText(getContext(),"permission denied",Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    private void pickProductImages() {
        productImgFilePicker.launch("image/*");
        if (uriUploadProductImgs != null) {
            uriUploadProductImgs = new ArrayList<>(uriUploadProductImgs);
            uriUploadProductImgs.clear();
        }
    }

    private void setProductImagesAdapter() {
        SellerAddProductImagesAdapter sellerAddProductImagesAdapter = new SellerAddProductImagesAdapter(getActivity(), uriUploadProductImgs);
        //ItemTouchClass
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(sellerAddProductImagesAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewProductImgs);

        recyclerViewProductImgs.setAdapter(sellerAddProductImagesAdapter);
        sellerAddProductImagesAdapter.setOnProductImagesListener(this);
    }

    // TODO: add List<ProductStyle> styles as 1st parameter, still needs to work
    private void setStylesAdapter(List<ProductStyle> styles) {
        SellerStyleListRecyclerAdapter sellerStyleListRecyclerAdapter = new SellerStyleListRecyclerAdapter(getContext(), styles);
        //ItemTouchClass
        ItemTouchHelper.Callback callback =
                new ItemMoveCallback(sellerStyleListRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewStyles);

        recyclerViewStyles.setAdapter(sellerStyleListRecyclerAdapter);
        sellerStyleListRecyclerAdapter.setmStyleListChangedListener(this);
        sellerStyleListRecyclerAdapter.notifyDataSetChanged();
    }

    private void submitAddProduct() {
        btnSubmitAddProduct.setOnClickListener(v->{

            String productName = textInputProductName.getText().toString();
            String productDescription = textInputProductDescription.getText().toString();
            String productCategory = textInputProductCategory.getText().toString();
            Double productPrice = textInputProductPrice.getNumericValue();
            String stylePriceRange = textInputStylePriceRange.getText().toString();


            //check required input
            if (TextUtils.isEmpty(productName)) {
                Toast.makeText(getContext(),
                        "Please enter the product name.", Toast.LENGTH_SHORT).show();
                textInputProductName.setError("Product name is required.");
                textInputProductName.requestFocus();
            } else if (TextUtils.isEmpty(productDescription)) {
                Toast.makeText(getContext(),
                        "Please enter the product description.", Toast.LENGTH_SHORT).show();
                textInputProductDescription.setError("Product description is required.");
                textInputProductDescription.requestFocus();
            } else if (TextUtils.isEmpty(productCategory)) {
                Toast.makeText(getContext(),
                        "Please select the product category.", Toast.LENGTH_SHORT).show();
                textInputProductCategory.setError("Product category is required.");
                textInputProductCategory.requestFocus();
            } else if (taxId == -1) {
                Toast.makeText(getContext(),
                        "Please select the tax.", Toast.LENGTH_SHORT).show();
            } else if (uriUploadProductImgs.size() == 0) {
                Toast.makeText(getContext(),
                        "Please add at least one product image.", Toast.LENGTH_SHORT).show();
            } else if (styleList.size() == 0 && productPrice == 0) {
                Toast.makeText(getContext(),
                        "Please enter the product price.", Toast.LENGTH_SHORT).show();
                textInputProductPrice.setError("Product price is required.");
                textInputProductPrice.requestFocus();
            } else if (styleList.size() != 0 && stylePriceRange == null) {
                Toast.makeText(getContext(),"Style price went wrong",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Request send.", Toast.LENGTH_SHORT).show();
                firebaseInstance = FirebaseDatabase.getInstance();
                firebaseDatabase = firebaseInstance.getReference("Product");  //Product -> productId -> newProduct


                if(editProductId != null){
                    productId = editProductId;
                } else {
                    productId = firebaseDatabase.push().getKey();
                }

                //tax define, so far use taxId as the data we save in db

                //compress product images and upload to FireStorage (uriList to stringList)
                compressProductImages();

                //compress style image and upload to FireStorage (uri to string)
                compressStyleImage(styleList);

                //decide which price string to save
                String strPrice = styleList.size() == 0 ? textInputProductPrice.getText().toString() : stylePriceRange;

                //upload product and styles to Realtime DB

                uploadProduct(productName, productCategory,productDescription,
                        taxId, strProductImgNames, styleList, strPrice);

                //go back to the frag you came from
                Navigation.findNavController(v).popBackStack();
            }

        });
    }

    private void uploadProduct(String productName, String category, String productDescription,
                               double tax, List<String> strProductImgNames,
                               List<ProductStyle> productStyles, String strPrice) {

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        Product newProduct = new Product(productName,category,productDescription,
                tax, strProductImgNames, firebaseUser.getUid(), productStyles, strPrice);

        firebaseDatabase.child(productId).setValue(newProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Test Upload", "Product Upload Successfully");
                } else {
                    Log.d("Test Upload", "Product Upload Failed");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            }
        });

        //
//        // Convert the Product object to a HashMap using Gson library
//        Gson gson = new Gson();
//        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
//        HashMap<String, Object> productMap = gson.fromJson(gson.toJson(newProduct), type);
//
////        HashMap<String, Object> imgMap = new HashMap<>();
////        for (int i = 0; i < strProductImgNames.size(); i++) {
////            imgMap.put(String.valueOf(i), strProductImgNames.get(i));
////        }
////        // Update the productMap with the imgMap
////        productMap.put("strProductImgNames", imgMap);
//
//        // Save the productMap to the Realtime Database
//        firebaseDatabase.child(productId).setValue(productMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // Data saved successfully
//                        Toast.makeText(getContext(), "Product Upload Successfully", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getContext(), TestPageActivity.class);
//                        // Prevent seller go back
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // Failed to save data
//                        Toast.makeText(getContext(), "Product Upload Failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//

    }

    private void compressStyleImage(List<ProductStyle> styleList) {
        String styleImgName;
        uploadImgCount = 0;
        for (int i = 0; i< styleList.size(); i++) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(styleList.get(i).getStylePic()));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                imageBytes = stream.toByteArray();
                styleImgName = productId + "_" + styleList.get(i).getStyleName() + ".jpg";
                styleList.get(i).setStylePic(styleImgName);
                //to FireStorage
                uploadImagesToFireStorage(styleImgName, imageBytes, styleList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void compressProductImages() {
        strProductImgNames = new ArrayList<>();
        uploadImgCount = 0;
        for (int i = 0; i < uriUploadProductImgs.size(); i++) {
            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriUploadProductImgs.get(i));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                imageBytes = stream.toByteArray();
                productImgName = productId + "_" + i + ".jpg";
                strProductImgNames.add(productImgName);
                //to FireStorage
                uploadImagesToFireStorage(productImgName, imageBytes, uriUploadProductImgs);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImagesToFireStorage(String imageName, byte[] imageBytes, List<?> imageList) {
        productImgsReference = FirebaseStorage.getInstance().getReference().child("ProductImages");
        StorageReference imgReference = productImgsReference.child(imageName);
        // I am saving all the product images from different sellers at the same place now
        imgReference.putBytes(imageBytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadImgCount++;
                if(uploadImgCount == imageList.size()) {
                    Log.d("upload", "All Images Are Uploaded");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onAddNewProductImg() {
        checkUserPermission();
    }

//    public void setStyleToList(String styleName, Double price, String imgUri, int idx) {
//        Log.d("Test", styleName+ " " + price + " " + imgUri + " " + idx);
//        ProductStyle newStyle = new ProductStyle(styleName, price, imgUri);
//
//        if (idx == -1) { //Add a new style
//            styleList.add(newStyle);
//        } else { //update the style
//            styleList.get(idx).setStyleName(styleName);
//            styleList.get(idx).setStylePrice(price);
//            styleList.get(idx).setStylePic(imgUri);
//        }
//
//        //set the recyclerview
//        if (styleList != null) {
//            //recyclerViewStyles.setVisibility(View.VISIBLE);
//            LinearLayoutManager lm = new LinearLayoutManager(getContext());
//            recyclerViewStyles.setLayoutManager(lm);
//            setStylesAdapter();
//        }
//    }

    @Override
    public void onStyleEdit(int position) {
        ProductStyle editStyle = styleList.get(position);

        SellerAddStyleFragment editStyleFragment = SellerAddStyleFragment.newInstance(
                editStyle.getStyleName(),
                editStyle.getStylePrice(),
                editStyle.getStylePic(),
                position);

        //Get the fragmentManager and start a transaction
        FragmentManager fragmentManager = getParentFragmentManager();

        //Add the addStyleFragment and commit the transaction
        editStyleFragment.show(fragmentManager, "Edit Style Frag show");
        //set interface listener
        editStyleFragment.setOnSellerAddStyleFragmentListener(SellerAddProductFragment.this);
    }

    @Override
    public void onStyleListChanged(List<ProductStyle> newStyles) {
        styleList = newStyles;
        if (styleList.size() != 0) { //with at least one style

            //set product price invisible
            textLayoutProductPrice.setVisibility(View.GONE);
            textInputProductPrice.setText(null);

            //set style price range visible
            textLayoutStylePriceRange.setVisibility(View.VISIBLE);

            double minStylePrice = Double.MAX_VALUE;
            double maxStylePrice = Double.MIN_VALUE;

            for (ProductStyle style: styleList) {
                double stylePrice = style.getStylePrice();
                if (stylePrice < minStylePrice) {
                    minStylePrice = stylePrice;
                }
                if (stylePrice > maxStylePrice) {
                    maxStylePrice = stylePrice;
                }
            }

            if (minStylePrice == maxStylePrice) {
                textInputStylePriceRange.setText("CA$ " + minStylePrice);
            } else {
                textInputStylePriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
            }

        } else { //no style
            //set product price visible
            textLayoutProductPrice.setVisibility(View.VISIBLE);

            //set style price range visible
            textLayoutStylePriceRange.setVisibility(View.GONE);
            textInputStylePriceRange.setText(null);
        }
    }

    @Override
    public void onAddStyleToList(String styleName, Double price, String imgUri, int idx) {
        Log.d("Test", styleName+ " " + price + " " + imgUri + " " + idx);
        ProductStyle newStyle = new ProductStyle(styleName, price, imgUri);

        if (idx == -1) { //Add a new style
            styleList.add(newStyle);
        } else { //update the style
            styleList.get(idx).setStyleName(styleName);
            styleList.get(idx).setStylePrice(price);
            styleList.get(idx).setStylePic(imgUri);
        }

        if (styleList != null) { //with styles
            //set the recyclerview
            LinearLayoutManager lm = new LinearLayoutManager(getContext());
            recyclerViewStyles.setLayoutManager(lm);
            setStylesAdapter(styleList);

            //set product price invisible
            textLayoutProductPrice.setVisibility(View.GONE);
            textInputProductPrice.setText(null);

            //set style price range visible
            textLayoutStylePriceRange.setVisibility(View.VISIBLE);

            double minStylePrice = Double.MAX_VALUE;
            double maxStylePrice = Double.MIN_VALUE;

            for (ProductStyle style: styleList) {
                double stylePrice = style.getStylePrice();
                if (stylePrice < minStylePrice) {
                    minStylePrice = stylePrice;
                }
                if (stylePrice > maxStylePrice) {
                    maxStylePrice = stylePrice;
                }
            }

            if (minStylePrice == maxStylePrice) {
                textInputStylePriceRange.setText("CA$ " + minStylePrice);
            } else {
                textInputStylePriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
            }

        }
    }
}