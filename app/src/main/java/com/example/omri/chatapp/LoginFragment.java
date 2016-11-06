package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class LoginFragment extends Fragment implements View.OnClickListener {


    AppCompatButton createBtn;
    AppCompatButton loginBtn;
    EditText emailText;
    EditText passwordText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        createBtn = (AppCompatButton) view.findViewById(R.id.create_button);
        loginBtn = (AppCompatButton) view.findViewById(R.id.login_button);
        emailText = (EditText) view.findViewById(R.id.login_email);
        passwordText = (EditText) view.findViewById(R.id.login_password);
        createBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_button:
                ((MainCommunicate) getActivity()).startSignUp();
                break;
            case R.id.login_button:
                if (validateInput())
                    ((MainCommunicate) getActivity()).login(emailText.getText().toString(), passwordText.getText().toString());
                break;
        }
    }

    private boolean validateInput() {
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


}