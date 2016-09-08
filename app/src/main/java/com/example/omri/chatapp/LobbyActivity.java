package com.example.omri.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LobbyActivity extends AppCompatActivity {



    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        public TextView chatName;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatName= (TextView)itemView.findViewById(R.id.chat_name);
        }
    }
    public static final String CHATS= "chats/"+FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView chatRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private FirebaseRecyclerAdapter<Chat,ChatViewHolder> firebaseRecyclerAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        chatRecyclerView= (RecyclerView)findViewById(R.id.chat_recycler_view);
        linearLayoutManager= new LinearLayoutManager(this);
        ref= FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(
                Chat.class,
                R.layout.chat_template,
                ChatViewHolder.class,
                ref.child(CHATS )) {
            @Override
            protected void populateViewHolder(ChatViewHolder viewHolder, Chat model, int position) {
                viewHolder.chatName.setText(model.getName());

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




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
