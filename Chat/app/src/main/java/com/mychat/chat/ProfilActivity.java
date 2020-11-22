package com.mychat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    CircleImageView circleImageView;
    TextView textName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        circleImageView = findViewById(R.id.circleImageView);
        textName = findViewById(R.id.textName);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Child.users).orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot snapshot1:snapshot.getChildren()){
                            textName.setText(snapshot1.child("ad").getValue().toString());
                            Helper.imageLoad(ProfilActivity.this,snapshot1.child("urlPp").getValue().toString(),circleImageView);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
