package com.victor.forevents.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.victor.forevents.R;
import com.victor.forevents.adapter.CategoryAdapter;
import com.victor.forevents.adapter.EventAdapter;
import com.victor.forevents.adapter.UserAdapter;
import com.victor.forevents.model.Event;
import com.victor.forevents.model.User;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment implements CategoryAdapter.OnCategoryListener{

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> users;

    private RecyclerView recyclerViewEvent;
    private EventAdapter eventAdapter;
    private List<Event> events;


    private RecyclerView recyclerViewCategory;
    private CategoryAdapter categoryAdapter;
    private List<String> categories;
    String categoria ="";


    TabLayout tabLayout;

    ImageButton imageButton,ic_filter;
    DrawerLayout drawer;
    NavigationView navigationView;

    Dialog epicDialog;
    Button aceptar, cancelar;

    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        imageButton = (ImageButton) view.findViewById(R.id.ic_menu);
        drawer = (DrawerLayout)getActivity().findViewById(R.id.drawer);
        navigationView = (NavigationView)getActivity().findViewById(R.id.nav_view);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navigationView);
            }
        });

        epicDialog = new Dialog(getContext());

        ic_filter = (ImageButton) view.findViewById(R.id.ic_filter);

        ic_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        search_bar = view.findViewById(R.id.search_bar);

        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), users);
        recyclerView.setAdapter(userAdapter);

        recyclerViewEvent = view.findViewById(R.id.recycler_view_event);
        recyclerViewEvent.setHasFixedSize(true);
        recyclerViewEvent.setLayoutManager(new LinearLayoutManager(getContext()));

        events = new ArrayList<>();
        eventAdapter = new EventAdapter(getContext(), events, "search");
        recyclerViewEvent.setAdapter(eventAdapter);


        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewEvent.setVisibility(View.GONE);
        ic_filter.setVisibility(View.GONE);

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable et) {

            }
        });


        readUsers();
        readEvents();


        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerViewEvent.setVisibility(View.GONE);
                        ic_filter.setVisibility(View.GONE);

                        search_bar.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                searchUsers(s.toString().toLowerCase());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {


                            }
                        });


                        break;
                    case 1:
                        readEvents();
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewEvent.setVisibility(View.VISIBLE);
                        ic_filter.setVisibility(View.VISIBLE);

                        search_bar.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                                searchEvents(s.toString());
                            }

                            @Override
                            public void afterTextChanged(Editable et) {

                            }
                        });

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

        return view;
    }

    private void showPopUp() {

        epicDialog.setContentView(R.layout.filter_popup);
        aceptar = (Button) epicDialog.findViewById(R.id.ok_filter);
        cancelar = (Button) epicDialog.findViewById(R.id.cancel_filter);



        recyclerViewCategory = epicDialog.findViewById(R.id.recycler_category);
        recyclerViewCategory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewCategory.setLayoutManager(linearLayoutManager);

        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(),categories,this);
        recyclerViewCategory.setAdapter(categoryAdapter);
        readCategory();


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!categoria.equals("")) {
                    searchEventsCategory(categoria);
                }
                Toast.makeText(getContext(),"Filtros Aceptados",Toast.LENGTH_SHORT).show();
                epicDialog.dismiss();
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                epicDialog.dismiss();
            }
        });

         epicDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         epicDialog.show();
    }


    private void searchUsers(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener((new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }


                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

    }

    private void searchEventsCategory(final String categoria){
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("tematica").equalTo(categoria);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    events.add(event);
                }

                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void searchEvents(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("nombre").startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Event event = snapshot.getValue(Event.class);
                    events.add(event);
                }

                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readUsers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                    users.clear();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User user = snapshot.getValue(User.class);
                        users.add(user);
                    }

                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readEvents(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                    events.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Event event = snapshot.getValue(Event.class);
                        events.add(event);
                    }

                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readCategory(){

        categories.clear();
        categories.add("Acádemico");
        categories.add("Comunitario");
        categories.add("Cultural");
        categories.add("Empresarial");
        categories.add("Ocio");
        categories.add("Politico");
        categories.add("Social");

        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCategoryClick(int position) {
            if(position == 0){
                categoria = "Acádemico";
            }else if(position == 1){
                categoria = "Comunitario";
            }else if(position == 2){
                categoria = "Cultural";
            }else if(position == 3){
                categoria = "Empresarial";
            }else if(position == 4){
                categoria = "Ocio";
            }else if(position == 5){
                categoria = "Politico";
            }else if(position == 6){
                categoria = "Social";
            }

            categories.clear();
            categories.add(categoria);
            categoryAdapter.notifyDataSetChanged();


        //searchEventsCategory(position);

    }



}
