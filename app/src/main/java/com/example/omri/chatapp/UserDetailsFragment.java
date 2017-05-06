package com.example.omri.chatapp;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.omri.chatapp.Entities.Question;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Omri on 27/04/2017.
 */

public class UserDetailsFragment extends Fragment implements View.OnClickListener{
    private Spinner statusSpinner;
    private EditText birthDate;
    private EditText weightTxt;
    private EditText heightTxt;
    private Spinner relationStatusSpinner;
    private Spinner genderSpinner;
    private SimpleDateFormat dateFormatter;
    private DatePickerDialog dateDialog;
    private ArrayAdapter<CharSequence> relationStatusAdapter;
    private ArrayAdapter<CharSequence> statusAdapter;
    private ArrayAdapter<CharSequence> genderAdapter;
    private String CurrentUserId;
    private String activity;
    private String CurrentUserName;
    Button Submit;
    public UserDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_details, container, false);
        setDateTimePickerDialog();

        CurrentUserId =  getArguments().getString("userId");
        Log.w("CurrentUserId",CurrentUserId);
        CurrentUserName = getArguments().getString("userName");
        getActivity().setTitle(CurrentUserName + " Details");
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        //statusSpinner = (Spinner)view.findViewById(R.id.status_spinner);
        birthDate = (EditText) view.findViewById(R.id.birth_date);
        weightTxt = (EditText) view.findViewById(R.id.weight_txt);
        heightTxt = (EditText) view.findViewById(R.id.height_txt);
       // relationStatusSpinner =  (Spinner)view.findViewById(R.id.relation_status);
       // genderSpinner =  (Spinner)view.findViewById(R.id.gender_spinner);
        Submit =  (Button)view.findViewById(R.id.submit_details_btn);
//        statusAdapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.status_array, android.R.layout.simple_spinner_item);
//
//        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        statusSpinner.setAdapter(statusAdapter);
//        relationStatusAdapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.relation_array, android.R.layout.simple_spinner_item);
//
//        relationStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        relationStatusSpinner.setAdapter(relationStatusAdapter);
//
//        genderAdapter = ArrayAdapter.createFromResource(getActivity(),
//                R.array.gender_array, android.R.layout.simple_spinner_item);
//
//        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        birthDate.setInputType(InputType.TYPE_NULL);
      //  genderSpinner.setAdapter(genderAdapter);
        birthDate.setOnClickListener(this);
        Submit.setOnClickListener(this);
        FirebaseDatabase.getInstance().getReference().child("users").child(CurrentUserId).child("Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if(dataSnapshot.hasChild("weight")) {

                        Submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(validateInput()) {
                                    ((LobbyCommunicate) getActivity()).updateUserDetails(weightTxt.getText().toString(),heightTxt.getText().toString(), birthDate.getText().toString());
                                    // ((LobbyCommunicate) getActivity()).updateUserDetails(weightTxt.getText().toString(),String.valueOf(statusSpinner.getSelectedItemPosition()), String.valueOf(relationStatusSpinner.getSelectedItemPosition()), birthDate.getText().toString(), String.valueOf(genderSpinner.getSelectedItemPosition()));
                                }
                            }
                        });
                       // statusSpinner.setSelection(Integer.valueOf(dataSnapshot.child("generalStatus").getValue().toString()));
                        //relationStatusSpinner.setSelection(Integer.valueOf(dataSnapshot.child("relationStatus").getValue().toString()));
                       // genderSpinner.setSelection(Integer.valueOf(dataSnapshot.child("gender").getValue().toString()));
                        heightTxt.setText((String) dataSnapshot.child("height").getValue());
                        weightTxt.setText((String) dataSnapshot.child("weight").getValue());
                        birthDate.setText((String) dataSnapshot.child("birthDate").getValue());
                    }else{
                        Submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(validateInput()) {
                                    ((MainCommunicate) getActivity()).updateUserDetails(weightTxt.getText().toString(),heightTxt.getText().toString(), birthDate.getText().toString());
                                    // ((LobbyCommunicate) getActivity()).updateUserDetails(weightTxt.getText().toString(),String.valueOf(statusSpinner.getSelectedItemPosition()), String.valueOf(relationStatusSpinner.getSelectedItemPosition()), birthDate.getText().toString(), String.valueOf(genderSpinner.getSelectedItemPosition()));
                                }
                            }
                        });
                    }
                }catch(Exception ex){
                    Log.w("exception",ex.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
    public void setDateTimePickerDialog(){
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(1988,9,19);
        dateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                birthDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.birth_date:
                dateDialog.show();
                break;
        }
    }
    private boolean validateInput() {
        if (birthDate.getText().toString().equals("")) {
            birthDate.setError("Please Submit Your Birth Date");
            return false;
        } else {
            try {
                DateFormat formattercheck =  new SimpleDateFormat("dd-MM-yyyy");
                formattercheck.setLenient(false);
                Date dob = formattercheck.parse(birthDate.getText().toString());
            } catch (Exception e) {
                birthDate.setError("Please type correct Birth Date");
               return false;
            }

        }
        if (weightTxt.getText().toString().equals("")) {
            weightTxt.setError("Please Submit Your Weight");
            return false;
        }
        if (heightTxt.getText().toString().equals("")) {
            heightTxt.setError("Please Submit Your Height");
            return false;
        }
        return true;


    }
}
