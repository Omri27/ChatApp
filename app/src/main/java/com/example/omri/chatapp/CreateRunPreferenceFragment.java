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

import java.util.ArrayList;

/**
 * Created by Omri on 04/01/2017.
 */

public class CreateRunPreferenceFragment extends Fragment implements View.OnClickListener {
    public static final String PREFERENCES = "questions/";
    private RecyclerView createRunPreferenceRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private Button createRun;
    private ArrayList<Question> questionList;
    @Override
    public void onClick(View view) {
    switch(view.getId()){
        case R.id.create_run_button:
            Bundle args = getArguments();
            ((LobbyCommunicate) getActivity()).createRun(args.getString("runName"),args.getString("runDate"),args.getString("runTime"),questionList);
    }
    }

    public static class CreateRunPreferencesViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView question;
        public RadioGroup radioGroup;
        public RadioButton buttonYes;
        public RadioButton buttonNo;

        public CreateRunPreferencesViewHolder(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.question_text);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radios_group);
            buttonNo= (RadioButton)itemView.findViewById(R.id.radio_button_no);
            buttonYes= (RadioButton)itemView.findViewById(R.id.radio_button_yes);
        }
    }
    private FirebaseRecyclerAdapter<Question, CreateRunPreferenceFragment.CreateRunPreferencesViewHolder> firebaseRecyclerAdapter;

    public CreateRunPreferenceFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_run_preference, container, false);


        questionList =new ArrayList<Question>();
        getActivity().setTitle("Run Preferences");
        createRun = (Button)view.findViewById(R.id.create_run_button);
        createRunPreferenceRecyclerView = (RecyclerView) view.findViewById(R.id.create_run_preferences_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = ref.child(PREFERENCES/* + FirebaseAuth.getInstance().getCurrentUser().getUid()*/);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Question, CreateRunPreferenceFragment.CreateRunPreferencesViewHolder>(
                Question.class,
                R.layout.question_template,
                CreateRunPreferenceFragment.CreateRunPreferencesViewHolder.class,
                userRef) {
            @Override
            protected void populateViewHolder(CreateRunPreferencesViewHolder viewHolder, Question model, int position) {
                viewHolder.question.setText(model.getQuestion());
                questionList.add(model);
                YesnoOnClickListener listener= new YesnoOnClickListener(model);
                viewHolder.buttonNo.setOnClickListener(listener);
                viewHolder.buttonYes.setOnClickListener(listener);
                if(model.getAnswer()==0) {
                    viewHolder.buttonNo.setChecked(true);
                }else {
                    viewHolder.buttonYes.setChecked(true);
                }
            }



        };




        createRunPreferenceRecyclerView.setLayoutManager(linearLayoutManager);
        createRunPreferenceRecyclerView.setAdapter(firebaseRecyclerAdapter);
        createRun.setOnClickListener(this);

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
    public class YesnoOnClickListener implements View.OnClickListener{

        Question question;
        public YesnoOnClickListener(Question question) {
            this.question = question;
        }
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.radio_button_no:
                question.setAnswer(0);
                    break;
                case R.id.radio_button_yes:
                    question.setAnswer(1);
                    break;
            }
        }
    }
}