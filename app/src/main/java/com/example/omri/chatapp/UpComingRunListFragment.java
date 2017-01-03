package com.example.omri.chatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Omri on 03/01/2017.
 */

public class UpComingRunListFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "users/";
    private RecyclerView upcomingRunsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button feedBtn;
    private Button upComingBtn;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upcoming_feed_btn:
                ((LobbyCommunicate) getActivity()).enterFeedPage();
                break;
            case R.id.upcoming_history_btn:
                ((LobbyCommunicate) getActivity()).enterHistoryListPage();
                break;
        }
    }

    public static class UpcomingRunsViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView creatorText;
        public TextView locationText;
        public TextView runNameText;
        public Button deletebtn;
        public LinearLayout runLayout;
        public UpcomingRunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.upcoming_run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.upcoming_run_location_text);
            runNameText= (TextView)itemView.findViewById(R.id.upcoming_run_name_text);
            deletebtn= (Button)itemView.findViewById(R.id.delete_btn);
            runLayout = (LinearLayout)itemView.findViewById(R.id.upcoming_run_layout);
        }
    }
    private FirebaseRecyclerAdapter<Run, UpComingRunListFragment.UpcomingRunsViewHolder> firebaseRecyclerAdapter;

    public UpComingRunListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_run_list, container, false);
        getActivity().setTitle("Running History");
        upcomingRunsRecyclerView = (RecyclerView) view.findViewById(R.id.upcoming_run_list_recycler_view);
        feedBtn = (Button) view.findViewById(R.id.upcoming_feed_btn);
        feedBtn.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        String userId = getArguments().getString("userId");
        emptyView = (LinearLayout)view.findViewById(R.id.history_run_empty_view);

        DatabaseReference runRef = ref.child(RUNS + userId+"/HistoryRuns/");

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, UpComingRunListFragment.UpcomingRunsViewHolder>(
                Run.class,
                R.layout.upcoming_run_template,
                UpComingRunListFragment.UpcomingRunsViewHolder.class,
                runRef) {
            @Override
            protected void populateViewHolder(UpcomingRunsViewHolder viewHolder, Run model, int position) {
                final String key = firebaseRecyclerAdapter.getRef(position).getKey();

                viewHolder.runNameText.setText(model.getName());
                viewHolder.locationText.setText(model.getLocation());
                viewHolder.creatorText.setText(model.getCreator());
                viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LobbyCommunicate) getActivity()).enterRunPage(key);
                    }
                });
            }
        };



        upcomingRunsRecyclerView.setLayoutManager(linearLayoutManager);
        upcomingRunsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        runRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((LobbyCommunicate)getActivity()).stopProgressBar();
                if(!dataSnapshot.hasChildren()){
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        return view;
    }
}
