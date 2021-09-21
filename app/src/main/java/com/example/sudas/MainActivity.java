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
    private arrayAdapter arrayAdapter;
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
        checkUserGender();

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
                usersDb.child(oppositeGender).child(userId).child("connections").child("noInterest").child(currentUserId).setValue("true");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userId = obj.getUserId();
                usersDb.child(oppositeGender).child(userId).child("connections").child("interested").child(currentUserId).setValue("true");
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
        DatabaseReference currentUserConnectionDb = usersDb.child(userGender).child(currentUserId).child("connections").child("interested").child(userId);
        currentUserConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usersDb.child(oppositeGender).child(snapshot.getKey()).child("connections").child("matches").child(currentUserId).setValue("true");
                    usersDb.child(userGender).child(currentUserId).child("connections").child("matches").child(snapshot.getKey()).setValue("true");
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

    public void checkUserGender() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference maleDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child("male");
        maleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.getKey().equals(user.getUid()))) {
                    userGender = "male";
                    oppositeGender = "female";
                    getOppositeGenderUsers();
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
        DatabaseReference femaleDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child("female");
        femaleDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.getKey().equals(user.getUid()))) {
                    userGender = "female";
                    oppositeGender = "male";
                    getOppositeGenderUsers();
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

    public void getOppositeGenderUsers() {
        DatabaseReference oppositeGenderDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(oppositeGender);
        oppositeGenderDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if ((snapshot.exists()) &&
                        !snapshot.child("connections").child("match").hasChild(currentUserId) &&
                        !snapshot.child("connections").child("noMatch").hasChild(currentUserId)
                ) {
                    cards item = new cards(snapshot.getKey(), snapshot.child("name").getValue().toString());
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
        startActivity(intent);
        return;
    }
}