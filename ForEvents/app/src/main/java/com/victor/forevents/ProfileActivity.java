package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.adapter.MyEventsAdapter;
import com.victor.forevents.model.Event;
import com.victor.forevents.model.Tokens;
import com.victor.forevents.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    ImageView image_profile;
    TextView events, followers, following, fullname, bio, username, events_empty,events_empty_attend;
    Button edit_profile;
    ImageButton back;

    public TabLayout tabLayout;
    TabItem my_events,events_attended;

    private List<String> myAttended;

    RecyclerView recyclerView;
    MyEventsAdapter myEventsAdapter;
    List<Event> eventList;

    RecyclerView recyclerView_attended;
    MyEventsAdapter myEventsAdapter_attended;
    List<Event> eventList_attended;

    String mtoken = "";
    public String muser= "";

    FirebaseUser firebaseUser;
    String profileid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = findViewById(R.id.image_profile);
        events = findViewById(R.id.events);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        fullname = findViewById(R.id.fullname);
        bio = findViewById(R.id.bio);
        username =findViewById(R.id.username);
        edit_profile = findViewById(R.id.edit_profile);
        my_events = findViewById(R.id.my_events);
        events_attended = findViewById(R.id.events_attended);
        back = findViewById(R.id.ic_back);
        events_empty = findViewById(R.id.events_empty);
        events_empty_attend = findViewById(R.id.events_empty_attend);

        tabLayout = findViewById(R.id.tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerView_attended.setVisibility(View.GONE);
                        if(eventList.isEmpty()){
                            events_empty.setVisibility(View.VISIBLE);
                        }else{
                            events_empty.setVisibility(View.GONE);
                        }
                        events_empty_attend.setVisibility(View.GONE);
                        break;
                    case 1:
                        recyclerView.setVisibility(View.GONE);
                        recyclerView_attended.setVisibility(View.VISIBLE);
                        if(eventList_attended.isEmpty()){
                            events_empty_attend.setVisibility(View.VISIBLE);
                        }else{
                            events_empty_attend.setVisibility(View.GONE);
                        }
                        events_empty.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventList = new ArrayList<>();
        myEventsAdapter = new MyEventsAdapter(this, eventList);
        recyclerView.setAdapter(myEventsAdapter);

        recyclerView_attended = findViewById(R.id.recycler_view_attended);
        recyclerView_attended.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_attended= new GridLayoutManager(this, 2);
        recyclerView_attended.setLayoutManager(linearLayoutManager_attended);
        eventList_attended = new ArrayList<>();
        myEventsAdapter_attended = new MyEventsAdapter(this, eventList_attended);
        recyclerView_attended.setAdapter(myEventsAdapter_attended);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_attended.setVisibility(View.GONE);


        userInfo();
        getFollowers();
        getNumEvents();
        myEvents();
        myEventsAttended();
        nombreNotification();
        myToken();

        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Editar perfil");
        }else if(profileid.equals("eWXNrrAlqsaW7jmjq4V3SnyU74N2") || profileid.equals("MH9jqhjZszNSZtepkRKZNQjd3Of1")){
            edit_profile.setVisibility(View.GONE);
        }else{
            checkFollow();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if(btn.equals("Editar perfil")){
                    startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
                }else if (btn.equals("Seguir")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileid).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(firebaseUser.getUid()).setValue(true);

                addNotifications();
                }else if(btn.equals("Siguiendo")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following").child(profileid).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Seguidores");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Siguiendo");
                startActivity(intent);
            }
        });



    }


    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(this == null){
                    return;
                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getApplicationContext()).load(user.getImagen()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void addNotifications(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "Te ha comenzado a seguir");
        hashMap.put("postid", "");
        hashMap.put("tag", "seguir");
        hashMap.put("isPost", false);

        llamarEspecifico(muser, "Te ha comenzado a seguir");
        reference.push().setValue(hashMap);
    }

    private void checkFollow(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("Siguiendo");
                }else{
                    edit_profile.setText("Seguir");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getFollowers(){
        DatabaseReference referenceFollowers = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("followers");
        referenceFollowers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference referenceFollowing = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid).child("following");
        referenceFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getNumEvents(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    if(event.getPublisher().equals(profileid)){
                        i++;
                    }
                }

                events.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void myEvents() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    if(event.getPublisher().equals(profileid)){
                        eventList.add(event);
                    }
                }
                if(eventList.isEmpty()){
                    events_empty.setVisibility(View.VISIBLE);
                }else{
                    events_empty.setVisibility(View.GONE);
                }
                events_empty_attend.setVisibility(View.GONE);


                Collections.reverse(eventList);
                myEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void myEventsAttended(){
        myAttended = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attends").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myAttended.add(snapshot.getKey());
                }

                readAttended();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readAttended(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList_attended.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);

                    for(String id : myAttended){
                        if(event.getPostid().equals(id) && event.getTipo().equals("Público")){
                            eventList_attended.add(event);
                        }
                    }
                }


                myEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myToken(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tokens").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tokens token = dataSnapshot.getValue(Tokens.class);
                mtoken = token.getToken();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void nombreNotification(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(this == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                muser = user.getUsername();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void llamarEspecifico(String titulo, String detalle){
        RequestQueue myrequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        try{
            json.put("to",mtoken);
            JSONObject notificacion = new JSONObject();
            notificacion.put("titulo",titulo);
            notificacion.put("detalle", detalle);

            json.put("data", notificacion);

            String URL = "https://fcm.googleapis.com/fcm/send";

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL,json,null,null){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> header = new HashMap<>();

                    header.put("content-type", "application/json");
                    header.put("authorization", "key= AAAA3xj18jc:APA91bEVaKYf4hLZVjdFdl8mOq2tysW7afzZKFET5WMnjZ11FEUy8KytjVntlJE4kLSknq05vWLzyncxLiw8Xi0bMeq0xHpvgpi5-r-tF24h06iRWbHv7iZ9TtezNR-7jjRgcY5wGPGC");
                    return header;
                }
            };

            myrequest.add(request);


        }catch (JSONException e){
            e.printStackTrace();
        }

    }





}
