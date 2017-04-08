package com.example.omri.chatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.omri.chatapp.Entities.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Omri on 04/01/2017.
 */

public class HistoryRunPageFragment extends Fragment implements View.OnClickListener{
    public static final String RUNS = "users/";
    private TextView trainerNametxt;
    private TextView dateTimetxt;
    private TextView runLocationtxt;
    private TextView distancetxt;
    private TextView suitxt;
    private TextView leveltxt;
    private LinearLayoutManager linearLayoutManager;
    private Button deleteBtn;
    private String runId;
    private String userId;
    private RadioButton yesBtn;
    private RadioButton noBtn;
    private Button updateBtn;
    private DatabaseReference ref;
    private boolean isLike;
    public HistoryRunPageFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_run_page, container, false);
        ref = FirebaseDatabase.getInstance().getReference();
        //get data from activity
        userId  = getArguments().getString("userId");
        runId = getArguments().getString("runId");
        trainerNametxt = (TextView) view.findViewById(R.id.history_trainer_name_txt);
        dateTimetxt = (TextView) view.findViewById(R.id.history_date_time_txt);
        runLocationtxt = (TextView) view.findViewById(R.id.history_location_txt);
        distancetxt = (TextView) view.findViewById(R.id.history_distance_txt);
        suitxt = (TextView) view.findViewById(R.id.history_suit_txt);
       // leveltxt = (TextView) view.findViewById(R.id.history_level_txt);
        deleteBtn = (Button) view.findViewById(R.id.history_run_page_delete);
        yesBtn = (RadioButton) view.findViewById(R.id.history_radio_button_yes);
        noBtn = (RadioButton)view.findViewById(R.id.history_radio_button_no);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        deleteBtn.setOnClickListener(this);
        updateBtn = (Button) view.findViewById(R.id.history_run_page_update);
        updateBtn.setOnClickListener(this);
        //final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("historyRuns").child(runId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // getActivity().setTitle(((String) dataSnapshot.child("name").getValue()));
                trainerNametxt.setText(dataSnapshot.child("creator").getValue().toString());
                runLocationtxt.setText(dataSnapshot.child("location").getValue().toString());
                dateTimetxt.setText(dataSnapshot.child("time").getValue().toString());
                HistoryYesnoOnClickListener yesNoListener = new HistoryYesnoOnClickListener();
                noBtn.setOnClickListener(yesNoListener);
                yesBtn.setOnClickListener(yesNoListener);
                if(dataSnapshot.child("like").getValue().equals(false)){
                    isLike= false;
                    noBtn.setChecked(true);
                }else {
                    isLike=true;
                    yesBtn.setChecked(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history_run_page_update:
                ((LobbyCommunicate) getActivity()).updateLike(runId,isLike);
                break;
//            case R.id.feed_coming_up_btn:
//                ((LobbyCommunicate) getActivity()).enterComingupRunPage();
//                break;
        }
    }
    public class HistoryYesnoOnClickListener implements View.OnClickListener{

        public HistoryYesnoOnClickListener() {

        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.history_radio_button_no:
                    isLike=false;
                    break;
                case R.id.history_radio_button_yes:
                    isLike=true;
                    break;
            }
        }
    }
}
