package com.example.omri.findmerun;

import android.graphics.Color;
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

import com.example.omri.findmerun.Entities.Run;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ComingUpRunListFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "users/";
    private RecyclerView upcomingRunsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    public final String COMINGUPLIST="comingUpList";
    private Button feedBtn;
    private Button historyBtn;
    private Button smartSearchBtn;
    private String currentUserId;
    private Date nowDate = new Date();
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comingup_feed_btn:
                ((LobbyCommunicate) getActivity()).enterFeedPage();
                break;
            case R.id.comingup_history_btn:
                ((LobbyCommunicate) getActivity()).enterHistoryListPage();
                break;
            case R.id.comingup__smart_search_btn:
                ((LobbyCommunicate) getActivity()).enterSmartSearchList();
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
        public Button editBtn;

        public ComingUpRunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.upcoming_run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.upcoming_run_location_text);
            runNameText = (TextView) itemView.findViewById(R.id.upcoming_run_name_text);
            deletebtn = (Button) itemView.findViewById(R.id.upcoming_Cancell);
            editBtn =(Button) itemView.findViewById(R.id.upcoming_edit);
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
        smartSearchBtn= (Button) view.findViewById(R.id.comingup__smart_search_btn);
        feedBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        smartSearchBtn.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        //String userId = getArguments().getString("userId");
        emptyView = (LinearLayout) view.findViewById(R.id.upcoming_run_empty_view);
        DatabaseReference runRef = ref.child(RUNS+"/"+currentUserId +"/comingUpRuns");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, ComingUpRunsViewHolder>(
                Run.class,
                R.layout.upcoming_run_template,
                ComingUpRunsViewHolder.class,
                runRef) {
            @Override
            protected void populateViewHolder(ComingUpRunsViewHolder viewHolder, Run model, int position) {
                try {
                    DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                    final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                    Log.w("comingupkey", key);
                    Log.w("runnersobject", String.valueOf(model.getRunners().toString().contains(currentUserId)));
                    Date date = formatter.parse(model.getDate() + " " + model.getTime());
                    if (date.after(nowDate)){
                        if (model.getRunners().toString().contains(currentUserId) || model.getCreatorId().toString().contains(currentUserId)) {
                            viewHolder.runNameText.setText(model.getName());
                            viewHolder.locationText.setText(model.getLocation().getName());
                            viewHolder.creatorText.setText(model.getCreator());
                            if (!model.getCreatorId().toString().contains(currentUserId)) {
                                viewHolder.deletebtn.setText("Won't Be There");
                                viewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((LobbyCommunicate) getActivity()).signOutOfARun(COMINGUPLIST,key);
                                    }
                                });

                            } else {
                                viewHolder.runLayout.setBackgroundColor(Color.GREEN);
                                viewHolder.deletebtn.setText("Delete Run");
                                viewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((LobbyCommunicate) getActivity()).deleteRun(key);
                                    }
                                });
                                viewHolder.editBtn.setVisibility(View.VISIBLE);
                                viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((LobbyCommunicate) getActivity()).enterEditRun(key);
                                    }
                                });
                            }
                            viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ((LobbyCommunicate) getActivity()).enterUpComingRunPage(key);
                                }
                            });
                        } else {
                            viewHolder.runLayout.setVisibility(View.GONE);
                        }
                }
                    }catch(Exception ex){
                        Log.w("upcomingException", ex.toString());
                    }
                }


        };

        upcomingRunsRecyclerView.setLayoutManager(linearLayoutManager);
        upcomingRunsRecyclerView.setAdapter(firebaseRecyclerAdapter);
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
}
