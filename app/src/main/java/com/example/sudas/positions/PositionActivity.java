package com.example.sudas.positions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.sudas.AddPositionActivity;
import com.example.sudas.Globals;
import com.example.sudas.MainActivity;
import com.example.sudas.R;
import com.example.sudas.SettingsActivity;
import com.example.sudas.matches.Matches;
import com.example.sudas.matches.MatchesActivity;
import com.example.sudas.matches.MatchesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PositionActivity extends AppCompatActivity {

    private Button mAddPositionButton;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mPositionAdapter;
    private RecyclerView.LayoutManager mPositionLayoutManager;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        continueToMain();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mPositionLayoutManager = new LinearLayoutManager(PositionActivity.this);
        mRecyclerView.setLayoutManager(mPositionLayoutManager);
        mPositionAdapter = new PositionsAdapter(getPositions(), PositionActivity.this);
        mRecyclerView.setAdapter(mPositionAdapter);
//        FetchPositionInformation();

        mAddPositionButton = (Button) findViewById(R.id.addPosition);

        mAddPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PositionActivity.this, AddPositionActivity.class);
                startActivity(intent);

            }
        });
    }

    private void continueToMain() {
        DatabaseReference userDb = FirebaseDatabase.getInstance(Globals.DBAddress).getReference().child("users").child(currentUserID);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("userType").getValue().toString().equals("talent")) {
                        Intent intent = new Intent(PositionActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void FetchPositionInformation() {
        DatabaseReference positionsDb = FirebaseDatabase.getInstance(Globals.DBAddress).getReference().child("users").child(currentUserID).child("positions");
        positionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot position : dataSnapshot.getChildren()) {
                        String positionId = position.getKey();
                        String title = "";
                        String location = "";
                        if(position.child("title").getValue()!=null){
                            title = position.child("title").getValue().toString();
                        }
                        if(position.child("location").getValue()!=null){
                            location = position.child("location").getValue().toString();
                        }

                        Position obj = new Position(title, location, currentUserID, positionId);
                        resultPositions.add(obj);
                        mPositionAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<Position> resultPositions = new ArrayList<Position>();
    private List<Position> getPositions() {
        return resultPositions;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultPositions.clear();
        FetchPositionInformation();
    }
}