package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {



    private Button signupBtn;
    private EditText nameText;
    private EditText emailText;
    private EditText passwordText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        nameText = (EditText) view.findViewById(R.id.signup_name);
        emailText = (EditText) view.findViewById(R.id.signup_email);
        passwordText = (EditText) view.findViewById(R.id.signup_password);
        signupBtn = (Button)view.findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signup_button:
                if(validateInput())
                    ((Communicate)getActivity()).signUp(nameText.getText().toString(),emailText.getText().toString(),passwordText.getText().toString());
        }
    }

    private boolean validateInput() {
        if(nameText.getText().toString().equals("")){
            nameText.setError("Please enter name!");
            return false;
        }
        if(emailText.getText().toString().equals("")) {
            emailText.setError("Please enter email!");
            return false;
        }
        if(passwordText.getText().toString().equals("")){
            passwordText.setError("Please enter password!");
            return false;
        }
        return true;


    }
    interface Communicate{
         void signUp(String name, String email,String password);
    }

}
