package com.example.omri.chatapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

//import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    public static final int GALLERY_REQUEST_CODE = 1;

    private AppCompatButton signupBtn;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private ImageView selectProfilePic;
    private ImageView selectedProfilePic;
    private Uri profileImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        nameText = (EditText) view.findViewById(R.id.signup_name);
        emailText = (EditText) view.findViewById(R.id.signup_email);
        passwordText = (EditText) view.findViewById(R.id.signup_password);
        signupBtn = (AppCompatButton) view.findViewById(R.id.signup_button);
        selectProfilePic = (ImageView)view.findViewById(R.id.select_profile_pic);
        selectedProfilePic = (ImageView)view.findViewById(R.id.selected_profile_pic);
        signupBtn.setOnClickListener(this);
        selectProfilePic.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button:
                if (validateInput())

                    ((MainCommunicate) getActivity()).signUp(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString(),profileImageUri);
                    break;
            case R.id.select_profile_pic:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALLERY_REQUEST_CODE);
break;
        }
    }
    private void resetErr(){
        nameText.setError(null);
        emailText.setError(null);
        passwordText.setError(null);

    }
    private boolean validateInput() {
        resetErr();
        if (nameText.getText().toString().equals("")) {
            nameText.setError("Please enter name!");
            return false;
        }
        if (emailText.getText().toString().equals("")) {
            emailText.setError("Please enter email!");
            return false;
        }
        if (passwordText.getText().toString().equals("")) {
            passwordText.setError("Please enter password!");
            return false;
        }
        return true;


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE  && resultCode == RESULT_OK && data != null ) {

            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getContext(), this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImageUri = result.getUri();
                Glide.with(getActivity().getApplicationContext())
                        .load(profileImageUri)
                        .fitCenter()
                        .into(selectedProfilePic);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }



}
