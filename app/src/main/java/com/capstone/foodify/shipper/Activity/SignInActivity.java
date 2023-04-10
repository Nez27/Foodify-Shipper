package com.capstone.foodify.shipper.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;

import com.capstone.foodify.shipper.API.FoodApi;
import com.capstone.foodify.shipper.API.FoodApiToken;
import com.capstone.foodify.shipper.Common;
import com.capstone.foodify.shipper.Model.Shipper;
import com.capstone.foodify.shipper.Model.User;
import com.capstone.foodify.shipper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputLayout textInput_email, textInput_password;
    TextInputEditText edt_email, edt_password;
    ConstraintLayout progressLayout;
    String email, password = null;
    MaterialButton btn_sign_in;
    private FirebaseUser user;

    @Override
    public void onStart() {
        super.onStart();

        //Get user from Paper
//        user = Paper.book().read("user");

        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();

        if (user != null && Common.CURRENT_USER == null) {
            progressLayout.setVisibility(View.VISIBLE);

            getInformationAndSignInUser(user);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Init firebase app
        FirebaseApp.initializeApp(SignInActivity.this);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Init Component
        initComponent();
        setFontUI();
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();

                if(validData()){
                    signIn();
                }
            }
        });


        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInput_password.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void signIn(){
        progressLayout.setVisibility(View.VISIBLE);

        mAuth.setLanguageCode("vi");

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                        if (isNewUser) {
                            Toast.makeText(SignInActivity.this, "Tài khoản chưa được đăng ký! Vui lòng đăng ký để tiếp tục sử dụng!", Toast.LENGTH_SHORT).show();
                            progressLayout.setVisibility(View.GONE);
                        } else {
                            signInMethod();
                        }

                    }
                });
    }

    private void signInMethod(){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            getInformationAndSignInUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignInActivity.this, "Thông tin đăng nhập không đúng! Vui lòng kiểm tra và thử lại!",
                                    Toast.LENGTH_SHORT).show();

                            //Dismiss progress bar
                            progressLayout.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void getInformationAndSignInUser(FirebaseUser user) {
        user.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if(task.isSuccessful()){
                            Common.TOKEN = task.getResult().getToken();

                            //Get user from database
                            FoodApiToken.apiService.getUserFromEmail(user.getEmail()).enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    if(response.code() == 200){

                                        User userData = response.body();
                                        if(userData != null){

                                            //Check user is lock or not
                                            if(!userData.isLocked()){

                                                //Check role account
                                                if(userData.getRole().getRoleName().equals("ROLE_SHIPPER")){
                                                    Common.CURRENT_USER = userData;

                                                    //Get shipper id
                                                    getShipperId(userData.getId());

                                                    startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(SignInActivity.this, "Tài khoản không hợp lệ! Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth.getInstance().signOut();

                                                    //Dismiss progress bar
                                                    progressLayout.setVisibility(View.GONE);
                                                }
                                            } else {
                                                Toast.makeText(SignInActivity.this, "Tài khoản của bạn đã bị khoá!", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();

                                                //Dismiss progress bar
                                                progressLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    System.out.println("ERROR: " + t);
                                    Common.showErrorDialog(SignInActivity.this, "Không thể đăng nhập tài khoản! Vui lòng thử lại sau!");
                                }
                            });
                        } else {
                            Toast.makeText(SignInActivity.this, "Error when taking token!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initData(){
        email = edt_email.getText().toString();
        password = edt_password.getText().toString();
    }

    private void initComponent(){
        btn_sign_in = findViewById(R.id.sign_in_button);
        textInput_email = findViewById(R.id.textInput_email);
        textInput_password = findViewById(R.id.textInput_password);

        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);

        progressLayout = findViewById(R.id.progress_layout);

    }

    private void setFontUI() {
        textInput_email.setTypeface(Common.setFontBebas(getAssets()));
        textInput_password.setTypeface(Common.setFontBebas(getAssets()));
    }

    private boolean validData(){
        boolean dataHasValidate = true;

        //Check phone number
        if(email.isEmpty()){
            textInput_email.setError("Email không được bỏ trống!");
            dataHasValidate = false;
        } else {

            //Check email valid
            Pattern patternEmail = Pattern.compile(Common.VALID_EMAIL_ADDRESS_REGEX, Pattern.CASE_INSENSITIVE);
            Matcher matcherEmail = patternEmail.matcher(email);
            if(!matcherEmail.matches()){
                textInput_email.setError("Địa chỉ email không đúng định dạng. Xin vui lòng kiểm tra lại!");
                dataHasValidate = false;
            } else {
                textInput_email.setErrorEnabled(false);
            }
        }

        //Check password
        Pattern patternPassword = Pattern.compile(Common.PASSWORD_PATTERN);
        Matcher matcherPassword = patternPassword.matcher(edt_password.getText().toString());

        if(!matcherPassword.matches() || edt_password.getText().toString().length() < 8) {
            textInput_password.setError("Mật khẩu của bạn cần tối thiểu có 8 ký tự, 1 ký tự viết hoa, 1 số và 1 ký tự đặc biệt!");
            dataHasValidate = false;
        } else {
            textInput_password.setErrorEnabled(false);
        }

        return dataHasValidate;
    }

    private void getShipperId(int userId){
        FoodApi.apiService.getShipper(userId).enqueue(new Callback<Shipper>() {
            @Override
            public void onResponse(Call<Shipper> call, Response<Shipper> response) {
                if(response.code() == 200){
                        Common.CURRENT_SHIPPER = response.body();
                    //Dismiss progress bar
                    progressLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Shipper> call, Throwable t) {
                Toast.makeText(SignInActivity.this, Common.ERROR_CONNECT_SERVER, Toast.LENGTH_SHORT).show();
            }
        });
    }
}