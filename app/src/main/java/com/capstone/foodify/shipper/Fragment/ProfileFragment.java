package com.capstone.foodify.shipper.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.capstone.foodify.shipper.Activity.AccountAndProfileActivity;
import com.capstone.foodify.shipper.Activity.ChangePasswordActivity;
import com.capstone.foodify.shipper.Activity.SignInActivity;
import com.capstone.foodify.shipper.Activity.WebViewActivity;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.R;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    EditText edt_birthday, edt_phone, edt_fullName, edt_email;
    LinearLayout changePasswordLayout, changeInformationLayout, logOutLayout, register_app_beta;
    RoundedImageView profile_avatar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if(Common.CURRENT_USER == null)
            startActivity(new Intent(getContext(), SignInActivity.class));

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

        changeInformationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountAndProfileActivity.class));
            }
        });

        register_app_beta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WebViewActivity.class));
            }
        });

        logOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog();
            }
        });

        return view;
    }

    private void initData(){
        edt_phone.setText(Common.CURRENT_USER.getPhoneNumber());
        edt_birthday.setText(Common.CURRENT_USER.getDateOfBirth());
        edt_fullName.setText(Common.CURRENT_USER.getFullName());
        edt_email.setText(Common.CURRENT_USER.getEmail());
    }
    private void initComponent(View view){
        changePasswordLayout = view.findViewById(R.id.change_password);
        changeInformationLayout = view.findViewById(R.id.change_information);
        logOutLayout = view.findViewById(R.id.log_out);
        register_app_beta = view.findViewById(R.id.register_beta_app);

        edt_phone = view.findViewById(R.id.edt_phone);
        edt_birthday = view.findViewById(R.id.edt_birthDay);
        edt_fullName = view.findViewById(R.id.edt_fullName);
        edt_email = view.findViewById(R.id.edt_email);

        profile_avatar = view.findViewById(R.id.profile_avatar);
    }

    private void logOutDialog(){
        PopupDialog.getInstance(getContext())
                .setStyle(Styles.STANDARD)
                .setHeading("Đăng xuất?")
                .setDescription("Bạn thực sự muốn đăng xuất?")
                .setPopupDialogIcon(R.drawable.baseline_logout_24)
                .setPopupDialogIconTint(R.color.primaryColor)
                .setPositiveButtonBackground(R.drawable.bg_color_primary_corner)
                .setPositiveButtonText("Có")
                .setNegativeButtonText("Không")
                .setCancelable(false)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveClicked(Dialog dialog) {
                        FirebaseAuth.getInstance().signOut();
                        Common.CURRENT_USER = null;
                        dialog.dismiss();

                        getActivity().startActivity(new Intent(getContext(), SignInActivity.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onNegativeClicked(Dialog dialog) {
                        super.onNegativeClicked(dialog);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update user name and date of birth on textview
        edt_fullName.setText(Common.CURRENT_USER.getFullName());
        edt_birthday.setText(Common.CURRENT_USER.getDateOfBirth());


        //Set image for user
        if(Common.CURRENT_USER.getImageUrl().isEmpty()){
            profile_avatar.setImageResource(R.drawable.profile_avatar);
        } else {
            Picasso.get().load(Common.CURRENT_USER.getImageUrl()).into(profile_avatar);
        }
    }
}