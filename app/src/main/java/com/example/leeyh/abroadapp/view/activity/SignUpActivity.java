package com.example.leeyh.abroadapp.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leeyh.abroadapp.R;
import com.example.leeyh.abroadapp.databinding.ActivitySignUpBinding;
import com.example.leeyh.abroadapp.repository.RepositoryListener;
import com.example.leeyh.abroadapp.repository.SignRepository;
import com.example.leeyh.abroadapp.viewmodel.SignViewModel;

import static com.example.leeyh.abroadapp.constants.StaticString.CAMERA_CODE;
import static com.example.leeyh.abroadapp.constants.StaticString.DOB_ERROR;
import static com.example.leeyh.abroadapp.constants.StaticString.ERROR;
import static com.example.leeyh.abroadapp.constants.StaticString.E_MAIL;
import static com.example.leeyh.abroadapp.constants.StaticString.INSUFFICIENT_DATA;
import static com.example.leeyh.abroadapp.constants.StaticString.NOT_FORMATTED_EMAIL;
import static com.example.leeyh.abroadapp.constants.StaticString.NOT_MATCH_PASSWORD;
import static com.example.leeyh.abroadapp.constants.StaticString.SUCCESS;
import static com.example.leeyh.abroadapp.constants.StaticString.WEAK_PASSWORD;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding mBinding;
    private SignViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        //instance repository
        SignRepository repository = new SignRepository();
        repository.setRepositoryListener(new RepositoryListener() {
            @Override
            public void onTaskStarted() {
                mBinding.getRoot().setAlpha(0.6f);
//                mBinding.signUpProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onTaskFinished(String status) {
                mBinding.getRoot().setAlpha(1.0f);
//                mBinding.signUpProgressBar.setVisibility(View.INVISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                switch (status) {
                    case INSUFFICIENT_DATA:
                        Toast.makeText(SignUpActivity.this, "fill the blank", Toast.LENGTH_SHORT).show();
                        break;
                    case DOB_ERROR:
                        Toast.makeText(SignUpActivity.this, "check Date Of Birth", Toast.LENGTH_SHORT).show();
                        break;
                    case WEAK_PASSWORD:
                        Toast.makeText(SignUpActivity.this, "password require more than 6", Toast.LENGTH_SHORT).show();
                        break;
                    case NOT_MATCH_PASSWORD:
                        Toast.makeText(SignUpActivity.this, "not match password. check the password", Toast.LENGTH_SHORT).show();
                        break;
                    case NOT_FORMATTED_EMAIL:
                        Toast.makeText(SignUpActivity.this, "not formatted email", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        Toast.makeText(SignUpActivity.this, "failed to create", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Intent result = new Intent();
//                        result.putExtra(E_MAIL, mBinding.signUpEmailEditTextView.getText().toString());
                        setResult(RESULT_OK, result);
                        finish();
                        break;
                }
            }
        });
        SignViewModel.SignViewModelFactory factory = new SignViewModel.SignViewModelFactory(getApplication(), repository);
        viewModel = ViewModelProviders.of(this, factory).get(SignViewModel.class);
        mBinding.setHandler(viewModel);
        mBinding.setLifecycleOwner(this);

        //set spinner adapter
        ArrayAdapter yearSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext()
                , R.array.date_year, R.layout.simple_spinner_item);
        yearSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter monthSpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext()
                , R.array.date_month, R.layout.simple_spinner_item);
        monthSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter daySpinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext()
                , R.array.date_day, R.layout.simple_spinner_item);
        daySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        mBinding.signUpYearSpinner.setAdapter(yearSpinnerAdapter);
//        mBinding.signUpMonthSpinner.setAdapter(monthSpinnerAdapter);
//        mBinding.signUpDaySpinner.setAdapter(daySpinnerAdapter);

        mBinding.signUpProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getPhoto = new Intent(Intent.ACTION_GET_CONTENT);
                getPhoto.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(getPhoto, CAMERA_CODE);
            }
        });
    }// end of onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        viewModel.getImageBitmap(requestCode, resultCode, data);
    }
}

