package com.example.omri.chatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.analytics.HitBuilders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ComingUpRunListFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "runs/";
    private RecyclerView upcomingRunsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button feedBtn;
    private Button historyBtn;
    private String currentUserId;
    private ArrayList<String> upcomingRuns;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comingup_feed_btn:
                ((LobbyCommunicate) getActivity()).enterFeedPage();
                break;
            case R.id.comingup_history_btn:
                ((LobbyCommunicate) getActivity()).enterHistoryListPage();
                break;
        }
    }

    public static class ComingUpRunsViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView creatorText;
        public TextView locationText;
        public TextView runNameText;
        public Button deletebtn;
        public LinearLayout runLayout;

        public ComingUpRunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.upcoming_run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.upcoming_run_location_text);
            runNameText = (TextView) itemView.findViewById(R.id.upcoming_run_name_text);
            deletebtn = (Button) itemView.findViewById(R.id.upcoming_Cancell);
            runLayout = (LinearLayout) itemView.findViewById(R.id.upcoming_run_layout);
        }
    }

    private FirebaseRecyclerAdapter<Run, ComingUpRunsViewHolder> firebaseRecyclerAdapter;

    public ComingUpRunListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comingup_run_list, container, false);
        currentUserId = ((LobbyCommunicate) getActivity()).getCurrentUserId();
        getActivity().setTitle("ComingUp Runs");
        upcomingRunsRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_run_list_recycler_view);
        feedBtn = (Button) view.findViewById(R.id.comingup_feed_btn);
        historyBtn = (Button) view.findViewById(R.id.comingup_history_btn);
        feedBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        //String userId = getArguments().getString("userId");
        emptyView = (LinearLayout) view.findViewById(R.id.upcoming_run_empty_view);
        upcomingRuns = new ArrayList<String>();
        FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("comingUpRuns").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.w("child", child.getKey().toString());
                        upcomingRuns.add(child.getKey().toString());
                    }
                    SetView();

                    // trainerNametxt.setText(dataSnapshot.child("creator").getValue().toString());
                } catch (Exception ex) {
                    Log.w("exception", ex.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference runRef = ref.child(RUNS);
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, ComingUpRunsViewHolder>(
//                Run.class,
//                R.layout.upcoming_run_template,
//                ComingUpRunsViewHolder.class,
//                runRef) {
//            @Override
//            protected void populateViewHolder(ComingUpRunsViewHolder viewHolder, Run model, int position) {
//                try {
//                    final String key = firebaseRecyclerAdapter.getRef(position).getKey();
//                    Log.w("comingupkey",key);
//                    if (upcomingRuns.contains(key)) {
//                        viewHolder.runNameText.setText(model.getName());
//                        viewHolder.locationText.setText(model.getLocation());
//                        viewHolder.creatorText.setText(model.getCreator());
//                        viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ((LobbyCommunicate) getActivity()).enterUpComingRunPage(key);
//                            }
//                        });
//                    }else{
//                        viewHolder.runLayout.setVisibility(View.GONE);
//                    }
//                }catch(Exception ex){
//                    Log.w("upcomingException",ex.toString());
//                }
//                }
//
//        };
//
//        upcomingRunsRecyclerView.setLayoutManager(linearLayoutManager);
//        upcomingRunsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        try {
            runRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        ((LobbyCommunicate) getActivity()).stopProgressBar();
                        if (!dataSnapshot.hasChildren()) {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }catch(Exception ex){
                        Log.w("cominguplistonempty",ex.toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch(Exception ex){
            Log.w("cominguplistonempty",ex.toString());
        }
        return view;
    }

    private void SetView() {
        try {
            DatabaseReference runRef = ref.child(RUNS);
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, ComingUpRunsViewHolder>(
                    Run.class,
                    R.layout.upcoming_run_template,
                    ComingUpRunsViewHolder.class,
                    runRef) {
                @Override
                protected void populateViewHolder(ComingUpRunsViewHolder viewHolder, Run model, int position) {
                    try {
                        final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                        Log.w("comingupkey", key);
                        if (upcomingRuns.contains(key)) {
                            viewHolder.runNameText.setText(model.getName());
                            viewHolder.locationText.setText(model.getLocation());
                            viewHolder.creatorText.setText(model.getCreator());
                            viewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((LobbyCommunicate) getActivity()).signOutOfARun(key);
                                    upcomingRuns.remove(key);
                                    SetView();
                                }
                            });
                            viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((LobbyCommunicate) getActivity()).enterUpComingRunPage(key);
                                }
                            });
                        } else {
                            viewHolder.runLayout.setVisibility(View.GONE);
                        }
                    } catch (Exception ex) {
                        Log.w("upcomingException", ex.toString());
                    }
                }

            };
            upcomingRunsRecyclerView.setLayoutManager(linearLayoutManager);
            upcomingRunsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        }catch(Exception ex){
            Log.w("SetViewbla",ex.toString());
        }
    }
}
