package com.mychat.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;



public class KonusmaActivity extends AppCompatActivity {
    LayoutInflater layoutInflater;
    Adapter adapter;
    List<String> lk = new ArrayList<>();//keyleri tutuyor, mesaj silme işlemi için
    ArrayList<listItem> list = new ArrayList<>();

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    String aliciUid;
    String aliciName;
    String gonderenUid;

    ListView listView;
    EditText editMesaj;

    GelenKutusu gelenKutusu;

    //sifreleme degiskenler
    String plaintext = "";
    String enc = "";
    String dec = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konusma);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);
        editMesaj = findViewById(R.id.editMesaj);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        gonderenUid = firebaseAuth.getUid();

        aliciUid = getIntent().getExtras().getString("aliciUid");
        aliciName = getIntent().getExtras().getString("aliciName");

        adapter = new Adapter();
        listView.setAdapter(adapter);

        createChat();

        findViewById(R.id.btnGonder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // göndermeden önce sifreleme yapiliyor

                try {
                    enc = Encrypt.encrypt(editMesaj.getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // new Konusmalar(gelenKutusu.getKeyGelenKutusu(), gonderenUid, editMesaj.getText().toString()), normali bu
                databaseReference.child(Child.CHATS).push().setValue(
                        new Konusmalar(gelenKutusu.getKeyGelenKutusu(), gonderenUid, enc),
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                final String mesajKey = ref.getKey();
                                databaseReference.child(Child.CHAT_LAST).orderByChild("keyGelenKutusu").equalTo(gelenKutusu.getKeyGelenKutusu())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                    databaseReference.child(Child.CHAT_LAST).child(snapshot1.getKey()).child("mesajKey").setValue(mesajKey);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                databaseReference.child(Child.CHAT_INBOX).orderByChild("keyGelenKutusu")
                                        .equalTo(gelenKutusu.getKeyGelenKutusu()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            if (aliciUid.equals(snapshot1.child("gonderenUid").getValue().toString())) {
                                                databaseReference.child(Child.CHAT_INBOX).child(snapshot1.getKey()).child("okundu").setValue("1");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                );
                editMesaj.setText("");
            }
        });
        removeMessage();

    }

    void createChat() {
        databaseReference.child(Child.CHAT_INBOX).orderByChild("gonderenUid").equalTo(gonderenUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            if (snapshot1.getValue(GelenKutusu.class).getAliciUid().equals(aliciUid)) {
                                gelenKutusu = snapshot1.getValue(GelenKutusu.class);
                            }
                        }
                        chatInboxAndChatLast();
                        chats();
                        chatLast();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    void chatInboxAndChatLast() {
        if (gelenKutusu == null) {
            //gelen kutusu oluşturma
            String key = databaseReference.push().getKey();
            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new GelenKutusu(key, gonderenUid, aliciUid, "0"));
            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new GelenKutusu(key, aliciUid, gonderenUid, "0"));
            gelenKutusu = new GelenKutusu(key, gonderenUid, aliciUid, "0");

            //Son mesaj oluşturma
            databaseReference.child(Child.CHAT_LAST).push().setValue(
                    new SonMesaj(key, ""));
        }

    }

    void removeMessage() {
        // Gönderilen Mesajı silmek için tıklama
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                databaseReference.child(Child.CHATS).orderByChild("keyGelenKutusu").equalTo(gelenKutusu.getKeyGelenKutusu())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    lk.add(snapshot1.getKey().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                AlertDialog.Builder builder = new AlertDialog.Builder(KonusmaActivity.this);
                builder.setTitle("Mesaj Silme İşlemi!");
                builder.setMessage("Bu mesajı silmek istiyor musunuz?");
                builder.setCancelable(false);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!lk.isEmpty()) {
                            databaseReference.child(Child.CHATS).child(lk.get(position).toString()).removeValue();
                            lk.remove(position);
                            list.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(KonusmaActivity.this, "Mesaj silindi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(KonusmaActivity.this, "İşlem iptal edildi!", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                lk.clear();
            }

        });
    }

    void chats() {
        databaseReference.child(Child.CHATS).orderByChild("keyGelenKutusu").equalTo(gelenKutusu.getKeyGelenKutusu())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Konusmalar konusmalar = snapshot1.getValue(Konusmalar.class);
                            //şifre çözülerek kullanıcıya sunulur
                            try {
                                dec = Encrypt.decrypt(konusmalar.getMesaj().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //list.add(new listItem(konusmalar.getGonderenUid(),konusmalar.getMesaj())); //orijinal bu
                            list.add(new listItem(konusmalar.getGonderenUid(), dec));
                            lk.add(snapshot1.getKey().toString());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    void chatLast() {
        databaseReference.child(Child.CHAT_LAST).orderByChild("keyGelenKutusu")
                .equalTo(gelenKutusu.getKeyGelenKutusu()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                databaseReference.child(Child.CHATS).child(snapshot.child("mesajKey").getValue().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Konusmalar konusmalar = snapshot.getValue(Konusmalar.class);
                                list.add(new listItem(konusmalar.getGonderenUid(), konusmalar.getMesaj()));
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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


    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.control_row_item_konusma, null);
            }
            TextView txtMesaj = view.findViewById(R.id.txtMesaj);
            txtMesaj.setText(list.get(position).getMesaj());
            LinearLayout linearLayout = view.findViewById(R.id.linearTalk);
            LinearLayout linearLayout2 = view.findViewById(R.id.linearRow);
            if (list.get(position).getGonderenUid().equals(gonderenUid)) {
                linearLayout.setBackgroundResource(R.drawable.draw_talk_ben);
                linearLayout2.setGravity(Gravity.RIGHT);

            } else {
                linearLayout.setBackgroundResource(R.drawable.draw_talk_o);
                linearLayout2.setGravity(Gravity.LEFT);


            }


            return view;
        }
    }

    class listItem {
        String gonderenUid;
        String mesaj;

        public listItem(String gonderenUid, String mesaj) {
            this.gonderenUid = gonderenUid;
            this.mesaj = mesaj;
        }

        public String getGonderenUid() {
            return gonderenUid;
        }

        public String getMesaj() {
            return mesaj;
        }
    }
}
