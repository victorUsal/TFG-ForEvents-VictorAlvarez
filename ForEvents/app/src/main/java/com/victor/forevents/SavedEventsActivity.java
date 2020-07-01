package com.victor.forevents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.adapter.EventAdapter;
import com.victor.forevents.model.Event;

import java.util.ArrayList;
import java.util.List;

public class SavedEventsActivity extends AppCompatActivity {

    private List <String> mySaves;

    RecyclerView recyclerViewSaves;
    EventAdapter eventAdapter;
    List<Event> eventList;

    ImageButton back;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewSaves = findViewById(R.id.recycler_view);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerSaves = new LinearLayoutManager(this);
        linearLayoutManagerSaves.setReverseLayout(true);
        linearLayoutManagerSaves.setStackFromEnd(true);
        recyclerViewSaves.setLayoutManager(linearLayoutManagerSaves);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this , eventList, "save");
        recyclerViewSaves.setAdapter(eventAdapter);

        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mySaves();

    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mySaves.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());

                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    for(String id : mySaves){
                        if(event.getPostid().equals(id)){
                            eventList.add(event);
                        }
                    }
                }

                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
