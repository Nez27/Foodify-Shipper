package com.capstone.foodify.shipper.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.capstone.foodify.shipper.Activity.ChangePasswordActivity;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;

public class ProfileFragment extends Fragment {
    private static final String FOLDER_DIRECTORY = "UserImages/";
    TextView txt_countdown, txt_resend_code, txt_verify_email;
    EditText edt_birthday, edt_phone, edt_fullName;
    Button update_image_button;
    RoundedImageView profile_avatar;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    ConstraintLayout progressLayout;

    LinearLayout changePasswordLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Init component
        initComponent(view);
        initData();

        //Set event for change password layout
        changePasswordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ChangePasswordActivity.class));
            }
        });

        return view;
    }

    private void initData(){
        edt_phone.setText(Common.CURRENT_USER.getPhoneNumber());
        edt_birthday.setText(Common.CURRENT_USER.getDateOfBirth());
        edt_fullName.setText(Common.CURRENT_USER.getFullName());
    }
    private void initComponent(View view){
        changePasswordLayout = view.findViewById(R.id.change_password);
        edt_phone = view.findViewById(R.id.edt_phone);
        edt_birthday = view.findViewById(R.id.edt_birthDay);
        edt_fullName = view.findViewById(R.id.edt_fullName);
    }
}