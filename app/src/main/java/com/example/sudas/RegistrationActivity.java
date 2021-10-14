package com.example.sudas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    Button mRegister;

    private EditText mEmail, mPassword, mName;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private RadioGroup mRadioGroup, mRadioGroupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
            }
        };

        mRegister = (Button) findViewById(R.id.register);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroupType = (RadioGroup) findViewById(R.id.radioGroupType);
        mName = (EditText) findViewById(R.id.name);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectedId);
                if (radioButton.getText() == null) {
                    return;
                }

                int selectedTypeId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButtonType = (RadioButton) findViewById(selectedId);
                if (radioButton.getText() == null) {
                    return;
                }


                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                Toast.makeText(RegistrationActivity.this, "email: " + email, Toast.LENGTH_LONG);

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        } else {
                            String userId =  mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance("https://sudas-1b8ee-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(userId);
                            Map userInfo = new HashMap();
                            userInfo.put("name", name);
                            userInfo.put("gender", radioButton.getText().toString());
                            userInfo.put("type", radioButtonType.getText().toString());
                            userInfo.put("profileImageUrl", "default");
                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}