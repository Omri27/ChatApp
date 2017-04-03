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



public class ComingUpRunListFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "runs/";
    private RecyclerView upcomingRunsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button feedBtn;
    private Button historyBtn;
    private String currentUserId;
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
            runNameText= (TextView)itemView.findViewById(R.id.upcoming_run_name_text);
            deletebtn= (Button)itemView.findViewById(R.id.delete_btn);
            runLayout = (LinearLayout)itemView.findViewById(R.id.upcoming_run_layout);
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
        historyBtn= (Button) view.findViewById(R.id.comingup_history_btn);
        feedBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        //String userId = getArguments().getString("userId");
        emptyView = (LinearLayout)view.findViewById(R.id.upcoming_run_empty_view);

        DatabaseReference runRef = ref.child(RUNS );

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, ComingUpRunsViewHolder>(
                Run.class,
                R.layout.upcoming_run_template,
                ComingUpRunsViewHolder.class,
                runRef) {
            @Override
            protected void populateViewHolder(ComingUpRunsViewHolder viewHolder, Run model, int position) {
                final String key = firebaseRecyclerAdapter.getRef(position).getKey();

                viewHolder.runNameText.setText(model.getName());
                viewHolder.locationText.setText(model.getLocation());
                viewHolder.creatorText.setText(model.getCreator());
                viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LobbyCommunicate) getActivity()).enterUpComingRunPage(key);
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
