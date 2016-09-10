package com.example.omri.chatapp;

import android.content.Intent;
import android.os.*;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LobbyActivity extends AppCompatActivity implements PeopleFragment.Communicate, ChatListFragment.Communicate,ChatFragment.Communicate{

    private String currentUserName;
    private String currentChatId;
    private String currentRecevierId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        getCurrentUserName();
        if (findViewById(R.id.fragment_container_lobby) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            ChatListFragment chatListFragment = new ChatListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_lobby, chatListFragment).commit();

        }
        //chatRecyclerView= (RecyclerView)findViewById(R.id.chat_recycler_view);


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
            case R.id.find_people:
                PeopleFragment peopleFragment = new PeopleFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby,peopleFragment).addToBackStack(null).commit();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
private void getCurrentUserName(){
    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId).child("name");
    currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            currentUserName = dataSnapshot.getValue(String.class);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
    @Override
    public void startChat(final String receiverId, final String receiverName) {

        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderId).child("name");
        final DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);

        senderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    accessChat(receiverId);
                } else {

                    createChatNodes(receiverName, currentUserName, receiverId);
                    accessChat(receiverId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    private void createChatNodes(String receiverName, String currentUserName,String receiverId){
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(receiverId);
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(receiverId).child(senderId);

        senderRef.setValue(new Chat(receiverName));
        receiverRef.setValue(new Chat(currentUserName));

    }
    @Override
    public void accessChat(String chatId) {
        currentChatId=chatId;
        currentRecevierId=chatId;
        ChatFragment chatFragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("chatId",chatId);
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_lobby,chatFragment).addToBackStack(null).commit();
    }

    @Override
    public void sendMessage(String messageText) {
        String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference().child("chats").child(senderId).child(currentRecevierId).child("messages");
        DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference().child("chats").child(currentRecevierId).child(senderId).child("messages");
        String key = senderRef.push().getKey();
        com.example.omri.chatapp.Message message = new com.example.omri.chatapp.Message(messageText,currentUserName);
        senderRef.child(key).setValue(message);
        receiverRef.child(key).setValue(message);
    }

}
