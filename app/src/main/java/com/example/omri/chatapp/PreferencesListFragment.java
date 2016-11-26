package com.example.omri.chatapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.text.SimpleDateFormat;

/**
 * Created by Omri on 26/11/2016.
 */

public class PreferencesListFragment extends Fragment {
    public static final String PREFERENCES = "questions/";
    private RecyclerView preferencesRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;

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
        preferencesRecyclerView = (RecyclerView) view.findViewById(R.id.preferences_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = ref.child(PREFERENCES/* + FirebaseAuth.getInstance().getCurrentUser().getUid()*/);

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Question, PreferencesListFragment.PreferencesViewHolder>(
                Question.class,
                R.layout.question_template,
                PreferencesListFragment.PreferencesViewHolder.class,
                userRef) {
            @Override
            protected void populateViewHolder(PreferencesViewHolder viewHolder, Question model, int position) {
                viewHolder.question.setText(model.getQuestion());
                Log.w("Preference",Integer.toString(model.getAnswer()));
                if(model.getAnswer()==0) {
                    viewHolder.buttonNo.setChecked(true);
                }else
                    viewHolder.buttonYes.setChecked(true);



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
}
