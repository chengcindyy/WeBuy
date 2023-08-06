package com.csis4495_cmk.webuy.fragments;

import static java.net.Proxy.Type.HTTP;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.google.android.material.textfield.TextInputLayout;

public class SellerCustomerSupportFragment extends Fragment {

    private TextInputLayout edtName, edtEmail, edtMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_customer_support, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtName = view.findViewById(R.id.edit_name);
        edtEmail = view.findViewById(R.id.edit_email);
        edtMessage = view.findViewById(R.id.edit_content);
        Button sendEmailButton = view.findViewById(R.id.sendEmailButton);

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String email = edtEmail.getEditText().getText().toString();
        String name = edtName.getEditText().getText().toString();
        String content = edtMessage.getEditText().getText().toString();

        // Validation
        if (email.isEmpty() || content.isEmpty() || name.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send email to admin
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"example@example.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        intent.putExtra(Intent.EXTRA_TEXT, "Email body");
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

        Toast.makeText(getActivity(), "Email sent successfully", Toast.LENGTH_SHORT).show();
    }
}