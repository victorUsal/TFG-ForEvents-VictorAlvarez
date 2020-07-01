package com.victor.forevents.fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.R;
import com.victor.forevents.adapter.EventAdapter;
import com.victor.forevents.adapter.RecommendationAdapter;
import com.victor.forevents.model.Event;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;




public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    private RecyclerView recyclerViewRecommendation;
    private RecommendationAdapter recommendationAdapter;
    private List<Event> eventsRecommendation;

    private RecyclerView recyclerViewTendencias;
    private RecommendationAdapter recommendationAdapterT;
    private List<Event> eventsRecommendationT;

    private List <String> myRecommendations;
    private HashMap<String, Long> mapaTendencias;
    private List<Event> listaTendencias;
    private List<String> idTendencias;

    private List <String> followingList;
    FirebaseUser firebaseUser;


    ImageButton imageButton;
    DrawerLayout drawer;
    NavigationView navigationView;
    ProgressBar progressBar;
    LinearLayout eventos_seguidores,eventos_recomendados,eventos_tendencias;
    TextView no_events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        imageButton = (ImageButton)view.findViewById(R.id.ic_menu);
        drawer = (DrawerLayout)getActivity().findViewById(R.id.drawer);
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);
        eventos_seguidores = view.findViewById(R.id.eventos_seguidores);
        eventos_recomendados= view.findViewById(R.id.eventos_recomendados);
        eventos_tendencias = view.findViewById(R.id.eventos_tendencias);
        no_events = view.findViewById(R.id.no_events);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navigationView);

            }
        });

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext() , eventList, "home");
        recyclerView.setAdapter(eventAdapter);


        recyclerViewRecommendation = view.findViewById(R.id.recycler_view_recommendations);
        recyclerViewRecommendation.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerRec = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecommendation.setLayoutManager(linearLayoutManagerRec);
        eventsRecommendation = new ArrayList<>();
        recommendationAdapter = new RecommendationAdapter(getContext(), eventsRecommendation);
        recyclerViewRecommendation.setAdapter(recommendationAdapter);


        recyclerViewTendencias = view.findViewById(R.id.recycler_view_tendencias);
        recyclerViewTendencias.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerT = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTendencias.setLayoutManager(linearLayoutManagerT);
        eventsRecommendationT = new ArrayList<>();
        recommendationAdapterT = new RecommendationAdapter(getContext(), eventsRecommendationT);
        recyclerViewTendencias.setAdapter(recommendationAdapterT);


        progressBar = view.findViewById(R.id.progress_circular);

        checkFollowing();
        myRecommendations();
        numLikes();

        return view ;

    }

    private void myRecommendations() {
        myRecommendations = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recommendations").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRecommendations.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    myRecommendations.add(snapshot.getKey());
                }
                readRecommendation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readRecommendation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsRecommendation.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    for(String id : myRecommendations){
                        if(event.getPostid().equals(id)){
                            eventsRecommendation.add(event);
                        }
                    }
                }

                if(eventsRecommendation.isEmpty()){
                    eventos_recomendados.setVisibility(View.GONE);
                }else{
                    eventos_recomendados.setVisibility(View.VISIBLE);
                }

                recommendationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readEvent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void numLikes(){
        mapaTendencias = new HashMap<>();
        listaTendencias = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mapaTendencias.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   mapaTendencias.put(snapshot.getKey(), snapshot.getChildrenCount());
                }

                readMaxLikes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readMaxLikes() {
        idTendencias = new ArrayList<>();
        idTendencias.clear();
       for(int icinco = 0; icinco<5 ;icinco++) {
           long max = 0;
           String idMax = "";
           for (String i : mapaTendencias.keySet()) {
               if (mapaTendencias.get(i) > max) {
                   max = mapaTendencias.get(i);
                   idMax = i;
               }
           }
           mapaTendencias.remove(idMax);
           idTendencias.add(idMax);
       }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsRecommendationT.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);

                    for(String id : idTendencias){
                        if(event.getPostid().equals(id)){
                            eventsRecommendationT.add(event);
                        }
                    }
                }

                if(eventsRecommendationT.isEmpty()){
                    eventos_tendencias.setVisibility(View.GONE);
                }else{
                    eventos_tendencias .setVisibility(View.VISIBLE);
                }

                recommendationAdapterT.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void readEvent(){
        Query reference = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("fechaInicio");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventList.clear();
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    for(String id : followingList){
                        try {
                            if((event.getPublisher().equals(id) || event.getPublisher().equals(firebaseUser.getUid())) && compararFechas(event.getFechaInicio())){
                                eventList.add(event);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

                eventAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(eventList.isEmpty()){
                    eventos_seguidores.setVisibility(View.GONE);
                    no_events.setVisibility(View.VISIBLE);
                }else{
                    eventos_seguidores.setVisibility(View.VISIBLE);
                    no_events.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private boolean compararFechas(String fechaInicio) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        final String fecha = dateFormat.format(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date inicio = sdf.parse(fechaInicio);
        Date actual = sdf.parse(fecha);

        if(actual.compareTo(inicio)<= 0){
            return true;
        }else{
            return false;
        }

    }

}
