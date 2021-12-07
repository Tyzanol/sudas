package com.example.sudas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.sudas.positions.PositionActivity;
import com.example.sudas.talents.TalentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecruiterOrTalent extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_or_talent);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userDb = FirebaseDatabase.getInstance(Globals.DBAddress).getReference().child("users").child(currentUserID);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    userDb.child("userType").setValue("recruiter");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void goToPositions(View view) {
        Intent intent = new Intent(RecruiterOrTalent.this, PositionActivity.class);
        Bundle b = new Bundle();
        b.putString("userType", "recruiter");
        intent.putExtras(b);
        startActivity(intent);
        finish();
        return;
    }

    public void gotToMain(View view) {
        Intent intent = new Intent(RecruiterOrTalent.this, TalentActivity.class);
        Bundle b = new Bundle();
        b.putString("userType", "talent");
        intent.putExtras(b);
        startActivity(intent);
        finish();
        return;
    }
}