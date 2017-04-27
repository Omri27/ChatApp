package com.example.omri.chatapp;

import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.R.attr.fragment;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

/**
 * Created by Omri on 26/11/2016.
 */

public class CreateRunFragment extends Fragment implements View.OnClickListener {
    private EditText runName;
    public EditText runDate;
    public Button dateBtn;
    public Button timeBtn;
    public EditText runTime;
    public EditText location;
    public EditText distance;
    public Button nextBtn;
    public Button locationBtn;
    //public MapView mMapView;
    private GoogleMap googleMap;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private SimpleDateFormat dateFormatter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mRequestingLocationUpdates;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private  Intent intent=null;
    static final int PICK_CONTACT_REQUEST = 1;


    public static class PreferencesViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView question;
        public RadioGroup radioGroup;
        public RadioButton buttonYes;
        public RadioButton buttonNo;

        public PreferencesViewHolder(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.question_text);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radios_group);
            buttonNo= (RadioButton)itemView.findViewById(R.id.radio_button_no);
            buttonYes= (RadioButton)itemView.findViewById(R.id.radio_button_yes);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_run, container, false);

        getActivity().setTitle("Create Run");
        //mMapView = (MapView) view.findViewById(R.id.mapView);
        //mMapView.onCreate(savedInstanceState);
        runName = (EditText) view.findViewById(R.id.run_name);
        locationBtn = (Button)view.findViewById(R.id.location_btn);
        runDate= (EditText)view.findViewById(R.id.date_txt);
        distance= (EditText)view.findViewById(R.id.distance_txt);
        dateBtn = (Button) view.findViewById(R.id.date_btn);
        location = (EditText) view.findViewById(R.id.location_txt);
        nextBtn= (Button) view.findViewById(R.id.next_btn);
        timeBtn= (Button) view.findViewById(R.id.time_btn);
        runTime = (EditText)view.findViewById(R.id.time_txt);
        dateBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        setDateTimePickerDialog();
        locationBtn.setOnClickListener(this);

        //mMapView.getMapAsync(this);


        return view;
    }

    public void setDateTimePickerDialog(){
        Calendar newCalendar = Calendar.getInstance();

        dateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                runDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        timeDialog= new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                runTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY),
                newCalendar.get(Calendar.MINUTE),true);
    }

    @Override
    public void onClick(View view) {
        if(view == dateBtn){

            dateDialog.show();

        }else
            if(view == timeBtn){
                timeDialog.show();
            }
        else if(view == locationBtn){

                ((LobbyCommunicate) getActivity()).activateLocation();

            }
        else if(view==nextBtn){
                ((LobbyCommunicate) getActivity()).createRunPreference(runName.getText().toString(),runDate.getText().toString(),runTime.getText().toString(),distance.getText().toString());
            }
        }
    @Override
    public void onResume() {
        super.onResume();
        Log.w("resumebla","resumebla");
        Location locationChose=null;
            try {
                locationChose =  ((LobbyCommunicate) getActivity()).getChosenLocation();

            } catch (Exception ex) {
                Log.w("exceptionbla", ex.toString());
            }
            if (locationChose != null && locationChose.getLatitude()>0 ) {
                location.setText(String.valueOf(locationChose.getProvider()));
            }
    }



}
