package com.example.omri.chatapp;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omri.chatapp.Entities.HistoryRun;
import com.example.omri.chatapp.Entities.Run;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;



/**
 * Created by Omri on 26/11/2016.
 */

public class RunFeedListFragment extends Fragment  implements View.OnClickListener {
    public static final String RUNS = "users/";
    private RecyclerView runsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private String currentUserId;
    public final String FEEDLIST="feedList";
    private LinearLayout emptyView;
    private Button historyRunBtn;
    private Button upcomingRunBtn;
    private Button smartSearchBtn;
    private Date nowDate = new Date();
    public Location deviceLocation;
    private HistoryRun history;
    ArrayList<Run> runList = new ArrayList<Run>();

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feed_history_btn:
                ((LobbyCommunicate) getActivity()).enterHistoryListPage();
                break;
            case R.id.feed_coming_up_btn:
                Log.w("feed_coming_up_btn", "feed_coming_up_btn");
                ((LobbyCommunicate) getActivity()).enterComingupRunList();
                break;
            case R.id.feed_smart_search_btn:
                Log.w("feed_smart_search_btn", "feed_smart_search_btn");
                ((LobbyCommunicate) getActivity()).enterSmartSearchList();
                break;
        }
    }

    public static class RunsViewHolder extends RecyclerView.ViewHolder {
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
            runNameText = (TextView) itemView.findViewById(R.id.run_name_text);
            beThereButton = (Button) itemView.findViewById(R.id.be_there_button);
            runLayout = (LinearLayout) itemView.findViewById(R.id.run_layout);
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
        ((LobbyCommunicate) getActivity()).getCurrentUserId();
        historyRunBtn = (Button) view.findViewById(R.id.feed_history_btn);
        upcomingRunBtn = (Button) view.findViewById(R.id.feed_coming_up_btn);
        smartSearchBtn = (Button) view.findViewById(R.id.feed_smart_search_btn);
        currentUserId = ((LobbyCommunicate) getActivity()).getCurrentUserId();
        historyRunBtn.setOnClickListener(this);
        upcomingRunBtn.setOnClickListener(this);
        smartSearchBtn.setOnClickListener(this);
        runsRecyclerView = (RecyclerView) view.findViewById(R.id.run_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        emptyView = (LinearLayout) view.findViewById(R.id.run_empty_view);

        DatabaseReference runRef = ref.child(RUNS+"/"+currentUserId+"/feedRuns"/*FirebaseAuth.getInstance().getCurrentUser().getUid()*/);
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, RunFeedListFragment.RunsViewHolder>(
                Run.class,
                R.layout.run_template,
                RunFeedListFragment.RunsViewHolder.class,
                runRef.orderByChild("distanceFrom")) {
            @Override
            protected void populateViewHolder(RunsViewHolder viewHolder, final Run model, int position) {
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
               try {


                    firebaseRecyclerAdapter.getItem(position);
                    Date date = formatter.parse(model.getDate()+" "+model.getTime());
                    if(date.after(nowDate)) {
                        final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                        viewHolder.runNameText.setText(model.getName());
                        viewHolder.locationText.setText(model.getLocation().getName());
                        viewHolder.creatorText.setText(model.getCreator());
                        if(!model.getCreatorId().toString().contains(currentUserId)){
                            if (model.getRunners().toString().contains(currentUserId)) {
                                viewHolder.beThereButton.setText("Won't Be There");
                                viewHolder.beThereButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((LobbyCommunicate) getActivity()).signOutOfARun(FEEDLIST,key);
                                    }
                                });
                            } else {
                                viewHolder.beThereButton.setText("Be There");
                                viewHolder.beThereButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ((LobbyCommunicate) getActivity()).signToARun(FEEDLIST,key);
                                    }
                                });
                            }
                        }else{
                            viewHolder.runLayout.setBackgroundColor(Color.GREEN);
                            viewHolder.beThereButton.setText("Delete Run");
                            viewHolder.beThereButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((LobbyCommunicate) getActivity()).deleteRun(key);
                                    ((LobbyCommunicate) getActivity()).enterFeedPage();
                                }
                            });
                        }
                        viewHolder.runLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((LobbyCommunicate) getActivity()).enterRunPage(key,FEEDLIST);
                            }
                        });
                    }
                    else{
                        viewHolder.runLayout.setVisibility(View.GONE);
                    }
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
        } catch (Exception ex) {
            Log.w("onemptyerr", ex.toString());
        }


        return view;
    }

}