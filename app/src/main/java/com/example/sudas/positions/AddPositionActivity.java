package com.example.sudas.positions;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sudas.R;
import com.example.sudas.Globals;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddPositionActivity extends AppCompatActivity {

    Button mAddPosition;

    private EditText mTitle, mLocation;
    private FirebaseAuth mAuth;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_position);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAddPosition = (Button) findViewById(R.id.addNewPosition);
        mTitle = (EditText) findViewById(R.id.title);
        mLocation = (EditText) findViewById(R.id.location);

        mAddPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String title = mTitle.getText().toString();
                final String location = mLocation.getText().toString();

                String userId =  mAuth.getCurrentUser().getUid();
                DatabaseReference currentUserPositions = FirebaseDatabase.getInstance(Globals.DBAddress).getReference().child("users").child(userId).child("positions");
                DatabaseReference newPositionDb = currentUserPositions.push();
                Map newPosition = new HashMap();
                newPosition.put("title", title);
                newPosition.put("location", location);
                newPosition.put("recruiterId", currentUserID);
                newPositionDb.setValue(newPosition);

                finish();
            }
        });
    }
}
