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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Omri on 26/11/2016.
 */

public class RunFeedListFragment extends Fragment  implements View.OnClickListener{
    public static final String RUNS = "runs/";
    private RecyclerView runsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button historyRunBtn;
    private Button upcomingRunBtn;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feed_history_btn:
                ((LobbyCommunicate) getActivity()).enterHistoryListPage();
                break;
            case R.id.feed_coming_up_btn:
                    ((LobbyCommunicate) getActivity()).enterComingupRunList();
                break;
        }
    }

    public static class RunsViewHolder extends RecyclerView.ViewHolder  {
        //public LinearLayout QuestionLayout;
        public TextView creatorText;
        public TextView locationText;
        public TextView runNameText;
        public Button beThereButton;
        public LinearLayout runLayout;
        public RunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.run_location_text);
            runNameText= (TextView)itemView.findViewById(R.id.run_name_text);
            beThereButton= (Button)itemView.findViewById(R.id.be_there_button);
            runLayout = (LinearLayout)itemView.findViewById(R.id.run_layout);
        }

    }
    private FirebaseRecyclerAdapter<Run, RunsViewHolder> firebaseRecyclerAdapter;

    public RunFeedListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_run_list, container, false);
        getActivity().setTitle("Run Feed");
        historyRunBtn = (Button) view.findViewById(R.id.feed_history_btn);
        upcomingRunBtn = (Button) view.findViewById(R.id.feed_coming_up_btn);
        historyRunBtn.setOnClickListener(this);
        upcomingRunBtn.setOnClickListener(this);
        runsRecyclerView = (RecyclerView) view.findViewById(R.id.run_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        emptyView = (LinearLayout)view.findViewById(R.id.run_empty_view);
        DatabaseReference runRef = ref.child(RUNS/* + FirebaseAuth.getInstance().getCurrentUser().getUid()*/);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, RunFeedListFragment.RunsViewHolder>(
                Run.class,
                R.layout.run_template,
                RunFeedListFragment.RunsViewHolder.class,
                runRef) {
            @Override
            protected void populateViewHolder(RunsViewHolder viewHolder, Run model, int position) {
                try {
                    final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                    Log.w("keybla", key);
                    viewHolder.runNameText.setText(model.getName());
                    viewHolder.locationText.setText(model.getLocation());
                    viewHolder.creatorText.setText(model.getCreator());
                    viewHolder.beThereButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((LobbyCommunicate) getActivity()).signToARun(key);
                        }
                    });
                    viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LobbyCommunicate) getActivity()).enterRunPage(key);
                        }
                    });
                }catch(Exception ex){
                    Log.w("RunFeedlistErr",ex.toString());
                }
            }




        };



        runsRecyclerView.setLayoutManager(linearLayoutManager);
        runsRecyclerView.setAdapter(firebaseRecyclerAdapter);
        try {
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
        }catch (Exception ex){
            Log.w("onemptyerr",ex.toString());
        }



        return view;
    }
}