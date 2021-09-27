package com.example.sudas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sudas.cards.arrayAdapter;
import com.example.sudas.cards.cards;
import com.example.sudas.matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private ArrayList<String> al;
    private com.example.sudas.cards.arrayAdapter arrayAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private String currentUserId;
    ListView listView;
    List<cards> rowItems;

    private DatabaseReference usersDb;
    SwipeFlingAdapterView flingContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        usersDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);
        checkUserPreferences();

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("noInterest").child(currentUserId).setValue("true");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("interested").child(currentUserId).setValue("true");
                isMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT);
            }
        });

    }

    private void isMatch(String userId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUserId).child("connections").child("interested").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("chats").push().getKey();
                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUserId).child("chatId").setValue(key);
                    usersDb.child(currentUserId).child("connections").child("matches").child(snapshot.getKey()).child("chatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginRegistrationActivity.class);
        startActivity(intent);
    }


    private String userGender;
    private String oppositeGender;

    public void checkUserPreferences() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference currentUserDb = usersDb.child(user.getUid());
        currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("gender").getValue() != null) {
                        userGender = snapshot.child("gender").getValue().toString();
                        switch (userGender) {
                            case "male":
                                oppositeGender = "female";
                                break;
                            case "female":
                                oppositeGender = "male";
                                break;
                        }
                        getOppositeGenderUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getOppositeGenderUsers() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.exists()) &&
                        !snapshot.child("connections").child("match").hasChild(currentUserId) &&
                        !snapshot.child("connections").child("noMatch").hasChild(currentUserId) &&
                        snapshot.child("gender").getValue().toString().equals(oppositeGender)
                ) {
                    String profileImageUrl = "default";
                    if (!snapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }
                    cards item = new cards(snapshot.getKey(),
                            snapshot.child("name").getValue().toString(),
                            profileImageUrl
                    );
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userGender", userGender);
        startActivity(intent);
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
//        intent.putExtra("matchId", )
        startActivity(intent);
        return;
    }
}