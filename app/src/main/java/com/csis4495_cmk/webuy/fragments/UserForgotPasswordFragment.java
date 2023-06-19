package com.csis4495_cmk.webuy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserForgotPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserForgotPasswordFragment extends Fragment {

    TextInputLayout editEmail;
    private Button btnReset, btnCancel;
    private FirebaseAuth authProfile;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserForgotPasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserForgotPasswordFragment newInstance(String param1, String param2) {
        UserForgotPasswordFragment fragment = new UserForgotPasswordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_forgot_password, container, false);;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editEmail = view.findViewById(R.id.edit_email);

        btnReset = view.findViewById(R.id.btn_send);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resetEmail = editEmail.getEditText().getText().toString();

                if(!userEnteredDataVerification(resetEmail)){
                    Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Valid emil is required.");
                    editEmail.requestFocus();
                }else{
                    ResetPassword(resetEmail);
                }
            }
        });

        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }

    private boolean userEnteredDataVerification(String resetEmail) {
        if (TextUtils.isEmpty(resetEmail)) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            editEmail.setError("Email is required.");
            editEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            editEmail.setError("Valid email is required.");
            editEmail.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    private void ResetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(),
                            "Please check your inbox for password reset link", Toast.LENGTH_SHORT).show();
                    if(getActivity() != null) {
                        getActivity().finish();
                    }
                }else {
                    Toast.makeText(getContext(),
                            "Something went wrong: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}