package com.mychat.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class GirisYapActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText editEmail, editSifre;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_yap);

        firebaseAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editMailGiris);
        editSifre = findViewById(R.id.editSifre);
        progressBar = findViewById(R.id.progressBar2);

        //giriş yap butonu
        findViewById(R.id.btnGiris).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(editEmail.getText().toString().isEmpty() || editSifre.getText().toString().isEmpty()){
                    Toast.makeText(getBaseContext(),"Lütfen tüm alanları doldurunuz!",Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = editEmail.getText().toString();
                String sifre = editSifre.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                firebaseAuth.signInWithEmailAndPassword(email,sifre)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(GirisYapActivity.this,HomeActivity.class));
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(),"Giriş Başarısız!",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });

            }
        });

        //kayıt ol butonu
        findViewById(R.id.btnKayit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisYapActivity.this,KayitOlActivity.class));
            }
        });
    }
}
