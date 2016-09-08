package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    public static final String CHATS = "chats/"+ FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView messageRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;


    public ChatFragment() {
        // Required empty public constructor
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public TextView sender;
        public TextView timeStamp;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageText= (TextView)itemView.findViewById(R.id.message_text);
            sender= (TextView)itemView.findViewById(R.id.sender);
            timeStamp=(TextView)itemView.findViewById(R.id.time_stamp);
        }
    }
    private FirebaseRecyclerAdapter<Message,MessageViewHolder> firebaseRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        //get data from activity
        String chatId = getArguments().getString("chatId");
        Log.w("TAG",chatId);
        getActivity().setTitle("Chat");
        messageRecyclerView= (RecyclerView)view.findViewById(R.id.chat_recycler_view);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        ref= FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.message_template,
                MessageViewHolder.class,
                ref.child(CHATS + "/" + chatId + "/messages")) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
                viewHolder.messageText.setText(model.getMessage());
                viewHolder.sender.setText(model.getSender());
                viewHolder.timeStamp.setText(model.getTime());
            }

        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart,int itemCount){
                super.onItemRangeInserted(positionStart,itemCount);
                int chatCount= firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastVisiblePosition== -1 || (positionStart>=(chatCount-1) && lastVisiblePosition==(positionStart-1))){
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(firebaseRecyclerAdapter);

        return view;
}

}
