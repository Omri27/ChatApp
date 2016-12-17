package com.example.omri.chatapp;

import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


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
    public Button createBtn;
    public Button locationBtn;
    public MapView mMapView;
    private GoogleMap googleMap;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private SimpleDateFormat dateFormatter;

    private int mYear, mMonth, mDay, mHour, mMinute;



/***at this time google play services are not initialize so get map and add what ever you want to it in onResume() or onStart() **/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_run, container, false);
        getActivity().setTitle("Create Run");
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        runName = (EditText) view.findViewById(R.id.run_name);
        locationBtn = (Button)view.findViewById(R.id.location_btn);
        runDate= (EditText)view.findViewById(R.id.date_txt);
        dateBtn = (Button) view.findViewById(R.id.date_btn);
        location = (EditText) view.findViewById(R.id.location_txt);
        createBtn= (Button) view.findViewById(R.id.create_btn);
        timeBtn= (Button) view.findViewById(R.id.time_btn);
        runTime = (EditText)view.findViewById(R.id.time_txt);
        dateBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        setDateTimePickerDialog();
        locationBtn.setOnClickListener(this);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if ( ContextCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                }

                // For dropping a marker at a point on the Map
                LatLng sydney = new LatLng(-34, 151);
                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                // googleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
                googleMap.setOnInfoWindowClickListener (new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Log.w("marker","yes");
                    }
                });
            }
        });

        return view;
    }
    public CreateRunFragment(){
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
                mMapView.onResume();
                try {
                    MapsInitializer.initialize(getActivity().getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    @Override
    public void onResume() {
       super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }


}
