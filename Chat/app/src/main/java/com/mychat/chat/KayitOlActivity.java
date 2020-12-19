package com.mychat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class KayitOlActivity extends AppCompatActivity {

    EditText editAd, editEmail, editSifre, editSifreTekrar;
    ProgressBar progressBar;
    CircleImageView imgPp;
    Uri uriPp;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        editAd = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editSifre = findViewById(R.id.editSifreKaydol);
        editSifreTekrar = findViewById(R.id.editSifreTekrar);
        imgPp = findViewById(R.id.imgPp);
        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //dosyaya erişme izni bazı cihazlar(android 6.0) için. Diğerleri için manifeste aldık
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }

        }

        //fotograf tıklama
        imgPp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 100);
            }
        });

        //kayıt ol butonu
        findViewById(R.id.btnUyeOl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (uriPp ==null
                        ||editAd.getText().toString().isEmpty()
                        || editEmail.getText().toString().isEmpty()
                        || editSifre.getText().toString().isEmpty()
                        || editSifreTekrar.getText().toString().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(editSifre.getText().toString().equals(editSifreTekrar.getText().toString()))) {
                    Toast.makeText(getBaseContext(), "Şifre tekrarları uyuşmuyor!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = editEmail.getText().toString();
                String sifre = editSifre.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.createUserWithEmailAndPassword(email, sifre)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            //üyelik başarılı
                            public void onSuccess(AuthResult authResult) {
                                setUserInfo();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    //üyelik başarısız
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(),"Üyelik Başarısız!",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);

                    }
                });

            }
        });
    }

    void setUserInfo() {
        UUID uuid = UUID.randomUUID();
        final String path = "image/" + uuid + ".jpg";
        storageReference.child(path).putFile(uriPp)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                storageReference.child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String uid = firebaseAuth.getUid();
                        String ad = editAd.getText().toString();
                        String url = uri.toString();
                        databaseReference.child(Child.users).push().setValue(
                                new UserInfo(uid, ad, url)
                        );
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(KayitOlActivity.this,HomeActivity.class));
                        finish();
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 100 && data != null) {
            uriPp = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriPp);
                imgPp.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
