package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.adapter.UserAdapter;
import com.victor.forevents.model.User;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    TextView nameActivity;
    ImageButton imageButton;
    String name = "";
    String id;
    String title;

    List <String> idList;

    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<User> userList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        Intent intent =getIntent();

        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        nameActivity = findViewById(R.id.name);
        imageButton = findViewById(R.id.ic_back);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();


        switch (title){
            case "Seguidores":

                getFollowers();
                break;
            case "Siguiendo":

                getFollowing();
                break;
            case "Asistentes":

                getParticipants();
                break;
        }

    }

    private void getParticipants() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes").child(id);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showUsers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               userList.clear();
               for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                   User user = snapshot.getValue(User.class);
                   for(String id : idList) {
                       if (user.getId().equals(id)) {
                           userList.add(user);
                       }
                   }
               }
               userAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
