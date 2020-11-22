package com.mychat.chat;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {

    LayoutInflater layoutInflater;
    ArrayList<ListItem> list = new ArrayList<>();
    Adapter adapter;
    ListView listView;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        //verileri okuma kısmı
        databaseReference.child(Child.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    UserInfo info = snapshot1.getValue(UserInfo.class);
                   if(!info.getUid().equals(firebaseAuth.getUid())){
                       list.add(new ListItem(info.getUid(),info.getAd(),info.getUrlPp()));
                   }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HomeActivity.this,KonusmaActivity.class);
                intent.putExtra("aliciUid",list.get(position).getUid());
                intent.putExtra("aliciName",list.get(position).getAd());
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navBtnCikis){
                    FirebaseAuth.getInstance().signOut();//oturumu kapat
                    startActivity(new Intent(HomeActivity.this,GirisYapActivity.class));
                    finish();
                }
                else if(item.getItemId() == R.id.navBtnProfil){
                    startActivity(new Intent(HomeActivity.this,ProfilActivity.class));

                }
                else if(item.getItemId() == R.id.navBtnInbox){
                    startActivity(new Intent(HomeActivity.this,GelenKutusuActivity.class));


                }
                return false;
            }
        });
    }

    class Adapter extends BaseAdapter{

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
            if(convertView == null){
                view = layoutInflater.inflate(R.layout.control_row_item_profil,null);
            }
            TextView textName = view.findViewById(R.id.txtName);
            CircleImageView circleImageView = view.findViewById(R.id.circleImageView);

            textName.setText(list.get(position).getAd());
            Helper.imageLoad(HomeActivity.this,list.get(position).getUrlProfil(),circleImageView);

            return view;
        }
    }


    class ListItem{
        String uid;
        String ad;
        String urlProfil;

        public ListItem(String uid, String ad, String urlProfil) {
            this.uid = uid;
            this.ad = ad;
            this.urlProfil = urlProfil;
        }

        public String getUid() {
            return uid;
        }

        public String getAd() {
            return ad;
        }

        public String getUrlProfil() {
            return urlProfil;
        }
    }


}
