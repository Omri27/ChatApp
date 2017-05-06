package com.example.omri.chatapp;

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

import com.example.omri.chatapp.Entities.Run;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Omri on 03/01/2017.
 */

public class HistoryRunListFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "users/";
    private RecyclerView historyRunsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button feedBtn;
    private Button comingUpBtn;
    private Button smartSearchBtn;
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.history_coming_up_btn:
                ((LobbyCommunicate) getActivity()).enterComingupRunList();
                break;
            case R.id.history_feed_btn:

                ((LobbyCommunicate) getActivity()).enterFeedPage();
                break;
            case R.id.history_smart_search_btn:
                ((LobbyCommunicate) getActivity()).enterSmartSearchList();
                break;

        }
    }

    public static class HistoryRunsViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView creatorText;
        public TextView locationText;
        public TextView runNameText;
        public Button deletebtn;
        public LinearLayout runLayout;
        public HistoryRunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.history_run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.history_run_location_text);
            runNameText= (TextView)itemView.findViewById(R.id.history_run_name_text);
            deletebtn= (Button)itemView.findViewById(R.id.delete_btn);
            runLayout = (LinearLayout)itemView.findViewById(R.id.history_run_layout);
        }
    }
    private FirebaseRecyclerAdapter<Run, HistoryRunListFragment.HistoryRunsViewHolder> firebaseRecyclerAdapter;

    public HistoryRunListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

            View view = inflater.inflate(R.layout.fragment_history_run_list, container, false);
            getActivity().setTitle("Running History");
            historyRunsRecyclerView = (RecyclerView) view.findViewById(R.id.history_run_list_recycler_view);
            feedBtn = (Button) view.findViewById(R.id.history_feed_btn);
            comingUpBtn = (Button) view.findViewById(R.id.history_coming_up_btn);
            smartSearchBtn = (Button) view.findViewById(R.id.history_smart_search_btn);
            smartSearchBtn.setOnClickListener(this);
            comingUpBtn.setOnClickListener(this);
            feedBtn.setOnClickListener(this);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            ref = FirebaseDatabase.getInstance().getReference();
            final String userId = getArguments().getString("userId");
            emptyView = (LinearLayout) view.findViewById(R.id.history_run_empty_view);

            DatabaseReference runRef = ref.child(RUNS).child(userId).child("historyRuns");

            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, HistoryRunListFragment.HistoryRunsViewHolder>(
                    Run.class,
                    R.layout.history_run_template,
                    HistoryRunListFragment.HistoryRunsViewHolder.class,
                    runRef) {
                @Override
                protected void populateViewHolder(HistoryRunsViewHolder viewHolder, Run model, int position) {
                    try {
                        final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                        if (model.getCreatorId().toString().contains(userId)) {
                            viewHolder.runLayout.setBackgroundColor(Color.GREEN);
                        }
                        viewHolder.runNameText.setText(model.getName());
                        viewHolder.locationText.setText(model.getLocation().getName());
                        viewHolder.creatorText.setText(model.getCreator());
                        viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((LobbyCommunicate) getActivity()).enterHistoryRunPage(key);
                            }
                        });
                        viewHolder.deletebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((LobbyCommunicate) getActivity()).deleteHistoryRun(key);
                            }
                        });
                    } catch (Exception ex) {
                        Log.w("exceptionhistory", ex.toString());
                    }
                }
            };


            historyRunsRecyclerView.setLayoutManager(linearLayoutManager);
            historyRunsRecyclerView.setAdapter(firebaseRecyclerAdapter);
            runRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ((LobbyCommunicate) getActivity()).stopProgressBar();
                    if (!dataSnapshot.hasChildren()) {
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
