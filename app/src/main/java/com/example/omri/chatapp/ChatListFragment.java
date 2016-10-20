package com.example.omri.chatapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    public static final String CHATS = "chats/";
    private RecyclerView chatRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private LinearLayout emptyView;



    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView chatName;
        public LinearLayout chatLayout;
        public ImageView chatImage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatName = (TextView) itemView.findViewById(R.id.chat_name);
            chatLayout = (LinearLayout) itemView.findViewById(R.id.chat_layout);
            chatImage = (ImageView) itemView.findViewById(R.id.chat_image);
        }
    }

    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> firebaseRecyclerAdapter;

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        getActivity().setTitle("Active Chats");
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        emptyView = (LinearLayout)view.findViewById(R.id.empty_view);
        ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = ref.child(CHATS + FirebaseAuth.getInstance().getCurrentUser().getUid());

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.chat_template,
                ChatViewHolder.class,
                userRef) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, final int position) {
                final String key = firebaseRecyclerAdapter.getRef(position).getKey();
                viewHolder.chatName.setText(model.getName());
                viewHolder.chatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((LobbyCommunicate) getActivity()).accessChat(key);
                    }
                });
                loadUserImage(key,viewHolder.chatImage);

            }


        };


        chatRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .marginResId(R.dimen.chat_divider_left,R.dimen.chat_divider_right)
                .build());
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatRecyclerView.setAdapter(firebaseRecyclerAdapter);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ((LobbyCommunicate)getActivity()).stopProgressBar();
                if(!dataSnapshot.hasChildren()){
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    private void loadUserImage(String userId, final ImageView imageView) {

        ref.child("users").child(userId).child("picUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Picasso.with(getActivity().getApplicationContext())
                            .load(dataSnapshot.getValue().toString())
                            .placeholder(R.drawable.default_user)
                            .fit()
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
