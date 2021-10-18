package com.example.sudas;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sudas.cards.arrayAdapter;
import com.example.sudas.cards.cards;
import com.example.sudas.matches.MatchesActivity;
import com.example.sudas.positions.Position;
import com.example.sudas.positions.positionCardAdapter;
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
    private positionCardAdapter positionAdapter;
    private int i;
    private FirebaseAuth mAuth;
    private String currentUserId, mUserType, mSelectedPositionId, mPositionId;
    ListView listView;
    List<cards> rowItems;
    List<Position> positionRowItems;

    private DatabaseReference usersDb, mDatabaseUserType;
    SwipeFlingAdapterView flingContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("positionId")) {
            mPositionId = getIntent().getExtras().getString("positionId");
        }

        usersDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rowItems = new ArrayList<cards>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        positionRowItems = new ArrayList<Position>();
        positionAdapter = new positionCardAdapter(this, R.layout.item_position_card, positionRowItems);
        checkUserPreferences();

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        mDatabaseUserType = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(currentUserId).child("userType");
        mDatabaseUserType.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mUserType = snapshot.getValue().toString();
                    switch (mUserType) {
                        case "talent":
                            flingContainer.setAdapter(positionAdapter);
                            break;
                        case "recruiter":
                            flingContainer.setAdapter(arrayAdapter);
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                switch (mUserType) {
                    case "talent":
                        positionRowItems.remove(0);
                        positionAdapter.notifyDataSetChanged();
                        break;
                    case "recruiter":
                        rowItems.remove(0);
                        arrayAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                switch (mUserType) {
                    case "talent":
                        Position posObj = (Position) dataObject;
                        String recruiterId = posObj.getRecruiterId();
                        String positionId = posObj.getPositionId();
                        mSelectedPositionId = positionId;
                        usersDb.child(recruiterId).child("connections").child(positionId).child("noInterest").child(currentUserId).setValue("true");
                        break;
                    case "recruiter":
                        cards obj = (cards) dataObject;
                        String userId = obj.getUserId();
                        usersDb.child(userId).child("connections").child("noInterest").child(currentUserId).setValue("true");
                        break;
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                switch (mUserType) {
                    case "talent":
                        Position posObj = (Position) dataObject;
                        String recruiterId = posObj.getRecruiterId();
                        String positionId = posObj.getPositionId();
                        usersDb.child(recruiterId).child("connections").child("interested").child(currentUserId).child(positionId).setValue("true");
                        isPositionMatch(recruiterId, positionId);
                        break;
                    case "recruiter":
                        cards obj = (cards) dataObject;
                        String userId = obj.getUserId();
                        usersDb.child(userId).child("connections").child("interested").child(currentUserId).child(mPositionId).setValue("true");
                        isMatch(userId);
                        break;
                }
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

    private void isPositionMatch(String recruiterId, String positionId) {
        DatabaseReference currentUserConnectionDb = usersDb.child(currentUserId).child("connections").child("interested").child(recruiterId).child(positionId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String key = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("chats").push().getKey();
                    usersDb.child(recruiterId).child("connections").child("matches").child(currentUserId).child("chatId").setValue(key);
                    usersDb.child(recruiterId).child("connections").child("matches").child(currentUserId).child("positionId").setValue(snapshot.getKey());
                    usersDb.child(currentUserId).child("connections").child("matches").child(recruiterId).child("chatId").setValue(key);
                    usersDb.child(currentUserId).child("connections").child("matches").child(recruiterId).child("positionId").setValue(snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUserId).child(mPositionId).setValue(snapshot.getKey());
                    usersDb.child(currentUserId).child("connections").child("matches").child(snapshot.getKey()).child("chatId").setValue(key);
                    usersDb.child(currentUserId).child("connections").child("matches").child(snapshot.getKey()).child(mPositionId).setValue(snapshot.getKey());

//                    usersDb.child(snapshot.getKey()).child("connections").child("matches").child(currentUserId).child("chatId").setValue(key);
//                    usersDb.child(currentUserId).child("connections").child("matches").child(snapshot.getKey()).child("chatId").setValue(key);
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


    private String userGender, userType;
    private String oppositeGender, oppositeUserType;

    public void checkUserPreferences() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference currentUserDb = usersDb.child(user.getUid());
        currentUserDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("userType").getValue() != null) {
                        userType = snapshot.child("userType").getValue().toString();
                        switch (userType) {
                            case "recruiter":
                                getFilteredTalents();
//                                oppositeUserType = "talent";
                                break;
                            case "talent":
                                getFilteredPositions();
//                                oppositeUserType = "recruiter";
                                break;
                        }
//                        getOppositeTypeUsers();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getFilteredTalents() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.exists()) &&
                        !snapshot.child("connections").child("match").hasChild(currentUserId) &&
                        !snapshot.child("connections").child("noMatch").hasChild(currentUserId) &&
                        snapshot.child("userType").getValue().toString().equals("talent")
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

    public void getFilteredPositions() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users");
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.exists()) &&
                        !snapshot.child("connections").child("match").hasChild(currentUserId) &&
                        !snapshot.child("connections").child("noMatch").hasChild(currentUserId) &&
                        snapshot.child("userType").getValue().toString().equals("recruiter")
                ) {
                    String profileImageUrl = "default";
                    if (!snapshot.child("profileImageUrl").getValue().equals("default")) {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }

                    DataSnapshot positions = snapshot.child("positions");
                    if (positions != null) {
                        for (DataSnapshot p: positions.getChildren()) {
//                        if (checkFilter(p)) {
                            Position item = new Position(p.child("title").getValue().toString(),
                                    p.child("location").getValue().toString(), p.child("recruiterId").getValue().toString(), p.getKey());
                            positionRowItems.add(item);
                            positionAdapter.notifyDataSetChanged();
//                        }
                        }
                    }
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
        intent.putExtra("selectedPositionId", mSelectedPositionId);
        startActivity(intent);
        return;
    }
}