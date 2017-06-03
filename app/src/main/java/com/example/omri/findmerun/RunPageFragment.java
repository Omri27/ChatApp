package com.example.omri.findmerun;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.omri.findmerun.Entities.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

/**
 * Created by Omri on 18/12/2016.
 */

public class RunPageFragment extends Fragment implements View.OnClickListener {
    public static final String RUNS = "runs/";
    private RecyclerView messageRecyclerView;
    private TextView trainerNametxt;
    private TextView dateTimetxt;
    public final String FEEDLIST="feedList";
    public final String COMINGUPLIST="comingUpList";
    public final String SMARTSEARCHLIST="smartSearch";
    private TextView runLocationtxt;
    private TextView distancetxt;
    private TextView suitxt;
    private TextView participantsTxt;
    private EditText textMessage;
    private LinearLayoutManager linearLayoutManager;
    private Button beTherebtn;
    private String runId;
    private boolean isthere;
    private String whichList;
    private DatabaseReference ref;
    private String currentUserId;
    beThereOnclickListener listener;
    private FloatingActionButton sendButton;
    public RunPageFragment(){
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        //public TextView sender;
        public TextView timeStamp;
        public LinearLayout messageLayout;
        public TextView senderName;
        public RelativeLayout messageTemplateLayout;
        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.group_message_text);
            //sender= (TextView)itemView.findViewById(R.id.sender);
            timeStamp = (TextView) itemView.findViewById(R.id.group_time_stamp);
            messageLayout = (LinearLayout) itemView.findViewById(R.id.group_message_layout);
            senderName = (TextView) itemView.findViewById(R.id.sender_name);
            messageTemplateLayout= (RelativeLayout)itemView.findViewById(R.id.messagRelativeLayout);
        }
    }
    private FirebaseRecyclerAdapter<Message, RunPageFragment.MessageViewHolder> firebaseRecyclerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_run_page, container, false);
    //  final View view = inflater.inflate(R.layout.fragment_chat, container, false);
    textMessage = (EditText) view.findViewById(R.id.messageEditText);
    sendButton = (FloatingActionButton) view.findViewById(R.id.sendMessageButton);
    currentUserId = ((LobbyCommunicate) getActivity()).getCurrentUserId();
    ref = FirebaseDatabase.getInstance().getReference();

    //get data from activity
    runId = getArguments().getString("runId");
    whichList = getArguments().getString("whichList");
    trainerNametxt = (TextView) view.findViewById(R.id.trainer_name_txt);
        participantsTxt=  (TextView) view.findViewById(R.id.participants_txt);
    dateTimetxt = (TextView) view.findViewById(R.id.date_time_txt);
    runLocationtxt = (TextView) view.findViewById(R.id.location_txt);
    distancetxt = (TextView) view.findViewById(R.id.distance_txt);
    suitxt = (TextView) view.findViewById(R.id.suit_txt);
    beTherebtn = (Button) view.findViewById(R.id.be_there_btn);
    linearLayoutManager = new LinearLayoutManager(getActivity());
    messageRecyclerView = (RecyclerView) view.findViewById(R.id.group_chat_recycler_view);
    final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String runRef = RUNS+ "/"+runId+"/messages";
    FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("feedRuns").child(runId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            try {
                getActivity().setTitle(((String) dataSnapshot.child("name").getValue()));
                 trainerNametxt.setText((String)dataSnapshot.child("creator").getValue());
                distancetxt.setText((String)dataSnapshot.child("distance").getValue());
                runLocationtxt.setText((String)dataSnapshot.child("location").child("name").getValue());
                participantsTxt.setText(String.valueOf(dataSnapshot.child("runners").getChildrenCount()));
                dateTimetxt.setText(((String)dataSnapshot.child("date").getValue()+ " "+(String)dataSnapshot.child("time").getValue()));
                if(dataSnapshot.hasChild("runPropertyMatch")){
                    suitxt.setText((String) dataSnapshot.child("runPropertyMatch").getValue().toString()+"%");
                }else{
                    suitxt.setVisibility(View.INVISIBLE);
                }
                if(!dataSnapshot.child("creatorId").getValue().toString().equals(currentUserId)) {
                    if (dataSnapshot.child("runners").hasChild(currentUserId)){
                        listener = new beThereOnclickListener(true);
                    }
                    else {
                        listener = new beThereOnclickListener(false);
                    }
                    beTherebtn.setOnClickListener(listener);
                }else{
                    beTherebtn.setText("Delete Run");
                    beTherebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((LobbyCommunicate) (getActivity())).deleteRun(runId);
                           whichList(whichList);
                        }
                    });
                }
            }catch(Exception ex){
                Log.w("exception",ex.toString());
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, RunPageFragment.MessageViewHolder>(
                Message.class,
                R.layout.group_message_template,
                RunPageFragment.MessageViewHolder.class,
                ref.child(runRef)) {
            @Override
            protected void populateViewHolder(RunPageFragment.MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.messageText.setText(model.getMessage());

                viewHolder.senderName.setText(model.getSender());
                if (model.getSenderId().equals(currentUserId)) {
                    viewHolder.messageText.setBackgroundResource(R.drawable.bubble_in);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageLayout.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    viewHolder.messageLayout.setLayoutParams(params);

                } else {
                    viewHolder.messageText.setBackgroundResource(R.drawable.bubble_out);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.messageLayout.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    viewHolder.messageLayout.setLayoutParams(params);


                }


                //viewHolder.sender.setText(model.getSender());
                viewHolder.timeStamp.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(model.getTime()));
            }

        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= (chatCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(firebaseRecyclerAdapter);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textMessage.getText().toString().equals("")) {
                    //String replaced = textMessage.getText().toString().replaceAll("\n","\\n");
                    ((LobbyCommunicate) (getActivity())).sendLobbyMessage(runId, textMessage.getText().toString());
                    textMessage.setText("");
                }
            }
        });

    return view;

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.be_there_btn:
                ((LobbyCommunicate) (getActivity())).signToARun(whichList,runId);
                beTherebtn.setEnabled(false);
                break;
        }
    }
    public class beThereOnclickListener implements View.OnClickListener{
        Boolean isThere;
        public beThereOnclickListener(Boolean isThere){
            this.isThere =isThere;
            if(isThere){
                beTherebtn.setText("Won't Be There");
            }else{
                beTherebtn.setText("Be There");
            }
        }
        @Override
        public void onClick(View view) {
            if(this.isThere){
                ((LobbyCommunicate) (getActivity())).signOutOfARun(whichList,runId);
                whichList(whichList);
                beTherebtn.setText("Won't Be There");
                this.isThere = false;
            }else{
                ((LobbyCommunicate) (getActivity())).signToARun(whichList,runId);
                whichList(whichList);
                this.isThere = true;
                beTherebtn.setText("Be There");
            }

        }

    }
    public void whichList(String whichList){
        switch(whichList) {
            case FEEDLIST:
                ((LobbyCommunicate) (getActivity())).enterFeedPage();
                break;
            case SMARTSEARCHLIST:
                ((LobbyCommunicate) (getActivity())).enterSmartSearchList();
                break;
            case COMINGUPLIST:
                ((LobbyCommunicate) (getActivity())).enterComingupRunList();
                break;
        }
    }
}