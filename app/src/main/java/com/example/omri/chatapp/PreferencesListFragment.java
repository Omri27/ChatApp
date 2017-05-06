package com.example.omri.chatapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.omri.chatapp.Entities.Question;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PreferencesListFragment extends Fragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    public static final String PREFERENCES = "questions";
    public static final String USERS = "users";
    public static final String YES = "1";
    private RecyclerView preferencesRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayout disanceLayout;
    private DatabaseReference ref;
    private LinearLayout emptyView;
    private ArrayList<Question> questionList;
    private Button submit;
    private String existUser;
    private String CurrentuserId;
    private SeekBar seekBar;
    private String activity;
    private int seekValue = 10;
    private TextView seekText;
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        seekText.setText(String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public static class PreferencesViewHolder extends RecyclerView.ViewHolder {
        //public LinearLayout QuestionLayout;
        public TextView question;
        public RadioGroup radioGroup;
        public RadioButton buttonYes;
        public RadioButton buttonNo;

        public PreferencesViewHolder(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.question_text);
            radioGroup = (RadioGroup) itemView.findViewById(R.id.radios_group);
            buttonNo= (RadioButton)itemView.findViewById(R.id.radio_button_no);
            buttonYes= (RadioButton)itemView.findViewById(R.id.radio_button_yes);
        }
    }
    private FirebaseRecyclerAdapter<Question, PreferencesListFragment.PreferencesViewHolder> firebaseRecyclerAdapter;

    public PreferencesListFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences_list, container, false);

        getActivity().setTitle("Preferences");
        seekBar = (SeekBar) view.findViewById(R.id.mySeekBar);
        seekText= (TextView) view.findViewById(R.id.seek_text);
        disanceLayout = (LinearLayout)view.findViewById(R.id.distance_section);


        questionList= new ArrayList<Question>();
        submit= (Button)view.findViewById(R.id.submit_button);
        submit.setOnClickListener(this);
        preferencesRecyclerView = (RecyclerView) view.findViewById(R.id.preferences_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        existUser = getArguments().getString("existUser");
        activity = getArguments().getString("Activity");
        CurrentuserId = getArguments().getString("userId");
        seekBar.setMax(100);
        seekBar.setProgress(seekValue);
        seekBar.setOnSeekBarChangeListener(this);
        Log.w("existuserbla",existUser);


        DatabaseReference userRef;
        DatabaseReference distanceRef= ref.child(USERS).child(CurrentuserId);
        if(existUser.equals(YES))
            userRef = ref.child(USERS).child(CurrentuserId).child("preferences");

        else
            userRef = ref.child(PREFERENCES);


        distanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("radiosDistance"))
                seekValue = Integer.valueOf((String) dataSnapshot.child("radiosDistance").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Question, PreferencesListFragment.PreferencesViewHolder>(
                Question.class,
                R.layout.question_template,
                PreferencesListFragment.PreferencesViewHolder.class,
                userRef) {
            @Override
            protected void populateViewHolder(PreferencesViewHolder viewHolder, Question model, int position) {
                viewHolder.question.setText(model.getQuestion());
                questionList.add(model);
                UserYesnoOnClickListener listener= new UserYesnoOnClickListener(model);
                viewHolder.buttonNo.setOnClickListener(listener);
                viewHolder.buttonYes.setOnClickListener(listener);
                if(model.getAnswer()==0) {
                    viewHolder.buttonNo.setChecked(true);
                }else
                    viewHolder.buttonYes.setChecked(true);
                seekText.setText(String.valueOf(seekValue));
                disanceLayout.setVisibility(View.VISIBLE);

            }



        };




        preferencesRecyclerView.setLayoutManager(linearLayoutManager);
        preferencesRecyclerView.setAdapter(firebaseRecyclerAdapter);


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
    public class UserYesnoOnClickListener implements View.OnClickListener{

        Question question;
        public UserYesnoOnClickListener(Question question) {
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
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.submit_button:
                switch(activity){
                    case "Main":
                        ((MainCommunicate) getActivity()).submitUserPreferences(questionList,seekText.getText().toString() );
                        break;
                    case "Lobby":
                        ((LobbyCommunicate) getActivity()).submitUserPreferences(questionList,seekText.getText().toString() );
                        break;
                }
                break;
        }
    }
}
