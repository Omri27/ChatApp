package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    public static final String CHATS = "chats/";
    private RecyclerView messageRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private EditText textMessage;
    private FloatingActionButton sendButton;
    private String chatId;
    private String receiverToken;

    //private String currentUser;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageText;
        //public TextView sender;
        public TextView timeStamp;
        public LinearLayout messageLayout;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            //sender= (TextView)itemView.findViewById(R.id.sender);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
            messageLayout = (LinearLayout) itemView.findViewById(R.id.message_layout);

        }
    }

    private FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;
//    private void getRecieverToken(String reciverId ){
//        ref.child("users").child(reciverId).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                receiverToken = dataSnapshot.getValue().toString();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);
        textMessage = (EditText) view.findViewById(R.id.messageEditText);
        sendButton = (FloatingActionButton) view.findViewById(R.id.sendMessageButton);

        ref = FirebaseDatabase.getInstance().getReference();
        //get data from activity
        chatId = getArguments().getString("chatId");
        //getRecieverToken(chatId);

        FirebaseDatabase.getInstance().getReference().child("users").child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getActivity().setTitle("Chat with " + ((String) dataSnapshot.child("name").getValue()).split(" ")[0]);
                receiverToken = dataSnapshot.child("token").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageRecyclerView = (RecyclerView) view.findViewById(R.id.chat_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        String chatRef = CHATS + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + chatId + "/messages";
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_template,
                MessageViewHolder.class,
                ref.child(chatRef)) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.messageText.setText(model.getMessage());


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
                viewHolder.timeStamp.setText(model.getTime());
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
                    ((LobbyCommunicate) (getActivity())).sendMessage(textMessage.getText().toString(), receiverToken);
                    textMessage.setText("");
                }
            }
        });
        return view;
    }


}
