package com.example.leeyh.abroadapp.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.leeyh.abroadapp.R;
import com.example.leeyh.abroadapp.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static com.example.leeyh.abroadapp.constants.StaticString.CAMERA_CODE;
import static com.example.leeyh.abroadapp.constants.StaticString.DOB_ERROR;
import static com.example.leeyh.abroadapp.constants.StaticString.ERROR;
import static com.example.leeyh.abroadapp.constants.StaticString.INSUFFICIENT_DATA;
import static com.example.leeyh.abroadapp.constants.StaticString.NOT_FORMATTED_EMAIL;
import static com.example.leeyh.abroadapp.constants.StaticString.WEAK_PASSWORD;

public class SignViewModel extends AndroidViewModel {

    private Application mApplication;
    private MutableLiveData<String> mHandlingErrorFlag;
    public MutableLiveData<String> mName;
    public MutableLiveData<String> mEMail;
    public MutableLiveData<String> mPassword;
    public MutableLiveData<String> mConfirmPassword;
    private MutableLiveData<String> mGender;
    private MutableLiveData<String> mDOBYear;
    private MutableLiveData<String> mDOBMonth;
    private MutableLiveData<String> mDOBDay;
    public MutableLiveData<Bitmap> mProfileBitmap;
    public MutableLiveData<Boolean> mIsRequesting;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    public SignViewModel(Application application) {
        super(application);
        mApplication = application;
        mHandlingErrorFlag = new MutableLiveData<>();
        mName = new MutableLiveData<>();
        mEMail = new MutableLiveData<>();
        mPassword = new MutableLiveData<>();
        mConfirmPassword = new MutableLiveData<>();
        mGender = new MutableLiveData<>();
        mDOBYear = new MutableLiveData<>();
        mDOBMonth = new MutableLiveData<>();
        mDOBDay = new MutableLiveData<>();
        mProfileBitmap = new MutableLiveData<>();
        mIsRequesting = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference().child("userProfileImage");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("users");
    }

    public MutableLiveData<String> getHandlingErrorFlag() {
        return mHandlingErrorFlag;
    }

    public void onGenderCheckChanged(RadioGroup group, int id) {
        switch (id) {
            case R.id.sign_up_man_radio_button:
                mGender.setValue("man");
                break;
            case R.id.sign_up_woman_radio_button:
                mGender.setValue("woman");
                break;
        }
    }

    public void onYearSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mDOBYear.setValue(parent.getSelectedItem().toString());
    }

    public void onMonthSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mDOBMonth.setValue(parent.getSelectedItem().toString());
    }

    public void onDaySpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mDOBMonth.setValue(parent.getSelectedItem().toString());
    }

    public void getImageBitmap(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Glide.with(mApplication).asBitmap().load(data.getData()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        mProfileBitmap.setValue(Bitmap.createScaledBitmap(resource, (int) (resource.getWidth() / 2), (int) (resource.getHeight() / 2), true));
                    }
                });
            }
        }
    }

    private byte[] getBitmapArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mProfileBitmap.getValue().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private void storageProfileToServer() {
        FirebaseUser user = mAuth.getCurrentUser();
        final StorageReference myProfileStorage = mStorageRef.child(user.getUid());
        UploadTask uploadTask = myProfileStorage.putBytes(getBitmapArray());
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mHandlingErrorFlag.setValue(ERROR);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {

                    return null;
                }
                return myProfileStorage.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri downloadUri = task.getResult();
                saveUserInfoToServerDB(downloadUri);
            }
        });
    }

    private void saveUserInfoToServerDB(@Nullable Uri profileUri) {
        String uid = mAuth.getCurrentUser().getUid();
        UserModel myInfo;
        if (profileUri != null) {
            myInfo = new UserModel(uid, mEMail.getValue(), profileUri.toString());
        } else {
            myInfo = new UserModel(uid, mEMail.getValue(), null);
        }
        mDatabaseRef.child(uid).setValue(myInfo);
        mIsRequesting.setValue(false);
    }

    public void onCreateUser(View view) {
        if (mName.getValue() == null && mEMail.getValue() == null && mPassword.getValue() == null && mConfirmPassword.getValue() == null
                && mGender.getValue() == null && mDOBYear.getValue() == null && mDOBMonth.getValue() == null && mDOBDay.getValue() == null) {
            mHandlingErrorFlag.setValue(INSUFFICIENT_DATA);
            return;
        }
        if (mDOBYear.getValue().equals("Year") || mDOBMonth.getValue().equals("Month") || mDOBMonth.getValue().equals("day")) {
            mHandlingErrorFlag.setValue(DOB_ERROR);
            return;
        }
        if (mPassword.getValue().length() < 6) {
            mHandlingErrorFlag.setValue(WEAK_PASSWORD);
            return;
        }

        mIsRequesting.setValue(true);
        mAuth.createUserWithEmailAndPassword(mEMail.getValue(), mPassword.getValue()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //storage user profile
                    if (mProfileBitmap.getValue() == null) {
                        saveUserInfoToServerDB(null);
                    } else {
                        storageProfileToServer();
                    }
                } else {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthInvalidCredentialsException) {
                    mHandlingErrorFlag.setValue(NOT_FORMATTED_EMAIL);
                }
            }
        });
    }

    public static class SignViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private Application mApplication;

        public SignViewModelFactory(@NonNull Application application) {
            mApplication = application;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SignViewModel.class)) {
                return (T) new SignViewModel(mApplication);
            }
            throw new Fragment.InstantiationException("not viewModel class", null);
        }
    }
}
