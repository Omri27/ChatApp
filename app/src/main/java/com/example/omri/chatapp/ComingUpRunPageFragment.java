package com.example.omri.chatapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

/**
 * Created by Omri on 04/01/2017.
 */

public class ComingUpRunPageFragment extends Fragment implements View.OnClickListener{
        public static final String RUNS = "runs/";
        private RecyclerView messageRecyclerView;
        private TextView trainerNametxt;
        private TextView dateTimetxt;
        private TextView runLocationtxt;
        private TextView distancetxt;
        private TextView suitxt;
        private TextView leveltxt;
        private EditText textMessage;
        private LinearLayoutManager linearLayoutManager;
        private Button cancelBtn;
        private String runId;
        private String userId;
        private DatabaseReference ref;
        private FloatingActionButton sendButton;
        public ComingUpRunPageFragment(){}

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
        private FirebaseRecyclerAdapter<Message, ComingUpRunPageFragment.MessageViewHolder> firebaseRecyclerAdapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment

            View view = inflater.inflate(R.layout.fragment_comingup_run_page, container, false);
            //  final View view = inflater.inflate(R.layout.fragment_chat, container, false);
            textMessage = (EditText) view.findViewById(R.id.messageEditText);
            sendButton = (FloatingActionButton) view.findViewById(R.id.sendMessageButton);
            ref = FirebaseDatabase.getInstance().getReference();
            //get data from activity
           // userId  = getArguments().getString("userId");
            runId = getArguments().getString("runId");
            trainerNametxt= (TextView)view.findViewById(R.id.upcoming_trainer_name_txt);
            dateTimetxt= (TextView)view.findViewById(R.id.upcoming_date_time_txt);
            runLocationtxt= (TextView)view.findViewById(R.id.upcoming_location_txt);
            distancetxt= (TextView)view.findViewById(R.id.upcoming_distance_txt);
            suitxt= (TextView)view.findViewById(R.id.upcoming_suit_txt);
            leveltxt= (TextView)view.findViewById(R.id.upcoming_level_txt);
            cancelBtn = (Button)view.findViewById(R.id.upcoming_Cancell);
            linearLayoutManager = new LinearLayoutManager(getActivity());
            messageRecyclerView = (RecyclerView) view.findViewById(R.id.group_chat_recycler_view);
            cancelBtn.setOnClickListener(this);
            final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String runRef = RUNS+ "/"+runId+"/messages";
            FirebaseDatabase.getInstance().getReference().child("runs").child(runId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    getActivity().setTitle(((String) dataSnapshot.child("name").getValue()));
                    trainerNametxt.setText(dataSnapshot.child("creator").getValue().toString());
                    runLocationtxt.setText(dataSnapshot.child("location").getValue().toString());
                    dateTimetxt.setText(dataSnapshot.child("time").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, ComingUpRunPageFragment.MessageViewHolder>(
                    Message.class,
                    R.layout.group_message_template,
                    ComingUpRunPageFragment.MessageViewHolder.class,
                    ref.child(runRef)) {
                @Override
                protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
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

        }
    }

