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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class RunListFragment extends Fragment {
    public static final String RUNS = "runs/";
    private RecyclerView runsRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;

    public static class RunsViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView creatorText;
        public TextView locationText;
        public TextView runNameText;
        public Button beThereButton;

        public RunsViewHolder(View itemView) {
            super(itemView);
            creatorText = (TextView) itemView.findViewById(R.id.run_creator_text);
            locationText = (TextView) itemView.findViewById(R.id.run_location_text);
            runNameText= (TextView)itemView.findViewById(R.id.run_name_text);
            beThereButton= (Button)itemView.findViewById(R.id.be_there_button);
        }
    }
    private FirebaseRecyclerAdapter<Run, RunsViewHolder> firebaseRecyclerAdapter;

    public RunListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences_list, container, false);
        getActivity().setTitle("Preferences");
        runsRecyclerView = (RecyclerView) view.findViewById(R.id.run_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = ref.child(RUNS/* + FirebaseAuth.getInstance().getCurrentUser().getUid()*/);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Run, RunListFragment.RunsViewHolder>(
                Run.class,
                R.layout.run_template,
                RunListFragment.RunsViewHolder.class,
                userRef) {
            @Override
            protected void populateViewHolder(RunsViewHolder viewHolder, Run model, int position) {
                viewHolder.runNameText.setText(model.getName());
                viewHolder.locationText.setText(model.getLocation());
                viewHolder.creatorText.setText(model.getCreator());
            }



        };




        runsRecyclerView.setLayoutManager(linearLayoutManager);
        runsRecyclerView.setAdapter(firebaseRecyclerAdapter);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                ((LobbyCommunicate)getActivity()).stopProgressBar();
//                if(!dataSnapshot.hasChildren()){
//                    emptyView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }
}