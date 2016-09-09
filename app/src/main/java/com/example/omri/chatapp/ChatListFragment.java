package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    public static final String CHATS= "chats/";
    private RecyclerView chatRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;


    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView chatName;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatName= (TextView)itemView.findViewById(R.id.chat_name);
        }
    }
    private FirebaseRecyclerAdapter<Chat,ChatViewHolder> firebaseRecyclerAdapter;
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list,container,false);
        getActivity().setTitle("Active Chats");
        chatRecyclerView= (RecyclerView)view.findViewById(R.id.chat_list_recycler_view);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        ref= FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.chat_template,
                ChatViewHolder.class,
                ref.child(CHATS + FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, final int position) {
                viewHolder.chatName.setText(model.getName());
                viewHolder.chatName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Communicate)getActivity()).accessChat(firebaseRecyclerAdapter.getRef(position).getKey());
                    }
                });

            }
        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart,int itemCount){
                super.onItemRangeInserted(positionStart,itemCount);
                int chatCount= firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastVisiblePosition== -1 || (positionStart>=(chatCount-1) && lastVisiblePosition==(positionStart-1))){
                    chatRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(firebaseRecyclerAdapter);




        return view;
    }

    interface Communicate{
        void accessChat(String chatId);
    }
}
