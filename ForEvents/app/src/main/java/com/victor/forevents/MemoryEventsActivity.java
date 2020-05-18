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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoryEventsActivity extends AppCompatActivity {

    private List <String> myMemories;

    RecyclerView recyclerViewSaves;
    EventAdapter eventAdapter;
    List<Event> eventList;

    FirebaseUser firebaseUser;
    ImageButton back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_events);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewSaves = findViewById(R.id.recycler_view);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerMemories = new LinearLayoutManager(this);
        linearLayoutManagerMemories.setReverseLayout(true);
        linearLayoutManagerMemories.setStackFromEnd(true);
        recyclerViewSaves.setLayoutManager(linearLayoutManagerMemories);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this , eventList, "memories");
        recyclerViewSaves.setAdapter(eventAdapter);
        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        myMemories();



    }


    private void myMemories(){
        myMemories = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attends").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myMemories.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myMemories.add(snapshot.getKey());
                }

                readMemories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMemories(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    for(String id : myMemories){
                        try {
                            if(event.getPostid().equals(id) && event.getTipo().equals("Privado") && compararFechas(event.getFechaInicio())){
                                eventList.add(event);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
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


    private boolean compararFechas(String fechaInicio) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = new Date();
        final String fecha = dateFormat.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date evento = sdf.parse(fechaInicio);
        Date actual = sdf.parse(fecha);

        if(actual.compareTo(evento)>= 0){
            return true;
        }else{
            return false;
        }

    }




}




