package com.example.omri.chatapp;

import android.app.DatePickerDialog;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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


import com.bumptech.glide.Glide;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.fragment;
import static android.app.Activity.RESULT_OK;
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
    private static final int PICK_LOCATION_REQUEST = 1;
    private String runId=null;
    //public MapView mMapView;

    private GoogleMap googleMap;
    private DatePickerDialog dateDialog;
    private TimePickerDialog timeDialog;
    private SimpleDateFormat dateFormatter;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mRequestingLocationUpdates;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private  Intent intent=null;
    private String editRun= "";
    static final int PICK_CONTACT_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_create_run, container, false);

        getActivity().setTitle("Create Run");
        //mMapView = (MapView) view.findViewById(R.id.mapView);
        //mMapView.onCreate(savedInstanceState);
        try {
            runId = getArguments().getString("runId");
        }catch(Exception ex){
            runId=null;
            Log.w("runIderr",ex.toString());
        }
        runName = (EditText) view.findViewById(R.id.run_name);
        locationBtn = (Button)view.findViewById(R.id.location_btn);
        runDate= (EditText)view.findViewById(R.id.date_txt);
        distance= (EditText)view.findViewById(R.id.distance_txt);
        dateBtn = (Button) view.findViewById(R.id.date_btn);
        location = (EditText) view.findViewById(R.id.location_txt);
        nextBtn= (Button) view.findViewById(R.id.next_btn);
        timeBtn= (Button) view.findViewById(R.id.time_btn);
        runTime = (EditText)view.findViewById(R.id.time_txt);
        runDate.setInputType(InputType.TYPE_NULL);
        location.setInputType(InputType.TYPE_NULL);
        runTime.setInputType(InputType.TYPE_NULL);
        dateBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        setDateTimePickerDialog();
        locationBtn.setOnClickListener(this);
        DatabaseReference runRef = FirebaseDatabase.getInstance().getReference();
        if(runId!=null) {
            runRef = FirebaseDatabase.getInstance().getReference().child("runs").child(runId);
            runRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    runName.setText(dataSnapshot.child("name").getValue().toString());
                    runDate.setText(dataSnapshot.child("date").getValue().toString());
                    runTime.setText(dataSnapshot.child("time").getValue().toString());
                    distance.setText(dataSnapshot.child("distance").getValue().toString());
                    Double latitude = Double.valueOf(dataSnapshot.child("location").child("latitude").getValue().toString());
                    Double longtitude = Double.valueOf(dataSnapshot.child("location").child("longtitude").getValue().toString());
                    String locationName = dataSnapshot.child("location").child("name").getValue().toString();
                    editRun= runId;
                    Location runLocation = new Location(locationName);
                    runLocation.setLatitude(latitude);
                    runLocation.setLongitude(longtitude);
                    location.setText(String.valueOf(runLocation.getProvider()));
                    ((LobbyCommunicate) getActivity()).setChosenLocation(runLocation);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
                ((LobbyCommunicate) getActivity()).setLocationNeeded(false);
                ((LobbyCommunicate) getActivity()).activateLocation();
                //Intent pickLocationIntent = new Intent(getActivity(),LocationMapActivity.class);

                // pickContactIntent.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                //startActivityForResult(pickLocationIntent, PICK_LOCATION_REQUEST);
            }
        else if(view==nextBtn){
                if(validateInput()) {

                    ((LobbyCommunicate) getActivity()).createRunPreference(editRun, runName.getText().toString(), runDate.getText().toString(), runTime.getText().toString(), distance.getText().toString());
                }
            }
        }
        private void resetErr(){
            runDate.setError(null);
            runName.setError(null);
            location.setError(null);
            runTime.setError(null);
            distance.setError(null);
        }
    private boolean validateInput() {
        resetErr();
        if (runDate.getText().toString().equals("")) {
            runDate.setError("Please Submit Your Run Date");
            return false;
        } else {
            try {
                DateFormat formattercheck =  new SimpleDateFormat("dd-MM-yyyy");
                formattercheck.setLenient(false);
                Date dob = formattercheck.parse(runDate.getText().toString());
                if(dob.before((new Date()))){
                    runDate.setError("Please type Run Date In the Future");
                    return false;
                }
            } catch (Exception e) {
                runDate.setError("Please type correct Run Date");
                return false;
            }
        }
        if (runName.getText().toString().equals("")) {
            runName.setError("Please Submit Your Run Name");
            return false;
        }
        if (location.getText().toString().equals("")) {
            location.setError("Please Submit Your Run Location");
            return false;
        }
        if (runTime.getText().toString().equals("")) {
            runTime.setError("Please Submit Your Run Time");
            return false;
        }
        if (distance.getText().toString().equals("")) {
            distance.setError("Please Submit Your Run Distance");
            return false;
        }
        return true;


    }
    @Override
    public void onResume() {
        super.onResume();
        Log.w("resumebla","resumebla");
        Location locationChose=null;
            try {
                locationChose =  ((LobbyCommunicate) getActivity()).getChosenLocation();


            if (locationChose != null && locationChose.getLatitude()>0 ) {
                location.setText(String.valueOf(locationChose.getProvider()));
            }
            } catch (Exception ex) {
                Log.w("exceptionbla", ex.toString());
            }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
//        super.onActivityResult(requestCode, resultCode, intent);
//        Log.w("activityResultbla", "enter");
//
//        try {
//          //  Bundle extras = intent.getExtras();
//           // if (extras != null) {
////                Double locationlat = extras.getDouble("LocationLat");
////                Double locationlng = extras.getDouble("LocationLng");
////                String  locationstr = extras.getString("LocationStr");
////                location.setText(locationstr);
//               // location = new Location(locationstr);
//                //location.setLatitude(locationlat);
//               // location.setLongitude(locationlng);
//                //Log.w("activityResultbla", String.valueOf(position));
//                //Bundle bundle = new Bundle();
//                //bundle.putDouble("position", position);
//                //createRunFragment.setArguments(bundle);
//                //Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag("createRun");
//               // getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby, frag,"CreateRun").addToBackStack(null).commit();
//
//        }catch(Exception ex){
//            Log.w("onActivityResultbla",ex.toString());
//           // location=(Location) null;
//
//        }
//    }


}
