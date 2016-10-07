package com.example.omri.chatapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.net.URI;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    public static final int GALLERY_REQUEST_CODE = 1;

    private Button signupBtn;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;
    private ImageView selectProfilePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        nameText = (EditText) view.findViewById(R.id.signup_name);
        emailText = (EditText) view.findViewById(R.id.signup_email);
        passwordText = (EditText) view.findViewById(R.id.signup_password);
        signupBtn = (Button) view.findViewById(R.id.signup_button);
        selectProfilePic = (ImageView)view.findViewById(R.id.select_profile_pic);
        signupBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_button:
                if (validateInput())
                    ((Communicate) getActivity()).signUp(nameText.getText().toString(), emailText.getText().toString(), passwordText.getText().toString());
                    break;
            case R.id.select_profile_pic:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, GALLERY_REQUEST_CODE);

        }
    }

    private boolean validateInput() {
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




        }

    }

    interface Communicate {
        void signUp(String name, String email, String password);


    }

}
